package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing organ recipients/patients
 * Each patient belongs to a specific hospital (multi-tenant)
 */
@Entity
@Table(name = "patients",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "patient_id")
       },
       indexes = {
           @Index(name = "idx_patient_id", columnList = "patient_id"),
           @Index(name = "idx_patient_blood_group", columnList = "blood_group"),
           @Index(name = "idx_patient_organ_type", columnList = "organ_type_id"),
           @Index(name = "idx_patient_urgency", columnList = "urgency_level"),
           @Index(name = "idx_patient_state", columnList = "state_id"),
           @Index(name = "idx_patient_hospital", columnList = "hospital_id"),
           @Index(name = "idx_patient_active", columnList = "is_active")
       })
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {

    @NotBlank(message = "Patient ID is required")
    @Size(max = 50, message = "Patient ID cannot exceed 50 characters")
    @Column(name = "patient_id", nullable = false, length = 50, unique = true)
    private String patientId;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Blood group is required")
    @Size(max = 5, message = "Blood group cannot exceed 5 characters")
    @Column(name = "blood_group", nullable = false, length = 5)
    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city", length = 100)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @NotNull(message = "Organ type is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_type_id", nullable = false)
    private OrganType organType;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false)
    private UrgencyLevel urgencyLevel = UrgencyLevel.MEDIUM;

    @Column(name = "medical_condition", columnDefinition = "TEXT")
    private String medicalCondition;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Enums
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum UrgencyLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return isActive != null && isActive;
    }

    // Age calculation helper
    public Integer calculateAge() {
        if (dateOfBirth != null) {
            return LocalDate.now().getYear() - dateOfBirth.getYear();
        }
        return null;
    }

    // Blood type compatibility helper
    public boolean isBloodTypeCompatible(String donorBloodType) {
        if (bloodGroup == null || donorBloodType == null) {
            return false;
        }
        
        // Basic blood type compatibility logic for recipients
        switch (bloodGroup.toUpperCase()) {
            case "AB":
                return true; // Universal recipient
            case "A":
                return donorBloodType.toUpperCase().matches("A|O");
            case "B":
                return donorBloodType.toUpperCase().matches("B|O");
            case "O":
                return "O".equals(donorBloodType.toUpperCase());
            default:
                return false;
        }
    }

    // Urgency level helpers
    public boolean isCritical() {
        return UrgencyLevel.CRITICAL.equals(urgencyLevel);
    }

    public boolean isHighPriority() {
        return UrgencyLevel.HIGH.equals(urgencyLevel) || UrgencyLevel.CRITICAL.equals(urgencyLevel);
    }

    // Status management
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Urgency management
    public void escalateUrgency() {
        switch (urgencyLevel) {
            case LOW:
                this.urgencyLevel = UrgencyLevel.MEDIUM;
                break;
            case MEDIUM:
                this.urgencyLevel = UrgencyLevel.HIGH;
                break;
            case HIGH:
                this.urgencyLevel = UrgencyLevel.CRITICAL;
                break;
            case CRITICAL:
                // Already at highest level
                break;
        }
    }

    public void reduceUrgency() {
        switch (urgencyLevel) {
            case CRITICAL:
                this.urgencyLevel = UrgencyLevel.HIGH;
                break;
            case HIGH:
                this.urgencyLevel = UrgencyLevel.MEDIUM;
                break;
            case MEDIUM:
                this.urgencyLevel = UrgencyLevel.LOW;
                break;
            case LOW:
                // Already at lowest level
                break;
        }
    }
}
