package com.bps.publikasistatistik.service;

import com.bps.publikasistatistik.dto.PublicationRequest;
import com.bps.publikasistatistik.dto.PublicationResponse;
import com.bps.publikasistatistik.entity.Category;
import com.bps.publikasistatistik.entity.Publication;
import com.bps.publikasistatistik.entity.User;
import com.bps.publikasistatistik.repository.CategoryRepository;
import com.bps.publikasistatistik.repository.PublicationRepository;
import com.bps.publikasistatistik.repository.UserRepository;
import com.bps.publikasistatistik.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public List<PublicationResponse> getAllPublications() {
        return publicationRepository.findAll().stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<PublicationResponse> searchPublications(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPublications();
        }

        return publicationRepository.searchByKeyword(keyword).stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<PublicationResponse> getPublicationsByCategory(Long categoryId) {
        return publicationRepository.findByCategoryId(categoryId).stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<PublicationResponse> getPublicationsByYear(Integer year) {
        return publicationRepository.findByYear(year).stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<PublicationResponse> getPublicationsByCategoryAndYear(Long categoryId, Integer year) {
        return publicationRepository.findByCategoryIdAndYear(categoryId, year).stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<PublicationResponse> getLatestPublications() {
        return publicationRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<PublicationResponse> getMostDownloaded() {
        return publicationRepository.findTop10ByOrderByDownloadsDesc().stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public PublicationResponse getPublicationById(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));

        // Increment views
        publication.setViews(publication.getViews() + 1);
        publicationRepository.save(publication);

        return new PublicationResponse(publication);
    }

    @Transactional
    public PublicationResponse uploadPublication(
            PublicationRequest request,
            MultipartFile file,
            CustomUserDetails userDetails) {

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        // Validate file type (PDF, Excel)
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new RuntimeException("Invalid file type. Only PDF and Excel files are allowed");
        }

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Get user
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store file
        String fileName = fileStorageService.storeFile(file);

        // Create publication
        Publication publication = new Publication();
        publication.setTitle(request.getTitle());
        publication.setDescription(request.getDescription());
        publication.setFileName(fileName);
        publication.setFilePath(fileStorageService.loadFile(fileName).toString());
        publication.setFileSize(file.getSize());
        publication.setYear(request.getYear());
        publication.setAuthor(request.getAuthor());
        publication.setCategory(category);
        publication.setUploadedBy(user);
        publication.setViews(0);
        publication.setDownloads(0);

        Publication savedPublication = publicationRepository.save(publication);
        log.info("Publication uploaded: {} by {}", savedPublication.getTitle(), user.getEmail());

        return new PublicationResponse(savedPublication);
    }

    @Transactional
    public PublicationResponse updatePublication(Long id, PublicationRequest request, CustomUserDetails userDetails) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));

        // Check if user is owner or admin
        if (!publication.getUploadedBy().getId().equals(userDetails.getId()) &&
                !userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("You don't have permission to update this publication");
        }

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Update publication
        publication.setTitle(request.getTitle());
        publication.setDescription(request.getDescription());
        publication.setYear(request.getYear());
        publication.setAuthor(request.getAuthor());
        publication.setCategory(category);

        Publication updatedPublication = publicationRepository.save(publication);
        log.info("Publication updated: {}", updatedPublication.getTitle());

        return new PublicationResponse(updatedPublication);
    }

    @Transactional
    public void deletePublication(Long id, CustomUserDetails userDetails) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));

        // Check if user is owner or admin
        if (!publication.getUploadedBy().getId().equals(userDetails.getId()) &&
                !userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("You don't have permission to delete this publication");
        }

        // Delete file
        fileStorageService.deleteFile(publication.getFileName());

        // Delete publication
        publicationRepository.delete(publication);
        log.info("Publication deleted: {}", publication.getTitle());
    }

    @Transactional
    public Resource downloadPublication(Long id) throws IOException {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));

        // Increment downloads
        publication.setDownloads(publication.getDownloads() + 1);
        publicationRepository.save(publication);

        // Load file as Resource
        Path filePath = fileStorageService.loadFile(publication.getFileName());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("File not found: " + publication.getFileName());
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/vnd.ms-excel") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
    }
}