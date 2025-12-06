package com.bps.publikasistatistik.controller;

import com.bps.publikasistatistik.dto.ApiResponse;
import com.bps.publikasistatistik.dto.ChangePasswordRequest;
import com.bps.publikasistatistik.dto.UpdateProfileRequest;
import com.bps.publikasistatistik.dto.UserResponse;
import com.bps.publikasistatistik.security.CustomUserDetails;
import com.bps.publikasistatistik.service.UserService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Profile", description = "User profile management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user profile", description = "Get current logged in user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserResponse userResponse = userService.getProfile(userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping
    @Operation(summary = "Update profile", description = "Update user profile (username and/or email)")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            UserResponse userResponse = userService.updateProfile(userDetails, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userDetails, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete account", description = "Delete user account permanently")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            userService.deleteAccount(userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Account deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile picture", description = "Upload or update user profile picture")
    public ResponseEntity<ApiResponse<UserResponse>> uploadProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        try {
            UserResponse userResponse = userService.uploadProfilePicture(userDetails, file);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile picture uploaded successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/picture")
    @Operation(summary = "Get profile picture", description = "Get current user profile picture")
    public ResponseEntity<Resource> getProfilePicture(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Resource resource = userService.getProfilePicture(userDetails);

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
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/picture")
    @Operation(summary = "Delete profile picture", description = "Delete current user profile picture")
    public ResponseEntity<ApiResponse<UserResponse>> deleteProfilePicture(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserResponse userResponse = userService.deleteProfilePicture(userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile picture deleted successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}