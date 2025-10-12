package com.bps.publikasistatistik.service;

import com.bps.publikasistatistik.dto.CategoryRequest;
import com.bps.publikasistatistik.dto.CategoryResponse;
import com.bps.publikasistatistik.entity.Category;
import com.bps.publikasistatistik.repository.CategoryRepository;
import com.bps.publikasistatistik.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PublicationRepository publicationRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    Long count = publicationRepository.countByCategoryId(category.getId());
                    return new CategoryResponse(category, count);
                })
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        Long count = publicationRepository.countByCategoryId(id);
        return new CategoryResponse(category, count);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Check if category name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {}", savedCategory.getName());

        return new CategoryResponse(savedCategory, 0L);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if new name already exists (excluding current category)
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated: {}", updatedCategory.getName());

        Long count = publicationRepository.countByCategoryId(id);
        return new CategoryResponse(updatedCategory, count);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if category has publications
        Long publicationCount = publicationRepository.countByCategoryId(id);
        if (publicationCount > 0) {
            throw new RuntimeException("Cannot delete category with existing publications. Please delete publications first.");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {}", category.getName());
    }
}