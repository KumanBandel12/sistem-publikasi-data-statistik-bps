package com.bps.publikasistatistik.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Year is required")
    private Integer year;

    @Size(max = 100, message = "Author name must not exceed 100 characters")
    private String author;

    @Size(max = 50, message = "Catalog number must not exceed 50 characters")
    private String catalogNumber;

    @Size(max = 50, message = "Publication number must not exceed 50 characters")
    private String publicationNumber;

    @Size(max = 50, message = "ISSN/ISBN must not exceed 50 characters")
    private String issnIsbn;

    @Size(max = 50, message = "Release frequency must not exceed 50 characters")
    private String releaseFrequency;

    private LocalDate releaseDate;

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    private Boolean isFlagship = false;
}