package com.bps.publikasistatistik.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.profile.dir}")
    private String profileUploadDir;

    @Value("${app.upload.covers.dir}")
    private String coversUploadDir;

    // Initialize upload directory
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Upload directory created: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    // Initialize profile upload directory
    public void initProfileDir() {
        try {
            Path uploadPath = Paths.get(profileUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Profile upload directory created: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create profile upload directory!", e);
        }
    }

    // Initialize covers upload directory
    public void initCoversDir() {
        try {
            Path uploadPath = Paths.get(coversUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Covers upload directory created: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create covers upload directory!", e);
        }
    }

    // Store file
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if file name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Invalid file name: " + originalFileName);
            }

            // Generate unique file name
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Initialize directory if not exists
            init();

            // Copy file to upload directory
            Path targetLocation = Paths.get(uploadDir).resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", uniqueFileName);
            return uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    // Load file as Path
    public Path loadFile(String fileName) {
        return Paths.get(uploadDir).resolve(fileName).normalize();
    }

    // Delete file
    public void deleteFile(String fileName) {
        try {
            Path filePath = loadFile(fileName);
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", fileName);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", fileName, ex);
        }
    }

    // Store profile picture
    public String storeProfilePicture(MultipartFile file) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if file name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Invalid file name: " + originalFileName);
            }

            // Generate unique file name
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Initialize directory if not exists
            initProfileDir();

            // Copy file to profile upload directory
            Path targetLocation = Paths.get(profileUploadDir).resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Profile picture stored successfully: {}", uniqueFileName);
            return uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store profile picture " + originalFileName + ". Please try again!", ex);
        }
    }

    // Load profile picture as Path
    public Path loadProfilePicture(String fileName) {
        return Paths.get(profileUploadDir).resolve(fileName).normalize();
    }

    // Delete profile picture
    public void deleteProfilePicture(String fileName) {
        try {
            Path filePath = loadProfilePicture(fileName);
            Files.deleteIfExists(filePath);
            log.info("Profile picture deleted successfully: {}", fileName);
        } catch (IOException ex) {
            log.error("Could not delete profile picture: {}", fileName, ex);
        }
    }

    // Generate cover image from PDF first page
    public String generateCoverFromPDF(Path pdfPath) {
        try {
            initCoversDir();

            // Generate unique filename for cover
            String coverFileName = UUID.randomUUID().toString() + ".jpg";
            Path coverPath = Paths.get(coversUploadDir).resolve(coverFileName);

            // Load PDF and render first page
            try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                
                // Render first page at 150 DPI for good quality
                BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150);
                
                // Save as JPEG
                ImageIO.write(image, "JPEG", coverPath.toFile());
                
                log.info("Cover image generated successfully: {}", coverFileName);
                return coverFileName;
            }
        } catch (IOException e) {
            log.error("Failed to generate cover from PDF: {}", e.getMessage());
            throw new RuntimeException("Could not generate cover image from PDF", e);
        }
    }

    // Load cover image as Path
    public Path loadCoverImage(String fileName) {
        return Paths.get(coversUploadDir).resolve(fileName).normalize();
    }

    // Delete cover image
    public void deleteCoverImage(String fileName) {
        try {
            Path filePath = loadCoverImage(fileName);
            Files.deleteIfExists(filePath);
            log.info("Cover image deleted successfully: {}", fileName);
        } catch (IOException ex) {
            log.error("Could not delete cover image: {}", fileName, ex);
        }
    }
}