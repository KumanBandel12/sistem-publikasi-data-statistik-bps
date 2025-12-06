package com.bps.publikasistatistik.dto;

import com.bps.publikasistatistik.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long publicationCount;
    private Integer level;
    private Integer displayOrder;
    private CategoryResponse parentCategory;
    private List<CategoryResponse> subCategories = new ArrayList<>();
    private LocalDateTime createdAt;

    // Constructor dari Entity (tanpa count, tanpa subCategories)
    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.level = category.getLevel();
        this.displayOrder = category.getDisplayOrder();
        this.createdAt = category.getCreatedAt();
        
        // Set parent category (simple, tanpa recursive)
        if (category.getParentCategory() != null) {
            Category parent = category.getParentCategory();
            this.parentCategory = new CategoryResponse();
            this.parentCategory.setId(parent.getId());
            this.parentCategory.setName(parent.getName());
            this.parentCategory.setLevel(parent.getLevel());
        }
    }

    // Constructor dari Entity (dengan count)
    public CategoryResponse(Category category, Long publicationCount) {
        this(category);
        this.publicationCount = publicationCount;
    }
    
    // Constructor dari Entity (dengan subCategories dan count)
    public CategoryResponse(Category category, Long publicationCount, boolean includeSubCategories) {
        this(category, publicationCount);
        
        if (includeSubCategories && category.getSubCategories() != null) {
            this.subCategories = category.getSubCategories().stream()
                .map(sub -> new CategoryResponse(sub, (long) sub.getPublications().size()))
                .collect(Collectors.toList());
        }
    }
}