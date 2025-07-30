package com.organlink.repository;

import com.organlink.model.entity.OrganType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrganType entity
 * Provides database operations for organ type management
 */
@Repository
public interface OrganTypeRepository extends JpaRepository<OrganType, Long> {

    /**
     * Find organ type by name (case-insensitive)
     */
    Optional<OrganType> findByNameIgnoreCase(String name);

    /**
     * Check if organ type exists by name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find all active organ types
     */
    List<OrganType> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all organ types ordered by name
     */
    List<OrganType> findAllByOrderByNameAsc();

    /**
     * Find organ types by active status
     */
    List<OrganType> findByIsActiveOrderByNameAsc(Boolean isActive);

    /**
     * Find organ types with preservation time less than or equal to specified hours
     */
    List<OrganType> findByPreservationTimeHoursLessThanEqualAndIsActiveTrueOrderByPreservationTimeHoursAsc(Integer hours);

    /**
     * Find organ types with preservation time greater than specified hours
     */
    List<OrganType> findByPreservationTimeHoursGreaterThanAndIsActiveTrueOrderByPreservationTimeHoursAsc(Integer hours);

    /**
     * Search organ types by name or description (case-insensitive)
     */
    @Query("SELECT ot FROM OrganType ot " +
           "WHERE (:searchTerm IS NULL OR " +
           "UPPER(ot.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(ot.description) LIKE UPPER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:isActive IS NULL OR ot.isActive = :isActive) " +
           "ORDER BY ot.name")
    Page<OrganType> searchOrganTypes(@Param("searchTerm") String searchTerm,
                                     @Param("isActive") Boolean isActive,
                                     Pageable pageable);

    /**
     * Find organ types by preservation time range
     */
    @Query("SELECT ot FROM OrganType ot " +
           "WHERE ot.preservationTimeHours BETWEEN :minHours AND :maxHours " +
           "AND ot.isActive = true " +
           "ORDER BY ot.preservationTimeHours")
    List<OrganType> findByPreservationTimeRange(@Param("minHours") Integer minHours,
                                                 @Param("maxHours") Integer maxHours);

    /**
     * Count active organ types
     */
    long countByIsActiveTrue();

    /**
     * Count total organ types
     */
    @Override
    long count();

    /**
     * Find organ types suitable for urgent transplantation (short preservation time)
     */
    @Query("SELECT ot FROM OrganType ot " +
           "WHERE ot.preservationTimeHours <= 12 " +
           "AND ot.isActive = true " +
           "ORDER BY ot.preservationTimeHours")
    List<OrganType> findUrgentOrganTypes();

    /**
     * Find organ types suitable for planned transplantation (longer preservation time)
     */
    @Query("SELECT ot FROM OrganType ot " +
           "WHERE ot.preservationTimeHours > 12 " +
           "AND ot.isActive = true " +
           "ORDER BY ot.preservationTimeHours DESC")
    List<OrganType> findPlannedOrganTypes();
}
