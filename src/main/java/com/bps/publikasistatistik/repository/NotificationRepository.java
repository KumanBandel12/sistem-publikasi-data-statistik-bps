package com.bps.publikasistatistik.repository;

import com.bps.publikasistatistik.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications by user, sorted by latest
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Count unread notifications by user
    Long countByUserIdAndIsReadFalse(Long userId);

    // Find all unread notifications by user
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Mark all as read for specific user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = ?1 AND n.isRead = false")
    void markAllAsReadByUserId(Long userId);

    // Delete all read notifications for specific user
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user.id = ?1 AND n.isRead = true")
    void deleteAllReadByUserId(Long userId);

    // Delete notifications older than specific date (for scheduled cleanup)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < ?1")
    void deleteByCreatedAtBefore(LocalDateTime dateTime);

    // Count all notifications by user (for pagination info)
    Long countByUserId(Long userId);
}
