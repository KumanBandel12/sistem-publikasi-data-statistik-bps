package com.bps.publikasistatistik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "publications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "catalog_number", length = 50)
    private String catalogNumber;

    @Column(name = "publication_number", length = 50)
    private String publicationNumber;

    @Column(name = "issn_isbn", length = 50)
    private String issnIsbn;

    @Column(name = "release_frequency", length = 50)
    private String releaseFrequency; // Contoh: "Tahunan", "Bulanan"

    @Column(name = "release_date")
    private LocalDate releaseDate; // Tanggal rilis lengkap

    @Column(length = 50)
    private String language; // Contoh: "Indonesia", "Inggris"

    @Column(name = "cover_image")
    private String coverImage; // Nama file cover

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize; // dalam bytes

    @Column(nullable = false)
    private Integer year;

    @Column(length = 100)
    private String author;

    @Column(nullable = false)
    private Integer views = 0;

    @Column(nullable = false)
    private Integer downloads = 0;

    // Relasi Many-to-One dengan Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Relasi Many-to-One dengan User (uploader)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "is_flagship", nullable = false)
    private Boolean isFlagship = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}