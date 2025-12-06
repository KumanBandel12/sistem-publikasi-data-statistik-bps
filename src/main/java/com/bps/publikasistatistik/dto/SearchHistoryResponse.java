package com.bps.publikasistatistik.dto;

import com.bps.publikasistatistik.entity.SearchHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryResponse {
    private Long id;
    private String keyword;
    private LocalDateTime searchedAt;

    public SearchHistoryResponse(SearchHistory searchHistory) {
        this.id = searchHistory.getId();
        this.keyword = searchHistory.getKeyword();
        this.searchedAt = searchHistory.getSearchedAt();
    }
}
