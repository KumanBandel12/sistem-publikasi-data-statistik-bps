package com.bps.publikasistatistik.repository;

import com.bps.publikasistatistik.entity.Publication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    // Find publications by category ID
    List<Publication> findByCategoryId(Long categoryId);

    // Find publications by year
    List<Publication> findByYear(Integer year);

    // Find publications by uploaded user
    List<Publication> findByUploadedById(Long userId);

    // Search publications by title (case insensitive, partial match)
    List<Publication> findByTitleContainingIgnoreCase(String keyword);

    // Search publications by title or description
    @Query("SELECT p FROM Publication p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Publication> searchByKeyword(@Param("keyword") String keyword);

    // Find publications by category and year
    List<Publication> findByCategoryIdAndYear(Long categoryId, Integer year);

    // Get most downloaded publications (top 10)
    List<Publication> findTop10ByOrderByDownloadsDesc();

    // Get most viewed publications (top 10)
    List<Publication> findTop10ByOrderByViewsDesc();

    // Get latest publications (top 10)
    List<Publication> findTop10ByOrderByCreatedAtDesc();

    // Count publications by category
    Long countByCategoryId(Long categoryId);

    @Query("SELECT DISTINCT p.title FROM Publication p " +
           "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.title ASC")
    List<String> findTitleSuggestions(@Param("keyword") String keyword, Pageable pageable);

    // --- QUERY UNTUK SLIDE 1 (Unggulan) ---
    // Ambil yang ditandai flagship, urutkan dari yang terbaru ditandai/diupdate
    List<Publication> findByIsFlagshipTrueOrderByUpdatedAtDesc();

    // Hitung jumlah flagship aktif
    long countByIsFlagshipTrue();
}