package com.organlink.repository;

import com.organlink.model.entity.Donor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Donor entity
 * Handles database operations for donor management
 */
@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    /**
     * Find donors by hospital ID
     */
    Page<Donor> findByHospitalIdAndIsActiveTrue(Long hospitalId, Pageable pageable);

    /**
     * Find donor by ID and hospital ID (security check)
     */
    Optional<Donor> findByIdAndHospitalId(Long id, Long hospitalId);

    /**
     * Find active donors
     */
    List<Donor> findByIsActiveTrue();

    /**
     * Find donors by blood group (for matching)
     */
    @Query("SELECT d FROM Donor d WHERE d.bloodGroup = :bloodGroup AND d.isActive = true")
    List<Donor> findCompatibleDonors(@Param("bloodGroup") String bloodGroup);

    /**
     * Search donors by name
     */
    @Query("SELECT d FROM Donor d WHERE d.isActive = true " +
           "AND (UPPER(d.firstName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
           "OR UPPER(d.lastName) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    Page<Donor> searchDonors(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count active donors
     */
    long countByIsActiveTrue();

    /**
     * Count active donors by hospital
     */
    long countByHospitalIdAndIsActiveTrue(Long hospitalId);

    /**
     * Find donors by hospital
     */
    List<Donor> findByHospitalIdAndIsActiveTrue(Long hospitalId);

    /**
     * Find donors by blood group and hospital
     */
    List<Donor> findByBloodGroupAndHospitalIdAndIsActiveTrue(String bloodGroup, Long hospitalId);

    /**
     * Find donors by state
     */
    List<Donor> findByStateIdAndIsActiveTrue(Long stateId);
}
