package com.bps.publikasistatistik.controller;

import com.bps.publikasistatistik.dto.ApiResponse;
import com.bps.publikasistatistik.dto.SearchHistoryResponse;
import com.bps.publikasistatistik.security.CustomUserDetails;
import com.bps.publikasistatistik.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search/history")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Search History", description = "Search history management APIs")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping
    @Operation(summary = "Get search history", description = "Get user's recent search history (last 10)")
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> getSearchHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<SearchHistoryResponse> history = searchHistoryService.getSearchHistory(userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Search history retrieved successfully", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping
    @Operation(summary = "Clear search history", description = "Clear all search history for current user")
    public ResponseEntity<ApiResponse<Void>> clearSearchHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            searchHistoryService.clearSearchHistory(userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Search history cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
