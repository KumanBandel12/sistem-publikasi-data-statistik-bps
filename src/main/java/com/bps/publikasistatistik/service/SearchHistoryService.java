package com.bps.publikasistatistik.service;

import com.bps.publikasistatistik.dto.SearchHistoryResponse;
import com.bps.publikasistatistik.entity.SearchHistory;
import com.bps.publikasistatistik.entity.User;
import com.bps.publikasistatistik.repository.SearchHistoryRepository;
import com.bps.publikasistatistik.repository.UserRepository;
import com.bps.publikasistatistik.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    public List<SearchHistoryResponse> getSearchHistory(CustomUserDetails userDetails) {
        List<SearchHistory> histories = searchHistoryRepository.findTop10ByUserIdOrderBySearchedAtDesc(userDetails.getId());
        return histories.stream()
                .map(SearchHistoryResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveSearchKeyword(CustomUserDetails userDetails, String keyword) {
        // 1. Validasi Input (Cegah null atau spasi kosong)
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String cleanKeyword = keyword.trim();

        // 2. Cek apakah user ini pernah mencari kata tersebut?
        SearchHistory existingHistory = searchHistoryRepository
                .findByUserIdAndKeyword(userDetails.getId(), cleanKeyword);

        if (existingHistory != null) {
            // SKENARIO A: Kata kunci SUDAH ADA
            // Update waktu pencariannya ke "Sekarang" agar naik ke paling atas
            existingHistory.setSearchedAt(LocalDateTime.now());
            searchHistoryRepository.save(existingHistory);
            log.debug("Updated timestamp for existing keyword: {}", cleanKeyword);

        } else {
            // SKENARIO B: Kata kunci BELUM ADA (Baru)
            // Ambil data user dari database
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Buat object history baru
            SearchHistory newHistory = new SearchHistory();
            newHistory.setKeyword(cleanKeyword);
            newHistory.setUser(user);
            newHistory.setSearchedAt(LocalDateTime.now());

            searchHistoryRepository.save(newHistory);
            log.info("Saved new keyword: {}", cleanKeyword);
        }
    }

    @Transactional
    public void clearSearchHistory(CustomUserDetails userDetails) {
        searchHistoryRepository.deleteByUserId(userDetails.getId());
        log.info("Search history cleared for user ID: {}", userDetails.getId());
    }
}
