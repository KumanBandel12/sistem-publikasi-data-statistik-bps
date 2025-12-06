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
    
    // Get all categories in tree structure (root dengan subCategories)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNullOrderByDisplayOrderAsc();
        
        return rootCategories.stream()
                .map(category -> {
                    Long count = publicationRepository.countByCategoryId(category.getId());
                    return new CategoryResponse(category, count, true); // include subCategories
                })
                .collect(Collectors.toList());
    }
    
    // Get sub-categories by parent ID
    public List<CategoryResponse> getSubCategories(Long parentId) {
        // Validasi parent category exists
        categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + parentId));
        
        List<Category> subCategories = categoryRepository.findByParentCategoryIdOrderByDisplayOrderAsc(parentId);
        
        return subCategories.stream()
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
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        
        // Handle parent category (untuk sub-kategori)
        if (request.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
            
            // Validasi: parent harus level 0 (root category)
            if (parentCategory.getLevel() != 0) {
                throw new RuntimeException("Cannot create sub-category under another sub-category. Maximum 2 levels allowed.");
            }
            
            category.setParentCategory(parentCategory);
            category.setLevel(1); // sub-kategori
        } else {
            category.setLevel(0); // root category
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {} (Level: {})", savedCategory.getName(), savedCategory.getLevel());

        return new CategoryResponse(savedCategory, 0L);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if new name already exists (excluding current category)
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : category.getDisplayOrder());
        
        // Handle parent category change
        if (request.getParentId() != null) {
            // Validasi: tidak boleh set diri sendiri sebagai parent
            if (request.getParentId().equals(id)) {
                throw new RuntimeException("Category cannot be its own parent");
            }
            
            Category newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
            
            // Validasi: parent harus level 0
            if (newParent.getLevel() != 0) {
                throw new RuntimeException("Cannot set sub-category as parent. Maximum 2 levels allowed.");
            }
            
            // Validasi: jika category ini punya sub-categories, tidak boleh jadi sub-category
            if (categoryRepository.existsByParentCategoryId(id)) {
                throw new RuntimeException("Cannot convert category with sub-categories into a sub-category");
            }
            
            category.setParentCategory(newParent);
            category.setLevel(1);
        } else if (category.getParentCategory() != null && request.getParentId() == null) {
            // Remove parent (convert sub-category to root)
            category.setParentCategory(null);
            category.setLevel(0);
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated: {} (Level: {})", updatedCategory.getName(), updatedCategory.getLevel());

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
        
        // Check if category has sub-categories
        if (categoryRepository.existsByParentCategoryId(id)) {
            throw new RuntimeException("Cannot delete category with existing sub-categories. Please delete sub-categories first.");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {}", category.getName());
    }
}