package com.organlink.repository;

import com.organlink.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Country entity
 * Provides database operations for country management
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * Find country by code (case-insensitive)
     */
    Optional<Country> findByCodeIgnoreCase(String code);

    /**
     * Find country by name (case-insensitive)
     */
    Optional<Country> findByNameIgnoreCase(String name);

    /**
     * Check if country exists by code
     */
    boolean existsByCodeIgnoreCase(String code);

    /**
     * Check if country exists by name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find all countries ordered by name
     */
    List<Country> findAllByOrderByNameAsc();

    /**
     * Find countries with states (using JOIN FETCH to avoid N+1 problem)
     */
    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.states ORDER BY c.name")
    List<Country> findAllWithStates();

    /**
     * Find country with states by ID
     */
    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.states WHERE c.id = :id")
    Optional<Country> findByIdWithStates(@Param("id") Long id);

    /**
     * Find country with states by code
     */
    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.states WHERE UPPER(c.code) = UPPER(:code)")
    Optional<Country> findByCodeWithStates(@Param("code") String code);

    /**
     * Search countries by name containing the search term (case-insensitive)
     */
    @Query("SELECT c FROM Country c WHERE UPPER(c.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')) ORDER BY c.name")
    List<Country> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Count total number of countries
     */
    @Query("SELECT COUNT(c) FROM Country c")
    long countAll();
}
