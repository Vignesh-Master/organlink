package com.organlink.repository;

import com.organlink.model.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Hospital entity
 * Provides database operations for hospital management
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    /**
     * Find hospital by code (case-insensitive)
     */
    Optional<Hospital> findByCodeIgnoreCase(String code);

    /**
     * Find hospital by tenant ID
     */
    Optional<Hospital> findByTenantId(String tenantId);

    /**
     * Check if hospital exists by code
     */
    boolean existsByCodeIgnoreCase(String code);

    /**
     * Check if hospital exists by tenant ID
     */
    boolean existsByTenantId(String tenantId);

    /**
     * Find hospitals by state ID
     */
    List<Hospital> findByStateIdOrderByNameAsc(Long stateId);

    /**
     * Find active hospitals by state ID
     */
    List<Hospital> findByStateIdAndIsActiveTrueOrderByNameAsc(Long stateId);

    /**
     * Find all active hospitals
     */
    List<Hospital> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find hospital with state and country information by ID
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country " +
           "WHERE h.id = :id")
    Optional<Hospital> findByIdWithStateAndCountry(@Param("id") Long id);

    /**
     * Find hospital with state and country information by code
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country " +
           "WHERE UPPER(h.code) = UPPER(:code)")
    Optional<Hospital> findByCodeWithStateAndCountry(@Param("code") String code);

    /**
     * Find hospital with state and country information by tenant ID
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country " +
           "WHERE h.tenantId = :tenantId")
    Optional<Hospital> findByTenantIdWithStateAndCountry(@Param("tenantId") String tenantId);

    /**
     * Search hospitals by name, code, or email (case-insensitive) with pagination
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country " +
           "WHERE (:searchTerm IS NULL OR " +
           "UPPER(h.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(h.code) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(h.email) LIKE UPPER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:stateId IS NULL OR h.state.id = :stateId) " +
           "AND (:isActive IS NULL OR h.isActive = :isActive)")
    Page<Hospital> searchHospitals(@Param("searchTerm") String searchTerm,
                                   @Param("stateId") Long stateId,
                                   @Param("isActive") Boolean isActive,
                                   Pageable pageable);

    /**
     * Find hospitals by country ID
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country c " +
           "WHERE c.id = :countryId " +
           "ORDER BY h.name")
    List<Hospital> findByCountryId(@Param("countryId") Long countryId);

    /**
     * Find active hospitals by country ID
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country c " +
           "WHERE c.id = :countryId AND h.isActive = true " +
           "ORDER BY h.name")
    List<Hospital> findActiveByCountryId(@Param("countryId") Long countryId);

    /**
     * Count hospitals by state ID
     */
    long countByStateId(Long stateId);

    /**
     * Count active hospitals by state ID
     */
    long countByStateIdAndIsActiveTrue(Long stateId);

    /**
     * Count total active hospitals
     */
    long countByIsActiveTrue();

    /**
     * Find hospitals by state code
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country " +
           "WHERE UPPER(s.code) = UPPER(:stateCode) " +
           "ORDER BY h.name")
    List<Hospital> findByStateCode(@Param("stateCode") String stateCode);

    /**
     * Find hospitals by country code
     */
    @Query("SELECT h FROM Hospital h " +
           "JOIN FETCH h.state s " +
           "JOIN FETCH s.country c " +
           "WHERE UPPER(c.code) = UPPER(:countryCode) " +
           "ORDER BY s.name, h.name")
    List<Hospital> findByCountryCode(@Param("countryCode") String countryCode);
}
