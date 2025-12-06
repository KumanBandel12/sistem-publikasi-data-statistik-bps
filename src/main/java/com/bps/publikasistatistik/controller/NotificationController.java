package com.bps.publikasistatistik.controller;

import com.bps.publikasistatistik.dto.ApiResponse;
import com.bps.publikasistatistik.dto.NotificationResponse;
import com.bps.publikasistatistik.security.CustomUserDetails;
import com.bps.publikasistatistik.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Get paginated list of user notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Page<NotificationResponse> notifications = notificationService.getUserNotifications(
                    userDetails.getId(), page, size);
            return ResponseEntity.ok(new ApiResponse<>(true, "Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Get list of unread notifications only")
    public ResponseEntity<ApiResponse<java.util.List<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            java.util.List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userDetails.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Unread notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications (for badge)")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long count = notificationService.getUnreadCount(userDetails.getId());
            Map<String, Long> result = new HashMap<>();
            result.put("unreadCount", count);
            return ResponseEntity.ok(new ApiResponse<>(true, "Unread count retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark single notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            notificationService.markAsRead(id, userDetails.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            notificationService.markAllAsRead(userDetails.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "All notifications marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete single notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            notificationService.deleteNotification(id, userDetails.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/clear-all")
    @Operation(summary = "Clear all read", description = "Delete all read notifications")
    public ResponseEntity<ApiResponse<Void>> clearAllRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            notificationService.clearAllRead(userDetails.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "All read notifications cleared"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
