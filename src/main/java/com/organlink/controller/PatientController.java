package com.organlink.controller;

import com.organlink.model.dto.PatientSummaryDto;
import com.organlink.model.entity.Patient;
import com.organlink.repository.PatientRepository;
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
 * REST Controller for hospital-specific patient management
 * Each hospital can only see and manage their own patients
 */
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Hospital-specific patient management APIs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientRepository patientRepository;

    /**
     * Get patients for specific hospital (tenant-based)
     */
    @GetMapping
    @Operation(summary = "Get hospital patients", description = "Get all patients for the authenticated hospital")
    public ResponseEntity<ApiResponse<Page<Patient>>> getHospitalPatients(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Page number") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Search term") 
            @RequestParam(required = false) String search) {
        
        logger.info("Fetching patients for hospital: {}", tenantId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patients;
        
        if (search != null && !search.trim().isEmpty()) {
            patients = patientRepository.searchPatientsByTenant(tenantId, search.trim(), pageable);
        } else {
            patients = patientRepository.findByTenantIdAndIsActiveTrue(tenantId, pageable);
        }
        
        ApiResponse<Page<Patient>> response = ApiResponse.success(
            "Patients retrieved successfully", 
            patients
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get patient by ID (tenant-specific)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Get specific patient details for the hospital")
    public ResponseEntity<ApiResponse<Patient>> getPatientById(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Patient ID") 
            @PathVariable Long id) {
        
        logger.info("Fetching patient {} for hospital: {}", id, tenantId);
        
        Optional<Patient> patient = patientRepository.findByIdAndTenantId(id, tenantId);
        
        if (patient.isEmpty()) {
            ApiResponse<Patient> response = ApiResponse.error("Patient not found", null);
            return ResponseEntity.status(404).body(response);
        }
        
        ApiResponse<Patient> response = ApiResponse.success(
            "Patient retrieved successfully", 
            patient.get()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create new patient
     */
    @PostMapping
    @Operation(summary = "Create patient", description = "Register new patient for the hospital")
    public ResponseEntity<ApiResponse<Patient>> createPatient(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody Patient patient) {
        
        logger.info("Creating new patient for hospital: {}", tenantId);
        
        // Set tenant ID and registration date
        patient.setTenantId(tenantId);
        patient.setRegistrationDate(LocalDateTime.now());
        patient.setWaitingListDate(LocalDateTime.now());
        patient.setIsActive(true);
        patient.setStatus("WAITING");
        
        Patient savedPatient = patientRepository.save(patient);
        
        ApiResponse<Patient> response = ApiResponse.success(
            "Patient created successfully", 
            savedPatient
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update patient
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Update patient information")
    public ResponseEntity<ApiResponse<Patient>> updatePatient(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Patient ID") 
            @PathVariable Long id,
            @RequestBody Patient patientUpdate) {
        
        logger.info("Updating patient {} for hospital: {}", id, tenantId);
        
        Optional<Patient> existingPatient = patientRepository.findByIdAndTenantId(id, tenantId);
        
        if (existingPatient.isEmpty()) {
            ApiResponse<Patient> response = ApiResponse.error("Patient not found", null);
            return ResponseEntity.status(404).body(response);
        }
        
        Patient patient = existingPatient.get();
        
        // Update fields (preserve tenant ID and creation info)
        patient.setPatientName(patientUpdate.getPatientName());
        patient.setAge(patientUpdate.getAge());
        patient.setBloodType(patientUpdate.getBloodType());
        patient.setGender(patientUpdate.getGender());
        patient.setOrganNeeded(patientUpdate.getOrganNeeded());
        patient.setUrgencyLevel(patientUpdate.getUrgencyLevel());
        patient.setMedicalCondition(patientUpdate.getMedicalCondition());
        patient.setMedicalHistory(patientUpdate.getMedicalHistory());
        patient.setContactNumber(patientUpdate.getContactNumber());
        patient.setAddress(patientUpdate.getAddress());
        patient.setStatus(patientUpdate.getStatus());
        patient.setHlaTyping(patientUpdate.getHlaTyping());
        patient.setCrossMatchResults(patientUpdate.getCrossMatchResults());
        patient.setPriorityScore(patientUpdate.getPriorityScore());
        
        Patient savedPatient = patientRepository.save(patient);
        
        ApiResponse<Patient> response = ApiResponse.success(
            "Patient updated successfully", 
            savedPatient
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get patient statistics for hospital
     */
    @GetMapping("/stats")
    @Operation(summary = "Get patient statistics", description = "Get patient statistics for the hospital")
    public ResponseEntity<ApiResponse<Object>> getPatientStats(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {
        
        logger.info("Fetching patient statistics for hospital: {}", tenantId);
        
        long totalPatients = patientRepository.countByTenantIdAndIsActiveTrue(tenantId);
        long waitingPatients = patientRepository.countByTenantIdAndStatusAndIsActiveTrue(tenantId, "WAITING");
        
        var stats = new Object() {
            public final long total = totalPatients;
            public final long waiting = waitingPatients;
            public final long matched = patientRepository.countByTenantIdAndStatusAndIsActiveTrue(tenantId, "MATCHED");
            public final long transplanted = patientRepository.countByTenantIdAndStatusAndIsActiveTrue(tenantId, "TRANSPLANTED");
        };
        
        ApiResponse<Object> response = ApiResponse.success(
            "Patient statistics retrieved successfully", 
            stats
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get critical patients for hospital
     */
    @GetMapping("/critical")
    @Operation(summary = "Get critical patients", description = "Get critical patients for the hospital")
    public ResponseEntity<ApiResponse<List<Patient>>> getCriticalPatients(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {
        
        logger.info("Fetching critical patients for hospital: {}", tenantId);
        
        List<Patient> criticalPatients = patientRepository
                .findByTenantIdAndUrgencyLevelAndIsActiveTrueOrderByWaitingListDateAsc(tenantId, "CRITICAL");
        
        ApiResponse<List<Patient>> response = ApiResponse.success(
            "Critical patients retrieved successfully", 
            criticalPatients
        );
        
        return ResponseEntity.ok(response);
    }
}
