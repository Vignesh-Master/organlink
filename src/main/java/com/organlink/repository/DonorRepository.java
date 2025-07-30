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
 * Handles database operations for donor management with multi-tenant support
 */
@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    /**
     * Find donors by tenant ID (hospital-specific)
     */
    Page<Donor> findByTenantIdAndIsActiveTrue(String tenantId, Pageable pageable);

    /**
     * Find donors by hospital ID
     */
    Page<Donor> findByHospitalIdAndIsActiveTrue(Long hospitalId, Pageable pageable);

    /**
     * Find donor by ID and tenant ID (security check)
     */
    Optional<Donor> findByIdAndTenantId(Long id, String tenantId);

    /**
     * Find available donors by organ type and tenant ID
     */
    List<Donor> findByOrganAvailableAndTenantIdAndStatusAndIsActiveTrue(
            String organAvailable, String tenantId, String status);

    /**
     * Find available donors by organ type (cross-hospital matching)
     */
    List<Donor> findByOrganAvailableAndStatusAndIsActiveTrue(String organAvailable, String status);

    /**
     * Find donors by blood type and organ type (for matching)
     */
    @Query("SELECT d FROM Donor d WHERE d.bloodType = :bloodType AND d.organAvailable = :organType " +
           "AND d.status = 'AVAILABLE' AND d.isActive = true")
    List<Donor> findCompatibleDonors(@Param("bloodType") String bloodType, 
                                   @Param("organType") String organType);

    /**
     * Search donors by name within tenant
     */
    @Query("SELECT d FROM Donor d WHERE d.tenantId = :tenantId AND d.isActive = true " +
           "AND (UPPER(d.donorName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
           "OR UPPER(d.contactPerson) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    Page<Donor> searchDonorsByTenant(@Param("tenantId") String tenantId, 
                                   @Param("searchTerm") String searchTerm, 
                                   Pageable pageable);

    /**
     * Count active donors by tenant
     */
    long countByTenantIdAndIsActiveTrue(String tenantId);

    /**
     * Count available donors by tenant
     */
    long countByTenantIdAndStatusAndIsActiveTrue(String tenantId, String status);

    /**
     * Find donors by status and tenant
     */
    List<Donor> findByTenantIdAndStatusAndIsActiveTrue(String tenantId, String status);
}
