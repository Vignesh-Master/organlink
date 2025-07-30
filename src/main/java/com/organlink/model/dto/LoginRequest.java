package com.organlink.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for hospital login request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hospital login request")
public class LoginRequest {

    @NotBlank(message = "User ID is required")
    @Size(min = 3, max = 50, message = "User ID must be between 3 and 50 characters")
    @Schema(description = "Hospital user ID", example = "ch-001")
    private String userId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Schema(description = "Hospital password", example = "apollo-25")
    private String password;
}
