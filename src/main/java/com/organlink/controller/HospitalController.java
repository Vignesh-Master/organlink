package com.organlink.controller;

import com.organlink.model.dto.HospitalDto;
import com.organlink.service.HospitalService;
import com.organlink.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Hospital management operations
 * Provides endpoints for CRUD operations on hospitals
 */
@RestController
@RequestMapping("/hospitals")
@Tag(name = "Hospital Management", description = "APIs for managing hospitals in the organ donation system")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class HospitalController {

    private static final Logger logger = LoggerFactory.getLogger(HospitalController.class);

    private final HospitalService hospitalService;

    @Autowired
    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    /**
     * Create a new hospital
     */
    @PostMapping
    @Operation(summary = "Create a new hospital", description = "Creates a new hospital in the system")
    public ResponseEntity<ApiResponse<HospitalDto>> createHospital(
            @Valid @RequestBody HospitalDto hospitalDto) {
        
        logger.info("Creating new hospital: {}", hospitalDto.getName());
        
        HospitalDto createdHospital = hospitalService.createHospital(hospitalDto);
        ApiResponse<HospitalDto> response = ApiResponse.success(
            "Hospital created successfully", 
            createdHospital
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all hospitals with pagination and filtering
     */
    @GetMapping
    @Operation(summary = "Get all hospitals", description = "Retrieves all hospitals with pagination and filtering options")
    public ResponseEntity<ApiResponse<Page<HospitalDto>>> getAllHospitals(
            @Parameter(description = "Search term for hospital name, code, or email")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by state ID")
            @RequestParam(required = false) Long stateId,
            
            @Parameter(description = "Filter by active status")
            @RequestParam(required = false) Boolean isActive,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "name") String sort,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction) {
        
        logger.debug("Fetching hospitals - page: {}, size: {}, search: {}", page, size, search);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<HospitalDto> hospitals = hospitalService.getAllHospitals(search, stateId, isActive, pageable);
        ApiResponse<Page<HospitalDto>> response = ApiResponse.success(
            "Hospitals retrieved successfully", 
            hospitals
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get hospital by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get hospital by ID", description = "Retrieves a specific hospital by its ID")
    public ResponseEntity<ApiResponse<HospitalDto>> getHospitalById(
            @Parameter(description = "Hospital ID") @PathVariable Long id) {
        
        logger.debug("Fetching hospital by ID: {}", id);
        
        HospitalDto hospital = hospitalService.getHospitalById(id);
        ApiResponse<HospitalDto> response = ApiResponse.success(
            "Hospital retrieved successfully", 
            hospital
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get hospital by code
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get hospital by code", description = "Retrieves a specific hospital by its code")
    public ResponseEntity<ApiResponse<HospitalDto>> getHospitalByCode(
            @Parameter(description = "Hospital code") @PathVariable String code) {
        
        logger.debug("Fetching hospital by code: {}", code);
        
        HospitalDto hospital = hospitalService.getHospitalByCode(code);
        ApiResponse<HospitalDto> response = ApiResponse.success(
            "Hospital retrieved successfully", 
            hospital
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get hospitals by state ID
     */
    @GetMapping("/state/{stateId}")
    @Operation(summary = "Get hospitals by state", description = "Retrieves all hospitals in a specific state")
    public ResponseEntity<ApiResponse<List<HospitalDto>>> getHospitalsByState(
            @Parameter(description = "State ID") @PathVariable Long stateId,
            @Parameter(description = "Include only active hospitals") 
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        
        logger.debug("Fetching hospitals by state ID: {}, activeOnly: {}", stateId, activeOnly);
        
        List<HospitalDto> hospitals = activeOnly ? 
            hospitalService.getActiveHospitalsByStateId(stateId) :
            hospitalService.getHospitalsByStateId(stateId);
            
        ApiResponse<List<HospitalDto>> response = ApiResponse.success(
            "Hospitals retrieved successfully", 
            hospitals
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active hospitals
     */
    @GetMapping("/active")
    @Operation(summary = "Get all active hospitals", description = "Retrieves all active hospitals in the system")
    public ResponseEntity<ApiResponse<List<HospitalDto>>> getAllActiveHospitals() {
        
        logger.debug("Fetching all active hospitals");
        
        List<HospitalDto> hospitals = hospitalService.getAllActiveHospitals();
        ApiResponse<List<HospitalDto>> response = ApiResponse.success(
            "Active hospitals retrieved successfully", 
            hospitals
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update hospital
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update hospital", description = "Updates an existing hospital")
    public ResponseEntity<ApiResponse<HospitalDto>> updateHospital(
            @Parameter(description = "Hospital ID") @PathVariable Long id,
            @Valid @RequestBody HospitalDto hospitalDto) {
        
        logger.info("Updating hospital with ID: {}", id);
        
        HospitalDto updatedHospital = hospitalService.updateHospital(id, hospitalDto);
        ApiResponse<HospitalDto> response = ApiResponse.success(
            "Hospital updated successfully", 
            updatedHospital
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete hospital (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hospital", description = "Soft deletes a hospital by setting it as inactive")
    public ResponseEntity<ApiResponse<Void>> deleteHospital(
            @Parameter(description = "Hospital ID") @PathVariable Long id) {
        
        logger.info("Deleting hospital with ID: {}", id);
        
        hospitalService.deleteHospital(id);
        ApiResponse<Void> response = ApiResponse.success("Hospital deleted successfully", null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get hospital statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get hospital statistics", description = "Retrieves statistics about hospitals")
    public ResponseEntity<ApiResponse<Object>> getHospitalStats() {
        
        logger.debug("Fetching hospital statistics");
        
        long activeCount = hospitalService.getActiveHospitalCount();
        
        Object stats = new Object() {
            public final long activeHospitals = activeCount;
        };
        
        ApiResponse<Object> response = ApiResponse.success(
            "Hospital statistics retrieved successfully", 
            stats
        );
        
        return ResponseEntity.ok(response);
    }
}
