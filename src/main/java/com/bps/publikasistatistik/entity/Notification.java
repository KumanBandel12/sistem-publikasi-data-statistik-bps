package com.bps.publikasistatistik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    // Untuk deep link (klik notifikasi langsung ke detail publikasi/user)
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // "Publication", "User", etc.

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enum untuk tipe notifikasi
    public enum NotificationType {
        NEW_PUBLICATION,        // Publikasi baru ditambahkan
        PROFILE_UPDATED,        // Profil user diupdate
        PASSWORD_CHANGED,       // Password diubah (security alert)
        ADMIN_NEW_USER,         // (Admin) User baru registrasi
        ADMIN_MILESTONE         // (Admin) Publikasi mencapai milestone
    }
}
