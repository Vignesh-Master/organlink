package com.organlink.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for State entity
 */
public class StateDto {

    private Long id;

    @NotBlank(message = "State name is required")
    @Size(min = 2, max = 100, message = "State name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "State code is required")
    @Size(min = 2, max = 10, message = "State code must be between 2 and 10 characters")
    private String code;

    @NotNull(message = "Country ID is required")
    private Long countryId;

    private String countryName;
    private String countryCode;
    private List<HospitalDto> hospitals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public StateDto() {}

    public StateDto(String name, String code, Long countryId) {
        this.name = name;
        this.code = code;
        this.countryId = countryId;
    }

    public StateDto(Long id, String name, String code, Long countryId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.countryId = countryId;
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public List<HospitalDto> getHospitals() {
        return hospitals;
    }

    public void setHospitals(List<HospitalDto> hospitals) {
        this.hospitals = hospitals;
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
        return "StateDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", countryId=" + countryId +
                ", countryName='" + countryName + '\'' +
                '}';
    }
}
