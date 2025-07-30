package com.organlink.controller;

import com.organlink.model.dto.CountryDto;
import com.organlink.service.CountryService;
import com.organlink.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Location management operations (Countries and States)
 * Provides endpoints for managing the location hierarchy
 */
@RestController
@RequestMapping("/locations")
@Tag(name = "Location Management", description = "APIs for managing countries and states in the location hierarchy")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final CountryService countryService;

    @Autowired
    public LocationController(CountryService countryService) {
        this.countryService = countryService;
    }

    // ========== COUNTRY ENDPOINTS ==========

    /**
     * Create a new country
     */
    @PostMapping("/countries")
    @Operation(summary = "Create a new country", description = "Creates a new country in the system")
    public ResponseEntity<ApiResponse<CountryDto>> createCountry(
            @Valid @RequestBody CountryDto countryDto) {
        
        logger.info("Creating new country: {}", countryDto.getName());
        
        CountryDto createdCountry = countryService.createCountry(countryDto);
        ApiResponse<CountryDto> response = ApiResponse.success(
            "Country created successfully", 
            createdCountry
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all countries
     */
    @GetMapping("/countries")
    @Operation(summary = "Get all countries", description = "Retrieves all countries in the system")
    public ResponseEntity<ApiResponse<List<CountryDto>>> getAllCountries(
            @Parameter(description = "Include states in response")
            @RequestParam(defaultValue = "false") boolean includeStates) {
        
        logger.debug("Fetching all countries, includeStates: {}", includeStates);
        
        List<CountryDto> countries = includeStates ? 
            countryService.getAllCountriesWithStates() : 
            countryService.getAllCountries();
            
        ApiResponse<List<CountryDto>> response = ApiResponse.success(
            "Countries retrieved successfully", 
            countries
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get country by ID
     */
    @GetMapping("/countries/{id}")
    @Operation(summary = "Get country by ID", description = "Retrieves a specific country by its ID")
    public ResponseEntity<ApiResponse<CountryDto>> getCountryById(
            @Parameter(description = "Country ID") @PathVariable Long id,
            @Parameter(description = "Include states in response")
            @RequestParam(defaultValue = "false") boolean includeStates) {
        
        logger.debug("Fetching country by ID: {}, includeStates: {}", id, includeStates);
        
        CountryDto country = includeStates ? 
            countryService.getCountryByIdWithStates(id) : 
            countryService.getCountryById(id);
            
        ApiResponse<CountryDto> response = ApiResponse.success(
            "Country retrieved successfully", 
            country
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get country by code
     */
    @GetMapping("/countries/code/{code}")
    @Operation(summary = "Get country by code", description = "Retrieves a specific country by its code")
    public ResponseEntity<ApiResponse<CountryDto>> getCountryByCode(
            @Parameter(description = "Country code") @PathVariable String code) {
        
        logger.debug("Fetching country by code: {}", code);
        
        CountryDto country = countryService.getCountryByCode(code);
        ApiResponse<CountryDto> response = ApiResponse.success(
            "Country retrieved successfully", 
            country
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update country
     */
    @PutMapping("/countries/{id}")
    @Operation(summary = "Update country", description = "Updates an existing country")
    public ResponseEntity<ApiResponse<CountryDto>> updateCountry(
            @Parameter(description = "Country ID") @PathVariable Long id,
            @Valid @RequestBody CountryDto countryDto) {
        
        logger.info("Updating country with ID: {}", id);
        
        CountryDto updatedCountry = countryService.updateCountry(id, countryDto);
        ApiResponse<CountryDto> response = ApiResponse.success(
            "Country updated successfully", 
            updatedCountry
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete country
     */
    @DeleteMapping("/countries/{id}")
    @Operation(summary = "Delete country", description = "Deletes a country from the system")
    public ResponseEntity<ApiResponse<Void>> deleteCountry(
            @Parameter(description = "Country ID") @PathVariable Long id) {
        
        logger.info("Deleting country with ID: {}", id);
        
        countryService.deleteCountry(id);
        ApiResponse<Void> response = ApiResponse.success("Country deleted successfully", null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search countries by name
     */
    @GetMapping("/countries/search")
    @Operation(summary = "Search countries", description = "Searches countries by name")
    public ResponseEntity<ApiResponse<List<CountryDto>>> searchCountries(
            @Parameter(description = "Search term") @RequestParam String q) {
        
        logger.debug("Searching countries with term: {}", q);
        
        List<CountryDto> countries = countryService.searchCountriesByName(q);
        ApiResponse<List<CountryDto>> response = ApiResponse.success(
            "Countries search completed", 
            countries
        );
        
        return ResponseEntity.ok(response);
    }

    // ========== LOCATION HIERARCHY ENDPOINTS ==========

    /**
     * Get complete location hierarchy
     */
    @GetMapping("/hierarchy")
    @Operation(summary = "Get location hierarchy", description = "Retrieves the complete location hierarchy (countries -> states -> hospitals)")
    public ResponseEntity<ApiResponse<List<CountryDto>>> getLocationHierarchy() {
        
        logger.debug("Fetching complete location hierarchy");
        
        List<CountryDto> hierarchy = countryService.getAllCountriesWithStates();
        ApiResponse<List<CountryDto>> response = ApiResponse.success(
            "Location hierarchy retrieved successfully", 
            hierarchy
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get location statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get location statistics", description = "Retrieves statistics about locations")
    public ResponseEntity<ApiResponse<Object>> getLocationStats() {
        
        logger.debug("Fetching location statistics");
        
        long countryCount = countryService.getTotalCount();
        
        Object stats = new Object() {
            public final long totalCountries = countryCount;
        };
        
        ApiResponse<Object> response = ApiResponse.success(
            "Location statistics retrieved successfully", 
            stats
        );
        
        return ResponseEntity.ok(response);
    }
}
