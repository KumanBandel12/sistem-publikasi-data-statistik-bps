package com.bps.publikasistatistik.dto;

import com.bps.publikasistatistik.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long publicationCount;
    private LocalDateTime createdAt;

    // Constructor dari Entity (tanpa count)
    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.createdAt = category.getCreatedAt();
    }

    // Constructor dari Entity (dengan count)
    public CategoryResponse(Category category, Long publicationCount) {
        this(category);
        this.publicationCount = publicationCount;
    }
}