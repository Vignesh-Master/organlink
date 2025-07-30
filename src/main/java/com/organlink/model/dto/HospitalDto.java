package com.organlink.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Hospital entity
 */
public class HospitalDto {

    private Long id;

    @NotBlank(message = "Hospital name is required")
    @Size(min = 2, max = 200, message = "Hospital name must be between 2 and 200 characters")
    private String name;

    @NotBlank(message = "Hospital code is required")
    @Size(min = 2, max = 20, message = "Hospital code must be between 2 and 20 characters")
    private String code;

    private String address;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotNull(message = "State ID is required")
    private Long stateId;

    private String stateName;
    private String stateCode;
    private String countryName;
    private String countryCode;

    @Size(max = 50, message = "License number cannot exceed 50 characters")
    private String licenseNumber;

    private Boolean isActive = true;

    @NotBlank(message = "Tenant ID is required")
    @Size(min = 3, max = 50, message = "Tenant ID must be between 3 and 50 characters")
    private String tenantId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public HospitalDto() {}

    public HospitalDto(String name, String code, String tenantId, Long stateId) {
        this.name = name;
        this.code = code;
        this.tenantId = tenantId;
        this.stateId = stateId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        this.code = code;
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

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
        this.tenantId = tenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "HospitalDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
