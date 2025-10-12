package com.bps.publikasistatistik.controller;

import com.bps.publikasistatistik.dto.ApiResponse;
import com.bps.publikasistatistik.dto.PublicationRequest;
import com.bps.publikasistatistik.dto.PublicationResponse;
import com.bps.publikasistatistik.security.CustomUserDetails;
import com.bps.publikasistatistik.service.PublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/publications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Publications", description = "Publication management APIs")
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping
    @Operation(summary = "Get all publications", description = "Get list of all publications or search by keyword")
    public ResponseEntity<ApiResponse<List<PublicationResponse>>> getAllPublications(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer year) {
        try {
            List<PublicationResponse> publications;

            if (categoryId != null && year != null) {
                publications = publicationService.getPublicationsByCategoryAndYear(categoryId, year);
            } else if (categoryId != null) {
                publications = publicationService.getPublicationsByCategory(categoryId);
            } else if (year != null) {
                publications = publicationService.getPublicationsByYear(year);
            } else if (search != null) {
                publications = publicationService.searchPublications(search);
            } else {
                publications = publicationService.getAllPublications();
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Publications retrieved successfully", publications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest publications", description = "Get top 10 latest publications")
    public ResponseEntity<ApiResponse<List<PublicationResponse>>> getLatestPublications() {
        try {
            List<PublicationResponse> publications = publicationService.getLatestPublications();
            return ResponseEntity.ok(new ApiResponse<>(true, "Latest publications retrieved successfully", publications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/most-downloaded")
    @Operation(summary = "Get most downloaded publications", description = "Get top 10 most downloaded publications")
    public ResponseEntity<ApiResponse<List<PublicationResponse>>> getMostDownloaded() {
        try {
            List<PublicationResponse> publications = publicationService.getMostDownloaded();
            return ResponseEntity.ok(new ApiResponse<>(true, "Most downloaded publications retrieved successfully", publications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get publication by ID", description = "Get publication details by ID (increments view count)")
    public ResponseEntity<ApiResponse<PublicationResponse>> getPublicationById(@PathVariable Long id) {
        try {
            PublicationResponse publication = publicationService.getPublicationById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Publication retrieved successfully", publication));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload publication", description = "Upload a new publication with file (Admin only)")
    public ResponseEntity<ApiResponse<PublicationResponse>> uploadPublication(
            @Valid @ModelAttribute PublicationRequest request,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            PublicationResponse publication = publicationService.uploadPublication(request, file, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Publication uploaded successfully", publication));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update publication", description = "Update publication metadata (Admin or Owner only)")
    public ResponseEntity<ApiResponse<PublicationResponse>> updatePublication(
            @PathVariable Long id,
            @Valid @RequestBody PublicationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            PublicationResponse publication = publicationService.updatePublication(id, request, userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Publication updated successfully", publication));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete publication", description = "Delete publication and its file (Admin or Owner only)")
    public ResponseEntity<ApiResponse<Void>> deletePublication(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            publicationService.deletePublication(id, userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Publication deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download publication file", description = "Download publication file (increments download count)")
    public ResponseEntity<Resource> downloadPublication(@PathVariable Long id) {
        try {
            Resource resource = publicationService.downloadPublication(id);

            // Determine file content type
            String contentType = "application/octet-stream";
            try {
                Path filePath = Paths.get(resource.getURI());
                contentType = Files.probeContentType(filePath);
            } catch (IOException ex) {
                // Use default content type
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}