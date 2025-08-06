package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * Entity representing a hospital in the system
 * Each hospital operates as a separate tenant with isolated data
 */
@Entity
@Table(name = "hospitals",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "code"),
           @UniqueConstraint(columnNames = "tenant_id")
       },
       indexes = {
           @Index(name = "idx_hospital_state", columnList = "state_id"),
           @Index(name = "idx_hospital_active", columnList = "is_active"),
           @Index(name = "idx_hospital_tenant", columnList = "tenant_id")
       })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"state"})
@EqualsAndHashCode(callSuper = true, exclude = {"state"})
public class Hospital extends BaseEntity {

    @NotBlank(message = "Hospital name is required")
    @Size(min = 2, max = 200, message = "Hospital name must be between 2 and 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotBlank(message = "Hospital code is required")
    @Size(min = 2, max = 20, message = "Hospital code must be between 2 and 20 characters")
    @Column(name = "code", nullable = false, length = 20, unique = true)
    private String code;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(name = "email", length = 100)
    private String email;

    @NotNull(message = "State is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Size(max = 50, message = "License number cannot exceed 50 characters")
    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotBlank(message = "Tenant ID is required")
    @Size(min = 3, max = 50, message = "Tenant ID must be between 3 and 50 characters")
    @Column(name = "tenant_id", nullable = false, length = 50, unique = true)
    private String tenantId;

    // Note: User, Donor, Recipient relationships will be added when those entities are created

    // Custom constructor for convenience
    public Hospital(String name, String code, String tenantId, State state) {
        this.name = name;
        this.code = code.toUpperCase();
        this.tenantId = tenantId.toLowerCase();
        this.state = state;
    }

    // Custom setters for business logic
    public void setCode(String code) {
        this.code = code.toUpperCase();
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId.toLowerCase();
    }

    // Helper methods will be added when User, Donor, Recipient entities are created
}
