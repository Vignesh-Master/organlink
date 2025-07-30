package com.organlink.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing hospital user credentials
 * Each hospital has one login account
 */
@Entity
@Table(name = "hospital_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // Relationship with Hospital
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", insertable = false, updatable = false)
    private Hospital hospital;

    /**
     * Check if account is locked
     */
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * Check if account is active and not locked
     */
    public boolean isAccountValid() {
        return isActive && !isAccountLocked();
    }
}
