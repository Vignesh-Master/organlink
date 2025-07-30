package com.organlink.controller;

import com.organlink.model.dto.DonorSummaryDto;
import com.organlink.model.entity.Donor;
import com.organlink.repository.DonorRepository;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for hospital-specific donor management
 * Each hospital can only see and manage their own donors
 */
@RestController
@RequestMapping("/donors")
@RequiredArgsConstructor
@Tag(name = "Donor Management", description = "Hospital-specific donor management APIs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class DonorController {

    private static final Logger logger = LoggerFactory.getLogger(DonorController.class);

    private final DonorRepository donorRepository;

    /**
     * Get donors for specific hospital (tenant-based) - Summary view
     */
    @GetMapping
    @Operation(summary = "Get hospital donors", description = "Get all donors for the authenticated hospital")
    public ResponseEntity<ApiResponse<Page<DonorSummaryDto>>> getHospitalDonors(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Page number") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Search term") 
            @RequestParam(required = false) String search) {
        
        logger.info("Fetching donors for hospital: {}", tenantId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Donor> donors;

        if (search != null && !search.trim().isEmpty()) {
            donors = donorRepository.searchDonorsByTenant(tenantId, search.trim(), pageable);
        } else {
            donors = donorRepository.findByTenantIdAndIsActiveTrue(tenantId, pageable);
        }

        // Convert to summary DTOs
        Page<DonorSummaryDto> donorSummaries = donors.map(this::convertToSummaryDto);

        ApiResponse<Page<DonorSummaryDto>> response = ApiResponse.success(
            "Donors retrieved successfully",
            donorSummaries
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get donor by ID (tenant-specific)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get donor by ID", description = "Get specific donor details for the hospital")
    public ResponseEntity<ApiResponse<Donor>> getDonorById(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Donor ID") 
            @PathVariable Long id) {
        
        logger.info("Fetching donor {} for hospital: {}", id, tenantId);
        
        Optional<Donor> donor = donorRepository.findByIdAndTenantId(id, tenantId);
        
        if (donor.isEmpty()) {
            ApiResponse<Donor> response = ApiResponse.error("Donor not found", null);
            return ResponseEntity.status(404).body(response);
        }
        
        ApiResponse<Donor> response = ApiResponse.success(
            "Donor retrieved successfully", 
            donor.get()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create new donor
     */
    @PostMapping
    @Operation(summary = "Create donor", description = "Register new donor for the hospital")
    public ResponseEntity<ApiResponse<Donor>> createDonor(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody Donor donor) {
        
        logger.info("Creating new donor for hospital: {}", tenantId);
        
        // Set tenant ID and registration date
        donor.setTenantId(tenantId);
        donor.setRegistrationDate(LocalDateTime.now());
        donor.setIsActive(true);
        donor.setStatus("AVAILABLE");
        
        Donor savedDonor = donorRepository.save(donor);
        
        ApiResponse<Donor> response = ApiResponse.success(
            "Donor created successfully", 
            savedDonor
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update donor
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update donor", description = "Update donor information")
    public ResponseEntity<ApiResponse<Donor>> updateDonor(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Donor ID") 
            @PathVariable Long id,
            @RequestBody Donor donorUpdate) {
        
        logger.info("Updating donor {} for hospital: {}", id, tenantId);
        
        Optional<Donor> existingDonor = donorRepository.findByIdAndTenantId(id, tenantId);
        
        if (existingDonor.isEmpty()) {
            ApiResponse<Donor> response = ApiResponse.error("Donor not found", null);
            return ResponseEntity.status(404).body(response);
        }
        
        Donor donor = existingDonor.get();
        
        // Update fields (preserve tenant ID and creation info)
        donor.setDonorName(donorUpdate.getDonorName());
        donor.setAge(donorUpdate.getAge());
        donor.setBloodType(donorUpdate.getBloodType());
        donor.setGender(donorUpdate.getGender());
        donor.setOrganAvailable(donorUpdate.getOrganAvailable());
        donor.setDonorType(donorUpdate.getDonorType());
        donor.setCauseOfDeath(donorUpdate.getCauseOfDeath());
        donor.setTimeOfDeath(donorUpdate.getTimeOfDeath());
        donor.setMedicalClearance(donorUpdate.getMedicalClearance());
        donor.setContactPerson(donorUpdate.getContactPerson());
        donor.setContactNumber(donorUpdate.getContactNumber());
        donor.setStatus(donorUpdate.getStatus());
        donor.setHlaTyping(donorUpdate.getHlaTyping());
        donor.setMedicalHistory(donorUpdate.getMedicalHistory());
        
        Donor savedDonor = donorRepository.save(donor);
        
        ApiResponse<Donor> response = ApiResponse.success(
            "Donor updated successfully", 
            savedDonor
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get donor statistics for hospital
     */
    @GetMapping("/stats")
    @Operation(summary = "Get donor statistics", description = "Get donor statistics for the hospital")
    public ResponseEntity<ApiResponse<Object>> getDonorStats(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {
        
        logger.info("Fetching donor statistics for hospital: {}", tenantId);
        
        long totalDonors = donorRepository.countByTenantIdAndIsActiveTrue(tenantId);
        long availableDonors = donorRepository.countByTenantIdAndStatusAndIsActiveTrue(tenantId, "AVAILABLE");
        
        var stats = new Object() {
            public final long total = totalDonors;
            public final long available = availableDonors;
            public final long matched = donorRepository.countByTenantIdAndStatusAndIsActiveTrue(tenantId, "MATCHED");
            public final long transplanted = donorRepository.countByTenantIdAndStatusAndIsActiveTrue(tenantId, "TRANSPLANTED");
        };
        
        ApiResponse<Object> response = ApiResponse.success(
            "Donor statistics retrieved successfully", 
            stats
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Convert Donor entity to DonorSummaryDto
     */
    private DonorSummaryDto convertToSummaryDto(Donor donor) {
        DonorSummaryDto dto = new DonorSummaryDto();
        dto.setId(donor.getId());
        dto.setDonorName(donor.getDonorName());
        dto.setAge(donor.getAge());
        dto.setBloodType(donor.getBloodType());
        dto.setGender(donor.getGender());
        dto.setOrganAvailable(donor.getOrganAvailable());
        dto.setDonorType(donor.getDonorType());
        dto.setStatus(donor.getStatus());
        dto.setContactPerson(donor.getContactPerson());
        dto.setContactNumber(donor.getContactNumber());
        dto.setRegistrationDate(donor.getRegistrationDate());

        // Set hospital name if available
        if (donor.getHospital() != null) {
            dto.setHospitalName(donor.getHospital().getName());
        }

        return dto;
    }
}
