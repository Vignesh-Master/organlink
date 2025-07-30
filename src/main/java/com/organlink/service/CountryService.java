package com.organlink.service;

import com.organlink.exception.ResourceNotFoundException;
import com.organlink.exception.DuplicateResourceException;
import com.organlink.model.dto.CountryDto;
import com.organlink.model.entity.Country;
import com.organlink.repository.CountryRepository;
import com.organlink.utils.mapper.CountryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Country operations
 * Handles business logic for country management
 */
@Service
@Transactional
public class CountryService {

    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Autowired
    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    /**
     * Create a new country
     */
    public CountryDto createCountry(CountryDto countryDto) {
        logger.info("Creating new country: {}", countryDto.getName());

        // Check for duplicate name
        if (countryRepository.existsByNameIgnoreCase(countryDto.getName())) {
            throw new DuplicateResourceException("Country with name '" + countryDto.getName() + "' already exists");
        }

        // Check for duplicate code
        if (countryRepository.existsByCodeIgnoreCase(countryDto.getCode())) {
            throw new DuplicateResourceException("Country with code '" + countryDto.getCode() + "' already exists");
        }

        Country country = countryMapper.toEntity(countryDto);
        country.setCode(country.getCode().toUpperCase());
        
        Country savedCountry = countryRepository.save(country);
        logger.info("Country created successfully with ID: {}", savedCountry.getId());
        
        return countryMapper.toDto(savedCountry);
    }

    /**
     * Get all countries
     */
    @Transactional(readOnly = true)
    public List<CountryDto> getAllCountries() {
        logger.debug("Fetching all countries");
        
        List<Country> countries = countryRepository.findAllByOrderByNameAsc();
        return countries.stream()
                .map(countryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all countries with their states
     */
    @Transactional(readOnly = true)
    public List<CountryDto> getAllCountriesWithStates() {
        logger.debug("Fetching all countries with states");
        
        List<Country> countries = countryRepository.findAllWithStates();
        return countries.stream()
                .map(countryMapper::toDtoWithStates)
                .collect(Collectors.toList());
    }

    /**
     * Get country by ID
     */
    @Transactional(readOnly = true)
    public CountryDto getCountryById(Long id) {
        logger.debug("Fetching country by ID: {}", id);
        
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with ID: " + id));
        
        return countryMapper.toDto(country);
    }

    /**
     * Get country by ID with states
     */
    @Transactional(readOnly = true)
    public CountryDto getCountryByIdWithStates(Long id) {
        logger.debug("Fetching country with states by ID: {}", id);
        
        Country country = countryRepository.findByIdWithStates(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with ID: " + id));
        
        return countryMapper.toDtoWithStates(country);
    }

    /**
     * Get country by code
     */
    @Transactional(readOnly = true)
    public CountryDto getCountryByCode(String code) {
        logger.debug("Fetching country by code: {}", code);
        
        Country country = countryRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with code: " + code));
        
        return countryMapper.toDto(country);
    }

    /**
     * Update country
     */
    public CountryDto updateCountry(Long id, CountryDto countryDto) {
        logger.info("Updating country with ID: {}", id);
        
        Country existingCountry = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with ID: " + id));

        // Check for duplicate name (excluding current country)
        if (countryDto.getName() != null && 
            !countryDto.getName().equalsIgnoreCase(existingCountry.getName()) &&
            countryRepository.existsByNameIgnoreCase(countryDto.getName())) {
            throw new DuplicateResourceException("Country with name '" + countryDto.getName() + "' already exists");
        }

        // Update fields
        if (countryDto.getName() != null) {
            existingCountry.setName(countryDto.getName());
        }

        Country updatedCountry = countryRepository.save(existingCountry);
        logger.info("Country updated successfully: {}", updatedCountry.getId());
        
        return countryMapper.toDto(updatedCountry);
    }

    /**
     * Delete country
     */
    public void deleteCountry(Long id) {
        logger.info("Deleting country with ID: {}", id);
        
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with ID: " + id));

        // Check if country has states
        if (!country.getStates().isEmpty()) {
            throw new IllegalStateException("Cannot delete country with existing states");
        }

        countryRepository.delete(country);
        logger.info("Country deleted successfully: {}", id);
    }

    /**
     * Search countries by name
     */
    @Transactional(readOnly = true)
    public List<CountryDto> searchCountriesByName(String searchTerm) {
        logger.debug("Searching countries by name: {}", searchTerm);
        
        List<Country> countries = countryRepository.searchByName(searchTerm);
        return countries.stream()
                .map(countryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if country exists by code
     */
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return countryRepository.existsByCodeIgnoreCase(code);
    }

    /**
     * Check if country exists by name
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return countryRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Get total count of countries
     */
    @Transactional(readOnly = true)
    public long getTotalCount() {
        return countryRepository.countAll();
    }
}
