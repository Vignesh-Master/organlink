package com.organlink.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Lightweight DTO for patient list/summary view
 * Contains only essential information for listing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Patient summary information")
public class PatientSummaryDto {

    @Schema(description = "Patient ID", example = "1")
    private Long id;

    @Schema(description = "Patient name", example = "Rajesh Kumar")
    private String patientName;

    @Schema(description = "Age", example = "45")
    private Integer age;

    @Schema(description = "Blood type", example = "O+")
    private String bloodType;

    @Schema(description = "Gender", example = "Male")
    private String gender;

    @Schema(description = "Needed organ", example = "Kidney")
    private String organNeeded;

    @Schema(description = "Urgency level", example = "HIGH")
    private String urgencyLevel;

    @Schema(description = "Status", example = "WAITING")
    private String status;

    @Schema(description = "Contact number", example = "+91-98765-43210")
    private String contactNumber;

    @Schema(description = "Registration date")
    private LocalDateTime registrationDate;

    @Schema(description = "Waiting list date")
    private LocalDateTime waitingListDate;

    @Schema(description = "Hospital name", example = "Apollo Hospital Mumbai")
    private String hospitalName;
}
