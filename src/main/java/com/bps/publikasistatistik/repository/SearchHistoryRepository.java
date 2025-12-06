package com.bps.publikasistatistik.repository;

import com.bps.publikasistatistik.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    // Find search history by user ID, ordered by most recent
    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(Long userId);

    // Delete all search history by user ID
    void deleteByUserId(Long userId);

    // Check if keyword already exists for user
    boolean existsByUserIdAndKeyword(Long userId, String keyword);

    // Get top N recent searches for user
    List<SearchHistory> findTop10ByUserIdOrderBySearchedAtDesc(Long userId);

    SearchHistory findByUserIdAndKeyword(Long userId, String keyword);
}
