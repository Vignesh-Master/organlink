package com.organlink.repository;

import com.organlink.model.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for State entity
 * Provides database operations for state management
 */
@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    /**
     * Find states by country ID
     */
    List<State> findByCountryIdOrderByNameAsc(Long countryId);

    /**
     * Find state by name and country ID (case-insensitive)
     */
    Optional<State> findByNameIgnoreCaseAndCountryId(String name, Long countryId);

    /**
     * Find state by code and country ID (case-insensitive)
     */
    Optional<State> findByCodeIgnoreCaseAndCountryId(String code, Long countryId);

    /**
     * Check if state exists by name and country ID
     */
    boolean existsByNameIgnoreCaseAndCountryId(String name, Long countryId);

    /**
     * Check if state exists by code and country ID
     */
    boolean existsByCodeIgnoreCaseAndCountryId(String code, Long countryId);

    /**
     * Find all states with country information
     */
    @Query("SELECT s FROM State s JOIN FETCH s.country ORDER BY s.country.name, s.name")
    List<State> findAllWithCountry();

    /**
     * Find state with country and hospitals by ID
     */
    @Query("SELECT s FROM State s " +
           "LEFT JOIN FETCH s.country " +
           "LEFT JOIN FETCH s.hospitals h " +
           "WHERE s.id = :id AND (h.isActive = true OR h.isActive IS NULL)")
    Optional<State> findByIdWithCountryAndActiveHospitals(@Param("id") Long id);

    /**
     * Find states with active hospitals by country ID
     */
    @Query("SELECT DISTINCT s FROM State s " +
           "LEFT JOIN FETCH s.hospitals h " +
           "WHERE s.country.id = :countryId AND (h.isActive = true OR h.isActive IS NULL) " +
           "ORDER BY s.name")
    List<State> findByCountryIdWithActiveHospitals(@Param("countryId") Long countryId);

    /**
     * Search states by name containing the search term (case-insensitive)
     */
    @Query("SELECT s FROM State s JOIN FETCH s.country " +
           "WHERE UPPER(s.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY s.country.name, s.name")
    List<State> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find states by country code
     */
    @Query("SELECT s FROM State s JOIN FETCH s.country c " +
           "WHERE UPPER(c.code) = UPPER(:countryCode) " +
           "ORDER BY s.name")
    List<State> findByCountryCode(@Param("countryCode") String countryCode);

    /**
     * Count states by country ID
     */
    long countByCountryId(Long countryId);

    /**
     * Count total number of states
     */
    @Query("SELECT COUNT(s) FROM State s")
    long countAll();
}
