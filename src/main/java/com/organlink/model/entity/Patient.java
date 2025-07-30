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
 * Entity representing organ recipients/patients
 * Each patient belongs to a specific hospital (multi-tenant)
 */
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "blood_type", nullable = false, length = 5)
    private String bloodType;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "organ_needed", nullable = false, length = 50)
    private String organNeeded;

    @Column(name = "urgency_level", nullable = false, length = 20)
    private String urgencyLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "medical_condition", columnDefinition = "TEXT")
    private String medicalCondition;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "WAITING"; // WAITING, MATCHED, TRANSPLANTED, INACTIVE

    @Column(name = "hla_typing", columnDefinition = "TEXT")
    private String hlaTyping;

    @Column(name = "cross_match_results", columnDefinition = "TEXT")
    private String crossMatchResults;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "waiting_list_date")
    private LocalDateTime waitingListDate;

    @Column(name = "priority_score")
    private Integer priorityScore;

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
     * Check if patient is eligible for matching
     */
    public boolean isEligibleForMatching() {
        return isActive && "WAITING".equals(status);
    }

    /**
     * Get urgency priority (higher number = more urgent)
     */
    public int getUrgencyPriority() {
        return switch (urgencyLevel) {
            case "CRITICAL" -> 4;
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            case "LOW" -> 1;
            default -> 0;
        };
    }
}
