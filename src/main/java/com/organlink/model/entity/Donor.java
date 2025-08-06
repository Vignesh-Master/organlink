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
 * Entity representing organ donors
 * Each donor belongs to a specific hospital (multi-tenant)
 */
@Entity
@Table(name = "donors",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "donor_id")
       },
       indexes = {
           @Index(name = "idx_donor_id", columnList = "donor_id"),
           @Index(name = "idx_donor_blood_group", columnList = "blood_group"),
           @Index(name = "idx_donor_state", columnList = "state_id"),
           @Index(name = "idx_donor_hospital", columnList = "hospital_id"),
           @Index(name = "idx_donor_active", columnList = "is_active")
       })
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Donor extends BaseEntity {

    @NotBlank(message = "Donor ID is required")
    @Size(max = 50, message = "Donor ID cannot exceed 50 characters")
    @Column(name = "donor_id", nullable = false, length = 50, unique = true)
    private String donorId;

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

    @Size(max = 64, message = "Consent form hash cannot exceed 64 characters")
    @Column(name = "consent_form_hash", length = 64)
    private String consentFormHash;

    @Size(max = 66, message = "Blockchain transaction hash cannot exceed 66 characters")
    @Column(name = "blockchain_tx_hash", length = 66)
    private String blockchainTxHash;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    // Enum for gender
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAvailable() {
        return isActive != null && isActive;
    }

    // Age calculation helper
    public Integer calculateAge() {
        if (dateOfBirth != null) {
            return LocalDate.now().getYear() - dateOfBirth.getYear();
        }
        return null;
    }

    // Organ compatibility helper
    public boolean isBloodTypeCompatible(String recipientBloodType) {
        if (bloodGroup == null || recipientBloodType == null) {
            return false;
        }
        
        // Basic blood type compatibility logic
        switch (bloodGroup.toUpperCase()) {
            case "O":
                return true; // Universal donor
            case "A":
                return recipientBloodType.toUpperCase().matches("A|AB");
            case "B":
                return recipientBloodType.toUpperCase().matches("B|AB");
            case "AB":
                return "AB".equals(recipientBloodType.toUpperCase());
            default:
                return false;
        }
    }

    // Status management
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
