package com.bps.publikasistatistik.util;

import com.bps.publikasistatistik.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;

    // Run every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        
        try {
            notificationRepository.deleteByCreatedAtBefore(sevenDaysAgo);
            log.info("Successfully deleted notifications older than 7 days");
        } catch (Exception e) {
            log.error("Error deleting old notifications: {}", e.getMessage());
        }
    }
}
