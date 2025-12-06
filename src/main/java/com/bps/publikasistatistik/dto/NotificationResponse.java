package com.bps.publikasistatistik.dto;

import com.bps.publikasistatistik.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String type;
    private String relatedEntityType;
    private Long relatedEntityId;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // Constructor from Entity
    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.type = notification.getType().name();
        this.relatedEntityType = notification.getRelatedEntityType();
        this.relatedEntityId = notification.getRelatedEntityId();
        this.isRead = notification.getIsRead();
        this.createdAt = notification.getCreatedAt();
    }
}
