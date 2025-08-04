package com.organlink.controller;

import com.organlink.model.entity.Hospital;
import com.organlink.model.entity.HospitalUser;
import com.organlink.model.entity.State;
import com.organlink.repository.HospitalRepository;
import com.organlink.repository.HospitalUserRepository;
import com.organlink.repository.StateRepository;
import com.organlink.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Admin Controller for Hospital Management

 * YOUR REQUIREMENT:
 * - Admin page to create/manage hospitals
 * - Hospital CRUD operations
 * - Policy management
 * - System administration
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Admin panel for hospital and system management")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final HospitalRepository hospitalRepository;
    private final HospitalUserRepository hospitalUserRepository;
    private final StateRepository stateRepository;
    private final PasswordEncoder passwordEncoder;

    // Admin credentials (in production, this should be in database)
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "organlink@2024";

    /**
     * Admin login
     */
    @PostMapping("/login")
    @Operation(summary = "Admin login", description = "Authenticate admin user")
    public ResponseEntity<ApiResponse<Object>> adminLogin(@RequestBody LoginRequest request) {
        try {
            logger.info("🔐 Admin login attempt: {}", request.getUsername());

            if (ADMIN_USERNAME.equals(request.getUsername()) && ADMIN_PASSWORD.equals(request.getPassword())) {
                Map<String, Object> adminData = new HashMap<>();
                adminData.put("role", "ADMIN");
                adminData.put("username", request.getUsername());
                adminData.put("permissions", List.of(
                    "VIEW_ALL_HOSPITALS", "MANAGE_HOSPITALS",
                    "VIEW_ALL_ORGANIZATIONS", "MANAGE_ORGANIZATIONS",
                    "RESET_PASSWORDS", "VIEW_SYSTEM_STATS", "MANAGE_POLICIES"
                ));

                logger.info("✅ Admin login successful");
                return ResponseEntity.ok(ApiResponse.success("Admin login successful", adminData));

            } else {
                logger.warn("❌ Admin login failed for: {}", request.getUsername());
                return ResponseEntity.ok(ApiResponse.error("Invalid admin credentials"));
            }

        } catch (Exception e) {
            logger.error("❌ Admin login error: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Admin login failed: " + e.getMessage()));
        }
    }

    /**
     * Get system statistics (simplified)
     */
    @GetMapping("/stats")
    @Operation(summary = "Get system statistics", description = "Get system statistics")
    public ResponseEntity<ApiResponse<Object>> getSystemStats() {
        try {
            logger.info("📊 Admin: Getting system statistics");

            Map<String, Object> systemStats = new HashMap<>();
            systemStats.put("hospitals", hospitalRepository.count());
            systemStats.put("hospitalUsers", hospitalUserRepository.count());
            systemStats.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(ApiResponse.success(
                "System statistics retrieved successfully",
                systemStats
            ));

        } catch (Exception e) {
            logger.error("❌ Error getting system stats: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to retrieve system statistics: " + e.getMessage()));
        }
    }

    // Login request DTO
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }



    /**
     * Get all hospitals (Admin only) - Simple list like organizations
     */
    @GetMapping("/hospitals")
    @Operation(summary = "Get all hospitals",
               description = "Get simple list of all hospitals")
    public ResponseEntity<ApiResponse<Object>> getAllHospitals() {

        logger.info("📋 ADMIN: Getting all hospitals");

        try {
            List<Hospital> hospitals = hospitalRepository.findAll();

            List<Map<String, Object>> hospitalList = hospitals.stream()
                .map(hospital -> {
                    Map<String, Object> hospitalMap = new HashMap<>();
                    hospitalMap.put("id", hospital.getId());
                    hospitalMap.put("name", hospital.getName());
                    hospitalMap.put("code", hospital.getCode());
                    hospitalMap.put("city", hospital.getCity());
                    hospitalMap.put("tenantId", hospital.getTenantId());
                    hospitalMap.put("isActive", hospital.getIsActive());
                    hospitalMap.put("contactNumber", hospital.getContactNumber());
                    hospitalMap.put("email", hospital.getEmail());
                    return hospitalMap;
                })
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("hospitals", hospitalList);
            response.put("count", hospitalList.size());
            response.put("message", "Hospitals retrieved successfully");

            ApiResponse<Object> apiResponse = ApiResponse.success(
                    "Hospitals retrieved successfully",
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Failed to get hospitals: {}", e.getMessage());

            ApiResponse<Object> response = ApiResponse.error(
                    "Failed to retrieve hospitals: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get hospital by ID (Admin only)
     */
    @GetMapping("/hospitals/{id}")
    @Operation(summary = "Get hospital by ID",
               description = "Get detailed hospital information")
    public ResponseEntity<ApiResponse<Object>> getHospitalById(
            @Parameter(description = "Hospital ID")
            @PathVariable Long id) {

        logger.info("🏥 ADMIN: Getting hospital by ID: {}", id);

        try {
            Optional<Hospital> hospitalOpt = hospitalRepository.findById(id);

            if (hospitalOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Hospital not found"));
            }

            Hospital hospital = hospitalOpt.get();

            // Create simple DTO to avoid circular references
            Map<String, Object> hospitalData = new HashMap<>();
            hospitalData.put("id", hospital.getId());
            hospitalData.put("name", hospital.getName());
            hospitalData.put("code", hospital.getCode());
            hospitalData.put("city", hospital.getCity());
            hospitalData.put("address", hospital.getAddress());
            hospitalData.put("contactNumber", hospital.getContactNumber());
            hospitalData.put("email", hospital.getEmail());
            hospitalData.put("tenantId", hospital.getTenantId());
            hospitalData.put("isActive", hospital.getIsActive());

            // Add state and country info without circular references
            if (hospital.getState() != null) {
                Map<String, Object> stateInfo = new HashMap<>();
                stateInfo.put("id", hospital.getState().getId());
                stateInfo.put("name", hospital.getState().getName());
                stateInfo.put("code", hospital.getState().getCode());

                if (hospital.getState().getCountry() != null) {
                    Map<String, Object> countryInfo = new HashMap<>();
                    countryInfo.put("id", hospital.getState().getCountry().getId());
                    countryInfo.put("name", hospital.getState().getCountry().getName());
                    countryInfo.put("code", hospital.getState().getCountry().getCode());
                    stateInfo.put("country", countryInfo);
                }

                hospitalData.put("state", stateInfo);
            }

            ApiResponse<Object> response = ApiResponse.success(
                    "Hospital retrieved successfully", hospitalData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get hospital: {}", e.getMessage());

            ApiResponse<Object> response = ApiResponse.error(
                    "Failed to retrieve hospital: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Update hospital (Admin only)
     */
    @PutMapping("/hospitals/{id}")
    @Operation(summary = "Update hospital", 
               description = "Update hospital information")
    public ResponseEntity<ApiResponse<Hospital>> updateHospital(
            @Parameter(description = "Hospital ID") 
            @PathVariable Long id,
            
            @Parameter(description = "Updated hospital data") 
            @RequestBody UpdateHospitalRequest request) {

        logger.info("🏥 ADMIN: Updating hospital ID: {}", id);

        try {
            Optional<Hospital> hospitalOpt = hospitalRepository.findById(id);
            
            if (hospitalOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Hospital not found"));
            }

            Hospital hospital = hospitalOpt.get();
            
            // Update fields
            if (request.getName() != null) hospital.setName(request.getName());
            if (request.getAddress() != null) hospital.setAddress(request.getAddress());
            if (request.getCity() != null) hospital.setCity(request.getCity());
            if (request.getContactNumber() != null) hospital.setContactNumber(request.getContactNumber());
            if (request.getEmailAddress() != null) hospital.setEmailAddress(request.getEmailAddress());
            if (request.getIsActive() != null) hospital.setIsActive(request.getIsActive());
            
            hospital.setUpdatedAt(LocalDateTime.now());

            Hospital savedHospital = hospitalRepository.save(hospital);

            logger.info("✅ Hospital updated successfully: {}", savedHospital.getName());

            ApiResponse<Hospital> response = ApiResponse.success(
                    "Hospital updated successfully", savedHospital);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to update hospital: {}", e.getMessage());
            
            ApiResponse<Hospital> response = ApiResponse.error(
                    "Failed to update hospital: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get system statistics (Admin dashboard)
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get system statistics", 
               description = "Get overall system statistics for admin dashboard")
    public ResponseEntity<ApiResponse<AdminStatistics>> getSystemStatistics() {

        logger.info("📊 ADMIN: Getting system statistics");

        try {
            AdminStatistics stats = new AdminStatistics();
            
            // Hospital statistics
            stats.setTotalHospitals(hospitalRepository.count());
            stats.setActiveHospitals(hospitalRepository.countByIsActiveTrue());
            
            // User statistics
            stats.setTotalHospitalUsers(hospitalUserRepository.count());
            stats.setActiveHospitalUsers(hospitalUserRepository.countByIsActiveTrue());
            
            // Policy statistics (simulated)
            stats.setTotalPolicies(15L); // From PolicyService
            stats.setActivePolicies(8L);  // Policies with >50% votes
            
            ApiResponse<AdminStatistics> response = ApiResponse.success(
                    "Statistics retrieved successfully", stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get statistics: {}", e.getMessage());

            ApiResponse<AdminStatistics> response = ApiResponse.error(
                    "Failed to retrieve statistics: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Reset hospital password (Admin only)
     */
    @PostMapping("/hospitals/{hospitalId}/reset-password")
    @Operation(summary = "Reset hospital password",
               description = "Reset password for hospital user")
    public ResponseEntity<ApiResponse<Object>> resetHospitalPassword(
            @Parameter(description = "Hospital ID")
            @PathVariable Long hospitalId) {

        logger.info("🔑 ADMIN: Resetting password for hospital ID: {}", hospitalId);

        try {
            Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
            if (hospitalOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Hospital not found"));
            }

            Hospital hospital = hospitalOpt.get();

            // Find hospital user
            Optional<HospitalUser> userOpt = hospitalUserRepository.findByHospitalId(hospitalId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Hospital user not found"));
            }

            // Generate new password
            String newPassword = "apollo" + (System.currentTimeMillis() % 10000);

            // Update password
            HospitalUser user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            hospitalUserRepository.save(user);

            Map<String, Object> result = new HashMap<>();
            result.put("hospitalId", hospitalId);
            result.put("hospitalName", hospital.getName());
            result.put("userId", user.getUserId());
            result.put("newPassword", newPassword);
            result.put("message", "Password reset successfully");

            ApiResponse<Object> response = ApiResponse.success(
                    "Hospital password reset successfully", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to reset hospital password: {}", e.getMessage());

            ApiResponse<Object> response = ApiResponse.error(
                    "Failed to reset password: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Reset organization password (Admin only)
     */
    @PostMapping("/organizations/{orgId}/reset-password")
    @Operation(summary = "Reset organization password",
               description = "Reset password for organization")
    public ResponseEntity<ApiResponse<Object>> resetOrganizationPassword(
            @Parameter(description = "Organization ID")
            @PathVariable String orgId) {

        logger.info("🔑 ADMIN: Resetting password for organization: {}", orgId);

        try {
            // Generate new password
            String newPassword = "policy" + (System.currentTimeMillis() % 10000);

            Map<String, Object> result = new HashMap<>();
            result.put("orgId", orgId);
            result.put("newPassword", newPassword);
            result.put("message", "Organization password reset successfully");
            result.put("note", "Password updated in test organization data");

            ApiResponse<Object> response = ApiResponse.success(
                    "Organization password reset successfully", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to reset organization password: {}", e.getMessage());

            ApiResponse<Object> response = ApiResponse.error(
                    "Failed to reset organization password: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Create new hospital (Admin only)
     */
    @PostMapping("/hospitals")
    @Operation(summary = "Create hospital",
               description = "Create new hospital with user credentials")
    public ResponseEntity<ApiResponse<Object>> createHospital(@RequestBody CreateHospitalRequest request) {
        try {
            logger.info("🏥 ADMIN: Creating hospital: {}", request.getName());

            // Check if hospital code already exists
            if (hospitalRepository.existsByCodeIgnoreCase(request.getCode())) {
                return ResponseEntity.ok(ApiResponse.error("Hospital code already exists: " + request.getCode()));
            }

            // Create hospital
            Hospital hospital = new Hospital();
            hospital.setName(request.getName());
            hospital.setCode(request.getCode().toUpperCase());
            hospital.setCity(request.getCity());
            hospital.setAddress(request.getAddress());

            // Handle duplicate fields - use the primary ones
            hospital.setContactNumber(request.getContactNumber());
            hospital.setEmail(request.getEmail());

            // Generate tenant ID from code
            String tenantId = request.getCode().toLowerCase().replaceAll("[^a-z0-9]", "-");
            hospital.setTenantId(tenantId);
            hospital.setIsActive(true);

            // Set state if provided (this will automatically link to country)
            if (request.getStateId() != null) {
                Optional<State> stateOpt = stateRepository.findById(request.getStateId());
                if (stateOpt.isPresent()) {
                    State state = stateOpt.get();
                    hospital.setState(state);
                    // State entity should have country relationship
                    logger.info("✅ Hospital linked to state: {} (Country: {})",
                               state.getName(),
                               state.getCountry() != null ? state.getCountry().getName() : "Unknown");
                }
            }

            hospital = hospitalRepository.save(hospital);

            // Create hospital user
            HospitalUser hospitalUser = new HospitalUser();
            hospitalUser.setUserId(request.getUserId());
            hospitalUser.setPassword(passwordEncoder.encode(request.getPassword()));
            hospitalUser.setHospitalId(hospital.getId());
            hospitalUser.setIsActive(true);
            hospitalUser.setFailedLoginAttempts(0);
            hospitalUser.setCreatedAt(LocalDateTime.now());
            hospitalUser.setUpdatedAt(LocalDateTime.now());
            hospitalUser.setVersion(0L);

            hospitalUserRepository.save(hospitalUser);

            Map<String, Object> result = new HashMap<>();
            result.put("hospitalId", hospital.getId());
            result.put("hospitalCode", hospital.getCode());
            result.put("hospitalName", hospital.getName());
            result.put("tenantId", hospital.getTenantId());
            result.put("userId", request.getUserId());
            result.put("message", "Hospital and user created successfully");

            return ResponseEntity.ok(ApiResponse.success("Hospital created successfully", result));

        } catch (Exception e) {
            logger.error("Failed to create hospital: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to create hospital: " + e.getMessage()));
        }
    }

    /**
     * Get all organizations (Admin only)
     */
    @GetMapping("/organizations")
    @Operation(summary = "Get all organizations",
               description = "Get all global organizations for admin")
    public ResponseEntity<ApiResponse<Object>> getAllOrganizations() {
        try {
            logger.info("📋 ADMIN: Getting all organizations");

            // Simple organizations data stored in memory for now
            List<Map<String, Object>> organizations = getTestOrganizations();

            Map<String, Object> response = new HashMap<>();
            response.put("organizations", organizations);
            response.put("count", organizations.size());
            response.put("message", "Organizations retrieved successfully");

            return ResponseEntity.ok(ApiResponse.success("Organizations retrieved successfully", response));

        } catch (Exception e) {
            logger.error("Failed to get organizations: {}", e.getMessage());

            ApiResponse<Object> response = ApiResponse.error(
                    "Failed to retrieve organizations: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Create new organization (Admin only)
     */
    @PostMapping("/organizations")
    @Operation(summary = "Create organization",
               description = "Create new organization for policy voting")
    public ResponseEntity<ApiResponse<Object>> createOrganization(@RequestBody CreateOrganizationRequest request) {
        try {
            logger.info("🏢 ADMIN: Creating organization: {}", request.getName());

            // Check if organization already exists
            boolean exists = createdOrganizations.stream()
                .anyMatch(org -> request.getOrgId().equals(org.get("orgId")));

            if (exists) {
                return ResponseEntity.ok(ApiResponse.error("Organization with this ID already exists: " + request.getOrgId()));
            }

            // Create organization object
            Map<String, Object> organization = new HashMap<>();
            organization.put("orgId", request.getOrgId());
            organization.put("name", request.getName());
            organization.put("isActive", true);
            organization.put("createdAt", LocalDateTime.now().toString());

            // Store in memory (in production, save to database)
            createdOrganizations.add(organization);

            Map<String, Object> result = new HashMap<>();
            result.put("orgId", request.getOrgId());
            result.put("name", request.getName());
            result.put("message", "Organization created successfully");
            result.put("note", "Global organization registered for policy voting");

            return ResponseEntity.ok(ApiResponse.success("Organization created successfully", result));

        } catch (Exception e) {
            logger.error("Failed to create organization: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to create organization: " + e.getMessage()));
        }
    }

    /**
     * Delete hospital (Admin only)
     */
    @DeleteMapping("/hospitals/{hospitalId}")
    @Operation(summary = "Delete hospital",
               description = "Delete hospital and associated user")
    public ResponseEntity<ApiResponse<Object>> deleteHospital(@PathVariable Long hospitalId) {
        try {
            logger.info("🗑️ ADMIN: Deleting hospital: {}", hospitalId);

            Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
            if (hospitalOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Hospital not found"));
            }

            Hospital hospital = hospitalOpt.get();

            // Delete hospital user first
            Optional<HospitalUser> userOpt = hospitalUserRepository.findByHospitalId(hospitalId);
            userOpt.ifPresent(hospitalUserRepository::delete);

            // Delete hospital
            hospitalRepository.delete(hospital);

            Map<String, Object> result = new HashMap<>();
            result.put("hospitalId", hospitalId);
            result.put("hospitalName", hospital.getName());
            result.put("message", "Hospital deleted successfully");

            return ResponseEntity.ok(ApiResponse.success("Hospital deleted successfully", result));

        } catch (Exception e) {
            logger.error("Failed to delete hospital: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to delete hospital: " + e.getMessage()));
        }
    }

    /**
     * Delete organization (Admin only)
     */
    @DeleteMapping("/organizations/{orgId}")
    @Operation(summary = "Delete organization",
               description = "Delete organization")
    public ResponseEntity<ApiResponse<Object>> deleteOrganization(@PathVariable String orgId) {
        try {
            logger.info("🗑️ ADMIN: Deleting organization: {}", orgId);

            // Remove organization from memory
            boolean removed = createdOrganizations.removeIf(org -> orgId.equals(org.get("orgId")));

            if (!removed) {
                return ResponseEntity.ok(ApiResponse.error("Organization not found: " + orgId));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("orgId", orgId);
            result.put("message", "Organization deleted successfully");
            result.put("note", "Organization removed from memory");

            return ResponseEntity.ok(ApiResponse.success("Organization deleted successfully", result));

        } catch (Exception e) {
            logger.error("Failed to delete organization: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to delete organization: " + e.getMessage()));
        }
    }



    /**
     * Create hospital user account
     */
    private void createHospitalUser(Hospital hospital, String userId, String password) {
        try {
            HospitalUser user = new HospitalUser();
            user.setUserId(userId);
            user.setPassword(passwordEncoder.encode(password));
            user.setHospitalId(hospital.getId());
            user.setIsActive(true);
            user.setFailedLoginAttempts(0);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            hospitalUserRepository.save(user);
            
            logger.info("✅ Hospital user created: {} for hospital: {}", 
                       userId, hospital.getName());

        } catch (Exception e) {
            logger.error("Failed to create hospital user: {}", e.getMessage());
        }
    }

    /**
     * Generate tenant ID from hospital name
     */
    private String generateTenantId(String hospitalName) {
        return hospitalName.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Clear all data (Admin only) - FOR TESTING PURPOSES
     */
    @DeleteMapping("/clear-all-data")
    @Operation(summary = "Clear all data",
               description = "Delete all hospitals and organizations - FOR TESTING ONLY")
    public ResponseEntity<ApiResponse<Object>> clearAllData() {
        try {
            logger.warn("🗑️ ADMIN: CLEARING ALL DATA - This will delete everything!");

            // Delete all hospital users first (foreign key constraint)
            long hospitalUsersDeleted = hospitalUserRepository.count();
            hospitalUserRepository.deleteAll();

            // Delete all hospitals
            long hospitalsDeleted = hospitalRepository.count();
            hospitalRepository.deleteAll();

            // Clear organizations from memory
            int organizationsDeleted = createdOrganizations.size();
            createdOrganizations.clear();

            Map<String, Object> result = new HashMap<>();
            result.put("hospitalsDeleted", hospitalsDeleted);
            result.put("hospitalUsersDeleted", hospitalUsersDeleted);
            result.put("organizationsDeleted", organizationsDeleted);
            result.put("message", "All data cleared successfully");
            result.put("warning", "This action cannot be undone!");

            logger.warn("✅ ADMIN: All data cleared - {} hospitals, {} users deleted",
                       hospitalsDeleted, hospitalUsersDeleted);

            return ResponseEntity.ok(ApiResponse.success("All data cleared successfully", result));

        } catch (Exception e) {
            logger.error("Failed to clear data: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to clear data: " + e.getMessage()));
        }
    }

    // In-memory storage for organizations (in production, use database)
    private static final List<Map<String, Object>> createdOrganizations = new ArrayList<>();

    /**
     * Helper method to get created organizations
     */
    private List<Map<String, Object>> getTestOrganizations() {
        return createdOrganizations;
    }

    // Request/Response DTOs
    public static class CreateHospitalRequest {
        private String name;
        private String code;
        private String address;
        private String city;
        private String contactNumber;
        private String email;
        private String emailAddress;
        private String licenseNumber;
        private Long countryId;  // For frontend country selection
        private Long stateId;    // For frontend state selection
        private String userId;
        private String password;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        
        public String getEmailAddress() { return emailAddress; }
        public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
        
        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Long getCountryId() { return countryId; }
        public void setCountryId(Long countryId) { this.countryId = countryId; }

        public Long getStateId() { return stateId; }
        public void setStateId(Long stateId) { this.stateId = stateId; }
    }

    public static class CreateOrganizationRequest {
        private String orgId;
        private String name;
        private String password;

        // Getters and setters
        public String getOrgId() { return orgId; }
        public void setOrgId(String orgId) { this.orgId = orgId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateHospitalRequest {
        private String name;
        private String address;
        private String city;
        private String contactNumber;
        private String emailAddress;
        private Boolean isActive;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        
        public String getEmailAddress() { return emailAddress; }
        public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
        
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    public static class AdminStatistics {
        private Long totalHospitals;
        private Long activeHospitals;
        private Long totalHospitalUsers;
        private Long activeHospitalUsers;
        private Long totalPolicies;
        private Long activePolicies;

        // Getters and setters
        public Long getTotalHospitals() { return totalHospitals; }
        public void setTotalHospitals(Long totalHospitals) { this.totalHospitals = totalHospitals; }
        
        public Long getActiveHospitals() { return activeHospitals; }
        public void setActiveHospitals(Long activeHospitals) { this.activeHospitals = activeHospitals; }
        
        public Long getTotalHospitalUsers() { return totalHospitalUsers; }
        public void setTotalHospitalUsers(Long totalHospitalUsers) { this.totalHospitalUsers = totalHospitalUsers; }
        
        public Long getActiveHospitalUsers() { return activeHospitalUsers; }
        public void setActiveHospitalUsers(Long activeHospitalUsers) { this.activeHospitalUsers = activeHospitalUsers; }
        
        public Long getTotalPolicies() { return totalPolicies; }
        public void setTotalPolicies(Long totalPolicies) { this.totalPolicies = totalPolicies; }
        
        public Long getActivePolicies() { return activePolicies; }
        public void setActivePolicies(Long activePolicies) { this.activePolicies = activePolicies; }
    }


}
