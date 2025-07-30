package com.organlink.service;

import com.organlink.exception.ResourceNotFoundException;
import com.organlink.exception.DuplicateResourceException;
import com.organlink.model.dto.HospitalDto;
import com.organlink.model.entity.Hospital;
import com.organlink.model.entity.State;
import com.organlink.repository.HospitalRepository;
import com.organlink.repository.StateRepository;
import com.organlink.utils.mapper.HospitalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Hospital operations
 * Handles business logic for hospital management
 */
@Service
@Transactional
public class HospitalService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalService.class);

    private final HospitalRepository hospitalRepository;
    private final StateRepository stateRepository;
    private final HospitalMapper hospitalMapper;

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository, 
                          StateRepository stateRepository,
                          HospitalMapper hospitalMapper) {
        this.hospitalRepository = hospitalRepository;
        this.stateRepository = stateRepository;
        this.hospitalMapper = hospitalMapper;
    }

    /**
     * Create a new hospital
     */
    public HospitalDto createHospital(HospitalDto hospitalDto) {
        logger.info("Creating new hospital: {}", hospitalDto.getName());

        // Validate state exists
        State state = stateRepository.findById(hospitalDto.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found with ID: " + hospitalDto.getStateId()));

        // Check for duplicate code
        if (hospitalRepository.existsByCodeIgnoreCase(hospitalDto.getCode())) {
            throw new DuplicateResourceException("Hospital with code '" + hospitalDto.getCode() + "' already exists");
        }

        // Check for duplicate tenant ID
        if (hospitalRepository.existsByTenantId(hospitalDto.getTenantId())) {
            throw new DuplicateResourceException("Hospital with tenant ID '" + hospitalDto.getTenantId() + "' already exists");
        }

        Hospital hospital = hospitalMapper.toEntity(hospitalDto);
        hospital.setState(state);
        hospital.setCode(hospital.getCode().toUpperCase());
        hospital.setTenantId(hospital.getTenantId().toLowerCase());
        
        Hospital savedHospital = hospitalRepository.save(hospital);
        logger.info("Hospital created successfully with ID: {}", savedHospital.getId());
        
        return hospitalMapper.toDto(savedHospital);
    }

    /**
     * Get all hospitals with pagination and filtering
     */
    @Transactional(readOnly = true)
    public Page<HospitalDto> getAllHospitals(String searchTerm, Long stateId, Boolean isActive, Pageable pageable) {
        logger.debug("Fetching hospitals with search: {}, stateId: {}, isActive: {}", searchTerm, stateId, isActive);
        
        Page<Hospital> hospitals = hospitalRepository.searchHospitals(searchTerm, stateId, isActive, pageable);
        return hospitals.map(hospitalMapper::toDto);
    }

    /**
     * Get hospital by ID
     */
    @Transactional(readOnly = true)
    public HospitalDto getHospitalById(Long id) {
        logger.debug("Fetching hospital by ID: {}", id);
        
        Hospital hospital = hospitalRepository.findByIdWithStateAndCountry(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));
        
        return hospitalMapper.toDto(hospital);
    }

    /**
     * Get hospital by code
     */
    @Transactional(readOnly = true)
    public HospitalDto getHospitalByCode(String code) {
        logger.debug("Fetching hospital by code: {}", code);
        
        Hospital hospital = hospitalRepository.findByCodeWithStateAndCountry(code)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with code: " + code));
        
        return hospitalMapper.toDto(hospital);
    }

    /**
     * Get hospital by tenant ID
     */
    @Transactional(readOnly = true)
    public HospitalDto getHospitalByTenantId(String tenantId) {
        logger.debug("Fetching hospital by tenant ID: {}", tenantId);
        
        Hospital hospital = hospitalRepository.findByTenantIdWithStateAndCountry(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with tenant ID: " + tenantId));
        
        return hospitalMapper.toDto(hospital);
    }

    /**
     * Get hospitals by state ID
     */
    @Transactional(readOnly = true)
    public List<HospitalDto> getHospitalsByStateId(Long stateId) {
        logger.debug("Fetching hospitals by state ID: {}", stateId);
        
        List<Hospital> hospitals = hospitalRepository.findByStateIdOrderByNameAsc(stateId);
        return hospitals.stream()
                .map(hospitalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get active hospitals by state ID
     */
    @Transactional(readOnly = true)
    public List<HospitalDto> getActiveHospitalsByStateId(Long stateId) {
        logger.debug("Fetching active hospitals by state ID: {}", stateId);
        
        List<Hospital> hospitals = hospitalRepository.findByStateIdAndIsActiveTrueOrderByNameAsc(stateId);
        return hospitals.stream()
                .map(hospitalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all active hospitals
     */
    @Transactional(readOnly = true)
    public List<HospitalDto> getAllActiveHospitals() {
        logger.debug("Fetching all active hospitals");
        
        List<Hospital> hospitals = hospitalRepository.findByIsActiveTrueOrderByNameAsc();
        return hospitals.stream()
                .map(hospitalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update hospital
     */
    public HospitalDto updateHospital(Long id, HospitalDto hospitalDto) {
        logger.info("Updating hospital with ID: {}", id);
        
        Hospital existingHospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));

        // Update fields if provided
        if (hospitalDto.getName() != null) {
            existingHospital.setName(hospitalDto.getName());
        }
        if (hospitalDto.getAddress() != null) {
            existingHospital.setAddress(hospitalDto.getAddress());
        }
        if (hospitalDto.getPhone() != null) {
            existingHospital.setPhone(hospitalDto.getPhone());
        }
        if (hospitalDto.getEmail() != null) {
            existingHospital.setEmail(hospitalDto.getEmail());
        }
        if (hospitalDto.getLicenseNumber() != null) {
            existingHospital.setLicenseNumber(hospitalDto.getLicenseNumber());
        }
        if (hospitalDto.getIsActive() != null) {
            existingHospital.setIsActive(hospitalDto.getIsActive());
        }

        Hospital updatedHospital = hospitalRepository.save(existingHospital);
        logger.info("Hospital updated successfully: {}", updatedHospital.getId());
        
        return hospitalMapper.toDto(updatedHospital);
    }

    /**
     * Delete hospital (soft delete by setting isActive to false)
     */
    public void deleteHospital(Long id) {
        logger.info("Deleting hospital with ID: {}", id);
        
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));

        hospital.setIsActive(false);
        hospitalRepository.save(hospital);
        logger.info("Hospital soft deleted successfully: {}", id);
    }

    /**
     * Get hospitals by country ID
     */
    @Transactional(readOnly = true)
    public List<HospitalDto> getHospitalsByCountryId(Long countryId) {
        logger.debug("Fetching hospitals by country ID: {}", countryId);
        
        List<Hospital> hospitals = hospitalRepository.findByCountryId(countryId);
        return hospitals.stream()
                .map(hospitalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if hospital exists by code
     */
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return hospitalRepository.existsByCodeIgnoreCase(code);
    }

    /**
     * Check if hospital exists by tenant ID
     */
    @Transactional(readOnly = true)
    public boolean existsByTenantId(String tenantId) {
        return hospitalRepository.existsByTenantId(tenantId);
    }

    /**
     * Get total count of active hospitals
     */
    @Transactional(readOnly = true)
    public long getActiveHospitalCount() {
        return hospitalRepository.countByIsActiveTrue();
    }
}
