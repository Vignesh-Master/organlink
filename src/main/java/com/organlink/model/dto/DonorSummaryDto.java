package com.organlink.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Lightweight DTO for donor list/summary view
 * Contains only essential information for listing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Donor summary information")
public class DonorSummaryDto {

    @Schema(description = "Donor ID", example = "1")
    private Long id;

    @Schema(description = "Donor name", example = "Priya Sharma")
    private String donorName;

    @Schema(description = "Age", example = "28")
    private Integer age;

    @Schema(description = "Blood type", example = "O+")
    private String bloodType;

    @Schema(description = "Gender", example = "Female")
    private String gender;

    @Schema(description = "Available organ", example = "Kidney")
    private String organAvailable;

    @Schema(description = "Donor type", example = "DECEASED")
    private String donorType;

    @Schema(description = "Status", example = "AVAILABLE")
    private String status;

    @Schema(description = "Contact person", example = "Dr. Suresh")
    private String contactPerson;

    @Schema(description = "Contact number", example = "+91-98765-12345")
    private String contactNumber;

    @Schema(description = "Registration date")
    private LocalDateTime registrationDate;

    @Schema(description = "Hospital name", example = "Apollo Hospital Chennai")
    private String hospitalName;
}
