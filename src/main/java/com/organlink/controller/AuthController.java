package com.organlink.controller;

import com.organlink.model.dto.LoginRequest;
import com.organlink.model.dto.LoginResponse;
import com.organlink.service.AuthenticationService;
import com.organlink.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for hospital authentication operations
 * Handles login, logout, and token validation
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Hospital authentication APIs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authenticationService;

    /**
     * Hospital login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "Hospital login", description = "Authenticate hospital user and get access token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        logger.info("Login request for user: {}", loginRequest.getUserId());
        
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        
        ApiResponse<LoginResponse> response = ApiResponse.success(
            "Login successful", 
            loginResponse
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Hospital logout endpoint
     */
    @PostMapping("/logout")
    @Operation(summary = "Hospital logout", description = "Logout hospital user and invalidate token")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(description = "Authorization token") 
            @RequestHeader("Authorization") String authHeader) {
        
        String token = extractToken(authHeader);
        logger.info("Logout request");
        
        authenticationService.logout(token);
        
        ApiResponse<Void> response = ApiResponse.success("Logout successful", null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validate token endpoint
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate authentication token and get hospital info")
    public ResponseEntity<ApiResponse<Object>> validateToken(
            @Parameter(description = "Authorization token") 
            @RequestHeader("Authorization") String authHeader) {
        
        String token = extractToken(authHeader);
        logger.debug("Token validation request");
        
        // For now, just return success if token format is correct
        if (token != null && token.startsWith("HOSPITAL_TOKEN_")) {
            ApiResponse<Object> response = ApiResponse.success("Token is valid", null);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Object> response = ApiResponse.error("Invalid token", null);
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Get current hospital info from token
     */
    @GetMapping("/me")
    @Operation(summary = "Get current hospital", description = "Get current hospital information from token")
    public ResponseEntity<ApiResponse<Object>> getCurrentHospital(
            @Parameter(description = "Authorization token") 
            @RequestHeader("Authorization") String authHeader) {
        
        String token = extractToken(authHeader);
        logger.debug("Get current hospital request");
        
        // For now, return placeholder response
        ApiResponse<Object> response = ApiResponse.success(
            "Current hospital information", 
            "Hospital info will be extracted from JWT token"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Extract token from Authorization header
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
