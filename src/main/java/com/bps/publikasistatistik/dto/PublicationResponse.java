package com.bps.publikasistatistik.dto;

import com.bps.publikasistatistik.entity.Publication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationResponse {
    private Long id;
    private String title;
    private String description;
    private String catalogNumber;
    private String publicationNumber;
    private String issnIsbn;
    private String releaseFrequency;
    private LocalDate releaseDate;
    private String language;
    private String coverImage;
    private String coverUrl;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileSizeFormatted;
    private Integer year;
    private String author;
    private Integer views;
    private Integer downloads;
    private CategoryResponse category;
    private UserResponse uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor dari Entity
    public PublicationResponse(Publication publication) {
        this.id = publication.getId();
        this.title = publication.getTitle();
        this.description = publication.getDescription();
        this.catalogNumber = publication.getCatalogNumber();
        this.publicationNumber = publication.getPublicationNumber();
        this.issnIsbn = publication.getIssnIsbn();
        this.releaseFrequency = publication.getReleaseFrequency();
        this.releaseDate = publication.getReleaseDate();
        this.language = publication.getLanguage();
        this.coverImage = publication.getCoverImage();
        this.coverUrl = publication.getCoverImage() != null ? "/api/publications/" + publication.getId() + "/cover" : null;
        this.fileName = publication.getFileName();
        this.fileUrl = "/api/publications/" + publication.getId() + "/download";
        this.fileSize = publication.getFileSize();
        this.fileSizeFormatted = formatFileSize(publication.getFileSize());
        this.year = publication.getYear();
        this.author = publication.getAuthor();
        this.views = publication.getViews();
        this.downloads = publication.getDownloads();
        this.category = new CategoryResponse(publication.getCategory());
        this.uploadedBy = new UserResponse(publication.getUploadedBy());
        this.createdAt = publication.getCreatedAt();
        this.updatedAt = publication.getUpdatedAt();
    }

    // Helper method untuk format file size
    private String formatFileSize(Long bytes) {
        if (bytes == null || bytes == 0) return "0 B";

        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }
}