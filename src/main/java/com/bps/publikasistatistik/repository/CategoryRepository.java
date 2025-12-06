package com.bps.publikasistatistik.repository;

import com.bps.publikasistatistik.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find category by name
    Optional<Category> findByName(String name);

    // Check if category name exists
    Boolean existsByName(String name);
    
    // Find all root categories (parent is null) ordered by displayOrder
    List<Category> findByParentCategoryIsNullOrderByDisplayOrderAsc();
    
    // Find sub-categories by parent ID ordered by displayOrder
    List<Category> findByParentCategoryIdOrderByDisplayOrderAsc(Long parentId);
    
    // Count sub-categories by parent ID
    Long countByParentCategoryId(Long parentId);
    
    // Check if category has sub-categories
    Boolean existsByParentCategoryId(Long parentId);
    
    // Check if category name exists (excluding specific ID for update)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.name = ?1 AND c.id <> ?2")
    Boolean existsByNameAndIdNot(String name, Long id);
}