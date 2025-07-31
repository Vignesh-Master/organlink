package com.organlink.controller;

import com.organlink.model.entity.Hospital;
import com.organlink.model.entity.HospitalUser;
import com.organlink.repository.HospitalRepository;
import com.organlink.repository.HospitalUserRepository;
import com.organlink.service.PolicyService;
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
import java.util.List;
import java.util.Optional;

/**
 * Admin Controller for Hospital Management
 * 
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
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final HospitalRepository hospitalRepository;
    private final HospitalUserRepository hospitalUserRepository;
    private final PolicyService policyService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create new hospital (Admin only)
     */
    @PostMapping("/hospitals")
    @Operation(summary = "Create new hospital", 
               description = "Create a new hospital with admin credentials")
    public ResponseEntity<ApiResponse<Hospital>> createHospital(
            @Parameter(description = "Hospital data") 
            @RequestBody CreateHospitalRequest request) {

        logger.info("🏥 ADMIN: Creating new hospital: {}", request.getName());

        try {
            // Validate request
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Hospital name is required"));
            }

            // Check if hospital already exists
            Optional<Hospital> existingHospital = hospitalRepository
                    .findByNameIgnoreCase(request.getName().trim());
            
            if (existingHospital.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Hospital with this name already exists"));
            }

            // Create hospital
            Hospital hospital = new Hospital();
            hospital.setName(request.getName().trim());
            hospital.setAddress(request.getAddress());
            hospital.setCity(request.getCity());
            hospital.setContactNumber(request.getContactNumber());
            hospital.setEmailAddress(request.getEmailAddress());
            hospital.setLicenseNumber(request.getLicenseNumber());
            hospital.setTenantId(generateTenantId(request.getName()));
            hospital.setIsActive(true);
            hospital.setCreatedAt(LocalDateTime.now());
            hospital.setUpdatedAt(LocalDateTime.now());

            Hospital savedHospital = hospitalRepository.save(hospital);

            // Create hospital user account
            if (request.getUserId() != null && request.getPassword() != null) {
                createHospitalUser(savedHospital, request.getUserId(), request.getPassword());
            }

            logger.info("✅ Hospital created successfully: {} (ID: {})", 
                       savedHospital.getName(), savedHospital.getId());

            ApiResponse<Hospital> response = ApiResponse.success(
                    "Hospital created successfully", savedHospital);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to create hospital: {}", e.getMessage());
            
            ApiResponse<Hospital> response = ApiResponse.error(
                    "Failed to create hospital: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get all hospitals (Admin only)
     */
    @GetMapping("/hospitals")
    @Operation(summary = "Get all hospitals", 
               description = "Get paginated list of all hospitals")
    public ResponseEntity<ApiResponse<Page<Hospital>>> getAllHospitals(
            @Parameter(description = "Page number") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Search term") 
            @RequestParam(required = false) String search) {

        logger.info("🏥 ADMIN: Getting all hospitals (page: {}, size: {}, search: {})", 
                   page, size, search);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Hospital> hospitals;

            if (search != null && !search.trim().isEmpty()) {
                hospitals = hospitalRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(
                        search.trim(), search.trim(), pageable);
            } else {
                hospitals = hospitalRepository.findAll(pageable);
            }

            ApiResponse<Page<Hospital>> response = ApiResponse.success(
                    "Hospitals retrieved successfully", hospitals);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get hospitals: {}", e.getMessage());
            
            ApiResponse<Page<Hospital>> response = ApiResponse.error(
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
    public ResponseEntity<ApiResponse<Hospital>> getHospitalById(
            @Parameter(description = "Hospital ID") 
            @PathVariable Long id) {

        logger.info("🏥 ADMIN: Getting hospital by ID: {}", id);

        try {
            Optional<Hospital> hospitalOpt = hospitalRepository.findById(id);
            
            if (hospitalOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Hospital not found"));
            }

            ApiResponse<Hospital> response = ApiResponse.success(
                    "Hospital retrieved successfully", hospitalOpt.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get hospital: {}", e.getMessage());
            
            ApiResponse<Hospital> response = ApiResponse.error(
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
     * Get global policies (Admin only)
     */
    @GetMapping("/policies")
    @Operation(summary = "Get all global policies", 
               description = "Get all policies from Global Collaboration Portal")
    public ResponseEntity<ApiResponse<List<PolicyService.OrganPolicy>>> getAllPolicies(
            @Parameter(description = "Organ type filter") 
            @RequestParam(required = false) String organType) {

        logger.info("🏛️ ADMIN: Getting all policies (organType: {})", organType);

        try {
            List<PolicyService.OrganPolicy> policies;
            
            if (organType != null && !organType.trim().isEmpty()) {
                policies = policyService.getAllPolicies(organType.trim());
            } else {
                // Get all policies for all organ types
                policies = List.of(); // Implement getAllPoliciesForAllOrgans() if needed
            }

            ApiResponse<List<PolicyService.OrganPolicy>> response = ApiResponse.success(
                    "Policies retrieved successfully", policies);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get policies: {}", e.getMessage());
            
            ApiResponse<List<PolicyService.OrganPolicy>> response = ApiResponse.error(
                    "Failed to retrieve policies: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
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

    // Request/Response DTOs
    public static class CreateHospitalRequest {
        private String name;
        private String address;
        private String city;
        private String contactNumber;
        private String emailAddress;
        private String licenseNumber;
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
