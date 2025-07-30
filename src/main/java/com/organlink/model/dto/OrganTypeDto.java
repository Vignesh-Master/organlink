package com.organlink.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for OrganType entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // Custom constructors for convenience
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

    // Helper method for convenience
    public boolean isActive() {
        return isActive != null && isActive;
    }
}
