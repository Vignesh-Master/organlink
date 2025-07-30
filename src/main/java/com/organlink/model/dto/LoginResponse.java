package com.organlink.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for hospital login response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hospital login response")
public class LoginResponse {

    @Schema(description = "Authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Hospital tenant ID", example = "apollo-chennai")
    private String tenantId;

    @Schema(description = "Hospital information")
    private HospitalDto hospital;

    @Schema(description = "Token expiration time in milliseconds", example = "1640995200000")
    private Long expiresAt;

    @Schema(description = "Login success message", example = "Login successful")
    private String message;
}
