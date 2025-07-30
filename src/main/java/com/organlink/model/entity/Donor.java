package com.organlink.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing organ donors
 * Each donor belongs to a specific hospital (multi-tenant)
 */
@Entity
@Table(name = "donors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "donor_name", nullable = false, length = 100)
    private String donorName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "blood_type", nullable = false, length = 5)
    private String bloodType;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "organ_available", nullable = false, length = 50)
    private String organAvailable;

    @Column(name = "donor_type", nullable = false, length = 20)
    private String donorType; // LIVING, DECEASED

    @Column(name = "cause_of_death", length = 200)
    private String causeOfDeath;

    @Column(name = "time_of_death")
    private LocalDateTime timeOfDeath;

    @Column(name = "medical_clearance", columnDefinition = "TEXT")
    private String medicalClearance;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE"; // AVAILABLE, MATCHED, TRANSPLANTED, EXPIRED

    @Column(name = "hla_typing", columnDefinition = "TEXT")
    private String hlaTyping;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // Relationship with Hospital
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", insertable = false, updatable = false)
    private Hospital hospital;

    /**
     * Check if donor is available for matching
     */
    public boolean isAvailableForMatching() {
        return isActive && "AVAILABLE".equals(status);
    }

    /**
     * Check if organ is still viable (within preservation time)
     */
    public boolean isOrganViable() {
        if (timeOfDeath == null) return true; // Living donor
        
        // Check if within preservation time (simplified - should check organ-specific times)
        LocalDateTime expirationTime = timeOfDeath.plusHours(24); // Default 24 hours
        return LocalDateTime.now().isBefore(expirationTime);
    }
}
