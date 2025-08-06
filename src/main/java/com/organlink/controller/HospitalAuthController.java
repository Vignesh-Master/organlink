package com.organlink.controller;

import com.organlink.utils.ApiResponse;
import com.organlink.model.entity.Hospital;
import com.organlink.model.entity.HospitalUser;
import com.organlink.repository.HospitalRepository;
import com.organlink.repository.HospitalUserRepository;
import com.organlink.repository.StateRepository;
import com.organlink.repository.CountryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Hospital Authentication Controller
 * Handles hospital login with location-based selection
 */
@RestController
@RequestMapping("/api/v1/hospital/auth")
@RequiredArgsConstructor
@Tag(name = "Hospital Authentication", description = "Hospital login and authentication APIs")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class HospitalAuthController {

    private static final Logger logger = LoggerFactory.getLogger(HospitalAuthController.class);

    private final HospitalRepository hospitalRepository;
    private final HospitalUserRepository hospitalUserRepository;
    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get hospitals by city for frontend dropdown (Country → State → City → Hospital flow)
     */
    @GetMapping("/hospitals-by-city")
    @Operation(summary = "Get hospitals by city",
               description = "Get hospitals in a specific city for frontend dropdown selection")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getHospitalsByCity(
            @Parameter(description = "City name") @RequestParam String city,
            @Parameter(description = "State ID") @RequestParam Long stateId) {

        logger.info("🏥 Getting hospitals by city: {} in state: {}", city, stateId);

        try {
            // Get hospitals by state and filter by city
            List<Hospital> hospitals = hospitalRepository.findByStateIdAndIsActiveTrue(stateId)
                .stream()
                .filter(h -> h.getCity() != null &&
                           h.getCity().toLowerCase().equals(city.toLowerCase()))
                .toList();

            // Convert to dropdown format for frontend
            List<Map<String, Object>> hospitalList = hospitals.stream()
                .map(hospital -> {
                    Map<String, Object> hospitalInfo = new HashMap<>();
                    hospitalInfo.put("id", hospital.getId());
                    hospitalInfo.put("name", hospital.getName());
                    hospitalInfo.put("code", hospital.getCode());
                    hospitalInfo.put("city", hospital.getCity());
                    hospitalInfo.put("address", hospital.getAddress());
                    hospitalInfo.put("contactNumber", hospital.getContactNumber());

                    return hospitalInfo;
                })
                .toList();

            ApiResponse<List<Map<String, Object>>> response = ApiResponse.success(
                "Hospitals retrieved successfully", hospitalList);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get hospitals by city: {}", e.getMessage());
            ApiResponse<List<Map<String, Object>>> response = ApiResponse.error(
                "Failed to retrieve hospitals: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get cities by state for frontend dropdown
     */
    @GetMapping("/cities-by-state")
    @Operation(summary = "Get cities by state",
               description = "Get all cities that have hospitals in a specific state")
    public ResponseEntity<ApiResponse<List<String>>> getCitiesByState(
            @Parameter(description = "State ID") @RequestParam Long stateId) {

        logger.info("🏙️ Getting cities by state: {}", stateId);

        try {
            List<String> cities = hospitalRepository.findByStateIdAndIsActiveTrue(stateId)
                .stream()
                .map(Hospital::getCity)
                .filter(city -> city != null && !city.trim().isEmpty())
                .distinct()
                .sorted()
                .toList();

            ApiResponse<List<String>> response = ApiResponse.success(
                "Cities retrieved successfully", cities);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get cities by state: {}", e.getMessage());
            ApiResponse<List<String>> response = ApiResponse.error(
                "Failed to retrieve cities: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Hospital login with location and credentials
     */
    @PostMapping("/login")
    @Operation(summary = "Hospital login", 
               description = "Authenticate hospital user with location selection and credentials")
    public ResponseEntity<ApiResponse<Map<String, Object>>> hospitalLogin(
            @RequestBody HospitalLoginRequest request) {

        logger.info("🔐 Hospital login attempt - Hospital: {}, User: {}", 
                   request.getHospitalId(), request.getUserId());

        try {
            // Validate hospital exists and is active
            Optional<Hospital> hospitalOpt = hospitalRepository.findById(request.getHospitalId());
            if (hospitalOpt.isEmpty() || !hospitalOpt.get().getIsActive()) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Hospital not found or inactive"));
            }

            Hospital hospital = hospitalOpt.get();

            // Find hospital user
            Optional<HospitalUser> userOpt = hospitalUserRepository
                .findByUserIdAndHospitalId(request.getUserId(), request.getHospitalId());
            
            if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid user credentials"));
            }

            HospitalUser hospitalUser = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), hospitalUser.getPassword())) {
                // Increment failed login attempts
                hospitalUser.setFailedLoginAttempts(hospitalUser.getFailedLoginAttempts() + 1);
                hospitalUserRepository.save(hospitalUser);
                
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid password"));
            }

            // Reset failed login attempts on successful login
            hospitalUser.setFailedLoginAttempts(0);
            hospitalUserRepository.save(hospitalUser);

            // Generate tenant ID
            String tenantId = hospital.getCode().toLowerCase().replace("_", "-");

            // Prepare response data
            Map<String, Object> loginData = new HashMap<>();
            loginData.put("token", "hospital-token-" + hospitalUser.getId());
            loginData.put("tenantId", tenantId);
            loginData.put("hospitalId", hospital.getId());
            loginData.put("userId", hospitalUser.getUserId());
            
            // Hospital information
            Map<String, Object> hospitalInfo = new HashMap<>();
            hospitalInfo.put("id", hospital.getId());
            hospitalInfo.put("name", hospital.getName());
            hospitalInfo.put("code", hospital.getCode());
            hospitalInfo.put("city", hospital.getCity());
            hospitalInfo.put("address", hospital.getAddress());
            hospitalInfo.put("contactNumber", hospital.getContactNumber());
            hospitalInfo.put("email", hospital.getEmail());
            
            if (hospital.getState() != null) {
                hospitalInfo.put("stateName", hospital.getState().getName());
                hospitalInfo.put("stateCode", hospital.getState().getCode());
            }
            
            loginData.put("hospital", hospitalInfo);

            logger.info("✅ Hospital login successful - {}: {}", hospital.getName(), hospitalUser.getUserId());

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Hospital login successful", loginData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Hospital login failed: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Validate hospital session
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate hospital session", 
               description = "Validate hospital authentication token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateSession(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("🔍 Validating hospital session for tenant: {}", tenantId);

        try {
            // In production, validate actual JWT token
            // For now, return success if tenant ID is provided
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("valid", true);
            sessionData.put("tenantId", tenantId);

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Session valid", sessionData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Session validation failed: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Session validation failed");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Hospital logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Hospital logout", description = "Logout hospital user")
    public ResponseEntity<ApiResponse<String>> hospitalLogout(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("🚪 Hospital logout for tenant: {}", tenantId);

        // In production, invalidate JWT token
        ApiResponse<String> response = ApiResponse.success("Logout successful", null);
        return ResponseEntity.ok(response);
    }

    /**
     * Hospital Login Request DTO
     */
    public static class HospitalLoginRequest {
        private Long hospitalId;
        private String userId;
        private String password;

        // Getters and setters
        public Long getHospitalId() { return hospitalId; }
        public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
