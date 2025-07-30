package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * Entity representing different types of organs available for transplantation
 * Contains metadata about preservation time, compatibility factors, etc.
 */
@Entity
@Table(name = "organ_types",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "name")
       },
       indexes = {
           @Index(name = "idx_organ_type_name", columnList = "name"),
           @Index(name = "idx_organ_type_preservation", columnList = "preservation_time_hours")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OrganType extends BaseEntity {

    @NotBlank(message = "Organ type name is required")
    @Size(min = 2, max = 100, message = "Organ type name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Preservation time is required")
    @Min(value = 1, message = "Preservation time must be at least 1 hour")
    @Column(name = "preservation_time_hours", nullable = false)
    private Integer preservationTimeHours;

    @Column(name = "compatibility_factors", columnDefinition = "JSON")
    private String compatibilityFactors;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Custom constructors for convenience
    public OrganType(String name, String description, Integer preservationTimeHours) {
        this.name = name;
        this.description = description;
        this.preservationTimeHours = preservationTimeHours;
    }

    public OrganType(String name, String description, Integer preservationTimeHours, String compatibilityFactors) {
        this.name = name;
        this.description = description;
        this.preservationTimeHours = preservationTimeHours;
        this.compatibilityFactors = compatibilityFactors;
    }

    // Helper method for convenience
    public boolean isActive() {
        return isActive != null && isActive;
    }
}
