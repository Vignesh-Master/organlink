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
 * Handles database operations for patient management
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find patients by hospital ID
     */
    Page<Patient> findByHospitalIdAndIsActiveTrue(Long hospitalId, Pageable pageable);

    /**
     * Find patient by ID and hospital ID (security check)
     */
    Optional<Patient> findByIdAndHospitalId(Long id, Long hospitalId);

    /**
     * Find active patients
     */
    List<Patient> findByIsActiveTrue();

    /**
     * Find patients by organ type
     */
    List<Patient> findByOrganTypeIdAndIsActiveTrue(Long organTypeId);

    /**
     * Find compatible patients by blood group and organ type (for matching)
     */
    @Query("SELECT p FROM Patient p WHERE p.bloodGroup = :bloodGroup AND p.organType.id = :organTypeId " +
           "AND p.isActive = true ORDER BY p.urgencyLevel DESC, p.registrationDate ASC")
    List<Patient> findCompatiblePatients(@Param("bloodGroup") String bloodGroup,
                                       @Param("organTypeId") Long organTypeId);

    /**
     * Search patients by name
     */
    @Query("SELECT p FROM Patient p WHERE p.isActive = true " +
           "AND (UPPER(p.firstName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
           "OR UPPER(p.lastName) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    Page<Patient> searchPatients(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count active patients
     */
    long countByIsActiveTrue();

    /**
     * Count active patients by hospital
     */
    long countByHospitalIdAndIsActiveTrue(Long hospitalId);

    /**
     * Find patients by hospital
     */
    List<Patient> findByHospitalIdAndIsActiveTrue(Long hospitalId);

    /**
     * Find patients by urgency level (for emergency matching)
     */
    List<Patient> findByUrgencyLevelAndIsActiveTrueOrderByRegistrationDateAsc(String urgencyLevel);

    /**
     * Count patients by urgency level
     */
    long countByUrgencyLevelAndIsActiveTrue(String urgencyLevel);

    /**
     * Find patients by name search
     */
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    /**
     * Find patients by name or ID search with pagination
     */
    Page<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrIdEquals(
        String firstName, String lastName, Long id, Pageable pageable);

    /**
     * Find active patients ordered by registration date
     */
    Page<Patient> findByIsActiveTrueOrderByRegistrationDateDesc(Pageable pageable);

    /**
     * Find patients by blood group and hospital
     */
    List<Patient> findByBloodGroupAndHospitalIdAndIsActiveTrue(String bloodGroup, Long hospitalId);

    /**
     * Find patients by state
     */
    List<Patient> findByStateIdAndIsActiveTrue(Long stateId);
}
