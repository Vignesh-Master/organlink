package com.organlink.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for OrganType entity
 */
public class OrganTypeDto {

    private Long id;

    @NotBlank(message = "Organ type name is required")
    @Size(min = 2, max = 100, message = "Organ type name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Preservation time is required")
    @Min(value = 1, message = "Preservation time must be at least 1 hour")
    private Integer preservationTimeHours;

    private String compatibilityFactors;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public OrganTypeDto() {}

    public OrganTypeDto(String name, String description, Integer preservationTimeHours) {
        this.name = name;
        this.description = description;
        this.preservationTimeHours = preservationTimeHours;
    }

    public OrganTypeDto(Long id, String name, String description, Integer preservationTimeHours) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preservationTimeHours = preservationTimeHours;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPreservationTimeHours() {
        return preservationTimeHours;
    }

    public void setPreservationTimeHours(Integer preservationTimeHours) {
        this.preservationTimeHours = preservationTimeHours;
    }

    public String getCompatibilityFactors() {
        return compatibilityFactors;
    }

    public void setCompatibilityFactors(String compatibilityFactors) {
        this.compatibilityFactors = compatibilityFactors;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    // Helper methods
    public boolean isActive() {
        return isActive != null && isActive;
    }

    @Override
    public String toString() {
        return "OrganTypeDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", preservationTimeHours=" + preservationTimeHours +
                ", isActive=" + isActive +
                '}';
    }
}
