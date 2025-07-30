package com.organlink.repository;

import com.organlink.model.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Patient entity
 * Handles database operations for patient management with multi-tenant support
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find patients by tenant ID (hospital-specific)
     */
    Page<Patient> findByTenantIdAndIsActiveTrue(String tenantId, Pageable pageable);

    /**
     * Find patients by hospital ID
     */
    Page<Patient> findByHospitalIdAndIsActiveTrue(Long hospitalId, Pageable pageable);

    /**
     * Find patient by ID and tenant ID (security check)
     */
    Optional<Patient> findByIdAndTenantId(Long id, String tenantId);

    /**
     * Find waiting patients by organ type and tenant ID
     */
    List<Patient> findByOrganNeededAndTenantIdAndStatusAndIsActiveTrue(
            String organNeeded, String tenantId, String status);

    /**
     * Find waiting patients by organ type (cross-hospital matching)
     */
    List<Patient> findByOrganNeededAndStatusAndIsActiveTrue(String organNeeded, String status);

    /**
     * Find compatible patients by blood type and organ type (for matching)
     */
    @Query("SELECT p FROM Patient p WHERE p.bloodType = :bloodType AND p.organNeeded = :organType " +
           "AND p.status = 'WAITING' AND p.isActive = true ORDER BY p.urgencyLevel DESC, p.waitingListDate ASC")
    List<Patient> findCompatiblePatients(@Param("bloodType") String bloodType, 
                                       @Param("organType") String organType);

    /**
     * Search patients by name within tenant
     */
    @Query("SELECT p FROM Patient p WHERE p.tenantId = :tenantId AND p.isActive = true " +
           "AND UPPER(p.patientName) LIKE UPPER(CONCAT('%', :searchTerm, '%'))")
    Page<Patient> searchPatientsByTenant(@Param("tenantId") String tenantId, 
                                       @Param("searchTerm") String searchTerm, 
                                       Pageable pageable);

    /**
     * Count active patients by tenant
     */
    long countByTenantIdAndIsActiveTrue(String tenantId);

    /**
     * Count waiting patients by tenant
     */
    long countByTenantIdAndStatusAndIsActiveTrue(String tenantId, String status);

    /**
     * Find patients by status and tenant
     */
    List<Patient> findByTenantIdAndStatusAndIsActiveTrue(String tenantId, String status);

    /**
     * Find patients by urgency level and tenant
     */
    List<Patient> findByTenantIdAndUrgencyLevelAndIsActiveTrueOrderByWaitingListDateAsc(
            String tenantId, String urgencyLevel);

    /**
     * Find critical patients across all hospitals (for emergency matching)
     */
    List<Patient> findByUrgencyLevelAndStatusAndIsActiveTrueOrderByWaitingListDateAsc(
            String urgencyLevel, String status);
}
