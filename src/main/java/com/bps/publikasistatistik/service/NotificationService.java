package com.bps.publikasistatistik.service;

import com.bps.publikasistatistik.entity.Notification;
import com.bps.publikasistatistik.entity.Publication;
import com.bps.publikasistatistik.entity.User;
import com.bps.publikasistatistik.repository.NotificationRepository;
import com.bps.publikasistatistik.repository.UserRepository;
import com.bps.publikasistatistik.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Get all notifications for user (paginated)
    public Page<NotificationResponse> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(NotificationResponse::new);
    }

    // Get unread notifications only
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    // Get unread count
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // Mark single notification as read
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        // Verify ownership
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // Mark all as read
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("All notifications marked as read for user: {}", userId);
    }

    // Delete single notification
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        // Verify ownership
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notificationRepository.delete(notification);
        log.info("Notification deleted: {}", notificationId);
    }

    // Clear all read notifications
    @Transactional
    public void clearAllRead(Long userId) {
        notificationRepository.deleteAllReadByUserId(userId);
        log.info("All read notifications cleared for user: {}", userId);
    }

    // ========== AUTO-TRIGGER METHODS ==========

    // Notify all users when new publication is uploaded
    @Transactional
    public void notifyAllUsersNewPublication(Publication publication) {
        List<User> allUsers = userRepository.findAll();

        // Batch insert - create list first, then saveAll() once
        List<Notification> notifications = allUsers.stream()
                .filter(user -> user.getRole() == User.Role.USER) // Only notify regular users
                .map(user -> {
                    Notification notif = new Notification();
                    notif.setUser(user);
                    notif.setTitle("Publikasi Baru!");
                    notif.setMessage(publication.getTitle() + " telah tersedia");
                    notif.setType(Notification.NotificationType.NEW_PUBLICATION);
                    notif.setRelatedEntityType("Publication");
                    notif.setRelatedEntityId(publication.getId());
                    return notif;
                })
                .collect(Collectors.toList());

        // 1 query for all inserts (not 100 separate queries)
        notificationRepository.saveAll(notifications);
        log.info("Notified {} users about new publication: {}", notifications.size(), publication.getTitle());
    }

    // Notify user about profile update (security alert)
    @Transactional
    public void notifyProfileUpdated(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Profil Diperbarui");
        notification.setMessage("Profil Anda telah berhasil diperbarui");
        notification.setType(Notification.NotificationType.PROFILE_UPDATED);
        notification.setRelatedEntityType("User");
        notification.setRelatedEntityId(user.getId());

        notificationRepository.save(notification);
        log.info("Profile update notification sent to user: {}", user.getEmail());
    }

    // Notify user about password change (security alert)
    @Transactional
    public void notifyPasswordChanged(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Password Diubah");
        notification.setMessage("Password Anda telah berhasil diubah. Jika bukan Anda, segera hubungi admin.");
        notification.setType(Notification.NotificationType.PASSWORD_CHANGED);
        notification.setRelatedEntityType("User");
        notification.setRelatedEntityId(user.getId());

        notificationRepository.save(notification);
        log.info("Password change notification sent to user: {}", user.getEmail());
    }

    // Notify all admins about new user registration
    @Transactional
    public void notifyAdminsNewUser(User newUser) {
        List<User> admins = userRepository.findByRole(User.Role.ADMIN);

        List<Notification> notifications = admins.stream()
                .map(admin -> {
                    Notification notif = new Notification();
                    notif.setUser(admin);
                    notif.setTitle("User Baru Terdaftar");
                    notif.setMessage("User baru: " + newUser.getEmail() + " telah mendaftar");
                    notif.setType(Notification.NotificationType.ADMIN_NEW_USER);
                    notif.setRelatedEntityType("User");
                    notif.setRelatedEntityId(newUser.getId());
                    return notif;
                })
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
        log.info("Notified {} admins about new user: {}", notifications.size(), newUser.getEmail());
    }

    // Notify admins about publication milestone
    @Transactional
    public void notifyAdminsMilestone(Publication publication, int milestone) {
        List<User> admins = userRepository.findByRole(User.Role.ADMIN);

        List<Notification> notifications = admins.stream()
                .map(admin -> {
                    Notification notif = new Notification();
                    notif.setUser(admin);
                    notif.setTitle("Milestone Tercapai!");
                    notif.setMessage(publication.getTitle() + " telah mencapai " + milestone + " downloads");
                    notif.setType(Notification.NotificationType.ADMIN_MILESTONE);
                    notif.setRelatedEntityType("Publication");
                    notif.setRelatedEntityId(publication.getId());
                    return notif;
                })
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
        log.info("Notified {} admins about milestone {} for publication: {}", 
                notifications.size(), milestone, publication.getTitle());
    }
}
