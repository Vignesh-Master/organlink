package com.organlink.repository;

import com.organlink.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Organization entity
 * Provides data access methods for organization management
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    /**
     * Find organization by organization ID
     */
    Optional<Organization> findByOrgIdIgnoreCase(String orgId);

    /**
     * Find organization by username
     */
    Optional<Organization> findByUsernameIgnoreCase(String username);

    /**
     * Find organization by name
     */
    Optional<Organization> findByNameIgnoreCase(String name);

    /**
     * Check if organization exists by organization ID
     */
    boolean existsByOrgIdIgnoreCase(String orgId);

    /**
     * Check if organization exists by username
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Check if organization exists by name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find all active organizations
     */
    List<Organization> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all organizations by location
     */
    List<Organization> findByLocationIgnoreCaseOrderByNameAsc(String location);

    /**
     * Find organizations that can vote
     */
    List<Organization> findByCanVoteTrueAndIsActiveTrueOrderByNameAsc();

    /**
     * Find organizations that can propose
     */
    List<Organization> findByCanProposeTrueAndIsActiveTrueOrderByNameAsc();

    /**
     * Find blockchain-enabled organizations
     */
    List<Organization> findByRegisterBlockchainTrueAndIsActiveTrueOrderByNameAsc();

    /**
     * Find organizations by voting and proposal capabilities
     */
    @Query("SELECT o FROM Organization o WHERE o.canVote = :canVote AND o.canPropose = :canPropose AND o.isActive = true ORDER BY o.name ASC")
    List<Organization> findByCapabilities(@Param("canVote") Boolean canVote, @Param("canPropose") Boolean canPropose);

    /**
     * Count active organizations
     */
    long countByIsActiveTrue();

    /**
     * Count organizations by location
     */
    long countByLocationIgnoreCaseAndIsActiveTrue(String location);

    /**
     * Count blockchain-enabled organizations
     */
    long countByRegisterBlockchainTrueAndIsActiveTrue();

    /**
     * Find organizations with search functionality
     */
    @Query("SELECT o FROM Organization o WHERE " +
           "(LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.orgId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND o.isActive = true ORDER BY o.name ASC")
    List<Organization> searchActiveOrganizations(@Param("searchTerm") String searchTerm);

    /**
     * Find all organizations ordered by name
     */
    List<Organization> findAllByOrderByNameAsc();

    /**
     * Find organizations by multiple criteria
     */
    @Query("SELECT o FROM Organization o WHERE " +
           "(:location IS NULL OR LOWER(o.location) = LOWER(:location)) AND " +
           "(:canVote IS NULL OR o.canVote = :canVote) AND " +
           "(:canPropose IS NULL OR o.canPropose = :canPropose) AND " +
           "(:isActive IS NULL OR o.isActive = :isActive) " +
           "ORDER BY o.name ASC")
    List<Organization> findByCriteria(
        @Param("location") String location,
        @Param("canVote") Boolean canVote,
        @Param("canPropose") Boolean canPropose,
        @Param("isActive") Boolean isActive
    );
}
