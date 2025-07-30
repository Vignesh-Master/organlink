package com.organlink.service;

import com.organlink.exception.AuthenticationException;
import com.organlink.exception.ResourceNotFoundException;
import com.organlink.model.dto.HospitalDto;
import com.organlink.model.dto.LoginRequest;
import com.organlink.model.dto.LoginResponse;
import com.organlink.model.entity.Hospital;
import com.organlink.model.entity.HospitalUser;
import com.organlink.utils.mapper.HospitalMapper;
import com.organlink.repository.HospitalRepository;
import com.organlink.repository.HospitalUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for hospital authentication operations
 * Handles login, token generation, and session management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    private final HospitalUserRepository hospitalUserRepository;
    private final HospitalRepository hospitalRepository;
    private final HospitalMapper hospitalMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate hospital user and generate login response
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUserId());

        // Find hospital user
        HospitalUser hospitalUser = hospitalUserRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        // Check if account is locked
        if (hospitalUser.isAccountLocked()) {
            logger.warn("Login attempt for locked account: {}", loginRequest.getUserId());
            throw new AuthenticationException("Account is temporarily locked. Please try again later.");
        }

        // Check if account is active
        if (!hospitalUser.getIsActive()) {
            logger.warn("Login attempt for inactive account: {}", loginRequest.getUserId());
            throw new AuthenticationException("Account is inactive. Please contact administrator.");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), hospitalUser.getPassword())) {
            handleFailedLogin(hospitalUser);
            throw new AuthenticationException("Invalid credentials");
        }

        // Get hospital information
        Hospital hospital = hospitalRepository.findById(hospitalUser.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        // Update successful login
        hospitalUserRepository.updateLastLogin(hospitalUser.getUserId(), LocalDateTime.now());

        // Generate token (simplified - in production use JWT)
        String token = generateToken(hospitalUser, hospital);

        // Create response
        HospitalDto hospitalDto = hospitalMapper.toDto(hospital);
        
        LoginResponse response = new LoginResponse(
                token,
                hospital.getTenantId(),
                hospitalDto,
                System.currentTimeMillis() + (24 * 60 * 60 * 1000), // 24 hours
                "Login successful"
        );

        logger.info("Successful login for user: {} (Hospital: {})", 
                loginRequest.getUserId(), hospital.getName());

        return response;
    }

    /**
     * Handle failed login attempt
     */
    private void handleFailedLogin(HospitalUser hospitalUser) {
        hospitalUserRepository.incrementFailedLoginAttempts(hospitalUser.getUserId());
        
        int attempts = hospitalUser.getFailedLoginAttempts() + 1;
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
            hospitalUserRepository.lockAccount(hospitalUser.getUserId(), lockUntil);
            logger.warn("Account locked due to too many failed attempts: {}", hospitalUser.getUserId());
        }
    }

    /**
     * Generate authentication token
     * In production, this should use JWT with proper signing
     */
    private String generateToken(HospitalUser hospitalUser, Hospital hospital) {
        // Simplified token generation - in production use JWT
        String tokenData = hospitalUser.getUserId() + ":" + hospital.getTenantId() + ":" + System.currentTimeMillis();
        return "HOSPITAL_TOKEN_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Validate token and get hospital information
     * In production, this should validate JWT token
     */
    public HospitalDto validateToken(String token) {
        // Simplified token validation - in production validate JWT
        if (token == null || !token.startsWith("HOSPITAL_TOKEN_")) {
            throw new AuthenticationException("Invalid token");
        }

        // For now, return null - in production, extract hospital info from JWT
        throw new AuthenticationException("Token validation not implemented");
    }

    /**
     * Logout (invalidate token)
     */
    public void logout(String token) {
        logger.info("Logout request for token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
        // In production, add token to blacklist or handle JWT expiration
    }
}
