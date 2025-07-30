package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

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

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

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

    // Constructors
    public Hospital() {}

    public Hospital(String name, String code, String tenantId, State state) {
        this.name = name;
        this.code = code;
        this.tenantId = tenantId;
        this.state = state;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code.toUpperCase();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId.toLowerCase();
    }

    // Helper methods will be added when User, Donor, Recipient entities are created

    @Override
    public String toString() {
        return "Hospital{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
