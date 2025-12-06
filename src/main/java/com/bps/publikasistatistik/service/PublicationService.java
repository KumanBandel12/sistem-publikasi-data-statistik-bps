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
import org.springframework.data.domain.PageRequest;

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
    private final NotificationService notificationService;

    public List<PublicationResponse> getAllPublications() {
        return publicationRepository.findAll().stream()
                .map(PublicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<PublicationResponse> searchPublications(String keyword, Long categoryId, Integer year, String sort) {
        List<Publication> publications;

        // Build query based on filters
        if (keyword != null && !keyword.trim().isEmpty() && categoryId != null && year != null) {
            // Search + Category + Year
            publications = publicationRepository.searchByKeyword(keyword).stream()
                    .filter(p -> p.getCategory().getId().equals(categoryId) && p.getYear().equals(year))
                    .collect(Collectors.toList());
        } else if (keyword != null && !keyword.trim().isEmpty() && categoryId != null) {
            // Search + Category
            publications = publicationRepository.searchByKeyword(keyword).stream()
                    .filter(p -> p.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        } else if (keyword != null && !keyword.trim().isEmpty() && year != null) {
            // Search + Year
            publications = publicationRepository.searchByKeyword(keyword).stream()
                    .filter(p -> p.getYear().equals(year))
                    .collect(Collectors.toList());
        } else if (categoryId != null && year != null) {
            // Category + Year
            publications = publicationRepository.findByCategoryIdAndYear(categoryId, year);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // Search only
            publications = publicationRepository.searchByKeyword(keyword);
        } else if (categoryId != null) {
            // Category only
            publications = publicationRepository.findByCategoryId(categoryId);
        } else if (year != null) {
            // Year only
            publications = publicationRepository.findByYear(year);
        } else {
            // No filter
            publications = publicationRepository.findAll();
        }

        // Apply sorting
        if (sort != null && !sort.trim().isEmpty()) {
            if (sort.equalsIgnoreCase("latest")) {
                publications = publications.stream()
                        .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                        .collect(Collectors.toList());
            } else if (sort.equalsIgnoreCase("oldest")) {
                publications = publications.stream()
                        .sorted((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()))
                        .collect(Collectors.toList());
            }
        }

        return publications.stream()
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

        // Validate file type (PDF only)
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new RuntimeException("Invalid file type. Only PDF files are allowed");
        }

        // Validate release date year matches year field
        if (request.getReleaseDate() != null && request.getYear() != null) {
            int releaseDateYear = request.getReleaseDate().getYear();
            if (releaseDateYear != request.getYear()) {
                throw new RuntimeException(
                    String.format("Release date year (%d) must match the year field (%d)", 
                        releaseDateYear, request.getYear())
                );
            }
        }

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Get user
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store file
        String fileName = fileStorageService.storeFile(file);
        Path filePath = fileStorageService.loadFile(fileName);

        // Generate cover image from PDF
        String coverFileName = null;
        try {
            coverFileName = fileStorageService.generateCoverFromPDF(filePath);
        } catch (Exception e) {
            log.error("Failed to generate cover, continuing without cover: {}", e.getMessage());
        }

        // Create publication
        Publication publication = new Publication();
        publication.setTitle(request.getTitle());
        publication.setDescription(request.getDescription());
        publication.setCatalogNumber(request.getCatalogNumber());
        publication.setPublicationNumber(request.getPublicationNumber());
        publication.setIssnIsbn(request.getIssnIsbn());
        publication.setReleaseFrequency(request.getReleaseFrequency());
        publication.setReleaseDate(request.getReleaseDate());
        publication.setLanguage(request.getLanguage());
        publication.setCoverImage(coverFileName);
        publication.setFileName(fileName);
        publication.setFilePath(filePath.toString());
        publication.setFileSize(file.getSize());
        publication.setYear(request.getYear());
        publication.setAuthor(request.getAuthor());
        publication.setCategory(category);
        publication.setUploadedBy(user);
        publication.setViews(0);
        publication.setDownloads(0);

        Publication savedPublication = publicationRepository.save(publication);
        log.info("Publication uploaded: {} by {}", savedPublication.getTitle(), user.getEmail());
        
        // Notify all users about new publication
        notificationService.notifyAllUsersNewPublication(savedPublication);

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

        // Validate release date year matches year field
        if (request.getReleaseDate() != null && request.getYear() != null) {
            int releaseDateYear = request.getReleaseDate().getYear();
            if (releaseDateYear != request.getYear()) {
                throw new RuntimeException(
                    String.format("Release date year (%d) must match the year field (%d)", 
                        releaseDateYear, request.getYear())
                );
            }
        }

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Update publication
        publication.setTitle(request.getTitle());
        publication.setDescription(request.getDescription());
        publication.setCatalogNumber(request.getCatalogNumber());
        publication.setPublicationNumber(request.getPublicationNumber());
        publication.setIssnIsbn(request.getIssnIsbn());
        publication.setReleaseFrequency(request.getReleaseFrequency());
        publication.setReleaseDate(request.getReleaseDate());
        publication.setLanguage(request.getLanguage());
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

        // Delete cover image if exists
        if (publication.getCoverImage() != null) {
            fileStorageService.deleteCoverImage(publication.getCoverImage());
        }

        // Delete publication
        publicationRepository.delete(publication);
        log.info("Publication deleted: {}", publication.getTitle());
    }

    @Transactional
    public Resource downloadPublication(Long id) throws IOException {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));

        // Increment downloads
        int previousDownloads = publication.getDownloads();
        publication.setDownloads(previousDownloads + 1);
        publicationRepository.save(publication);
        
        // Check milestone and notify admins (100, 500, 1000)
        int newDownloads = publication.getDownloads();
        if ((previousDownloads < 100 && newDownloads >= 100) ||
            (previousDownloads < 500 && newDownloads >= 500) ||
            (previousDownloads < 1000 && newDownloads >= 1000)) {
            // Determine which milestone was reached
            int milestone = newDownloads >= 1000 ? 1000 : (newDownloads >= 500 ? 500 : 100);
            notificationService.notifyAdminsMilestone(publication, milestone);
        }

        // Load file as Resource
        Path filePath = fileStorageService.loadFile(publication.getFileName());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("File not found: " + publication.getFileName());
        }
    }

    public Resource getCoverImage(Long id) throws IOException {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));

        if (publication.getCoverImage() == null) {
            throw new RuntimeException("Cover image not found for this publication");
        }

        // Load cover image as Resource
        Path coverPath = fileStorageService.loadCoverImage(publication.getCoverImage());
        Resource resource = new UrlResource(coverPath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Cover image file not found: " + publication.getCoverImage());
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && contentType.equals("application/pdf");
    }

    public List<String> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        // Ambil maksimal 10 saran judul
        return publicationRepository.findTitleSuggestions(keyword.trim(), PageRequest.of(0, 10));
    }
}