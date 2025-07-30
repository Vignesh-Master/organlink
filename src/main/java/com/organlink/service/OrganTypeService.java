package com.organlink.service;

import com.organlink.model.dto.OrganTypeDto;
import com.organlink.model.entity.OrganType;
import com.organlink.repository.OrganTypeRepository;
import com.organlink.exception.ResourceNotFoundException;
import com.organlink.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for OrganType operations
 * Handles business logic for organ type management
 */
@Service
@Transactional
public class OrganTypeService {

    private final OrganTypeRepository organTypeRepository;

    @Autowired
    public OrganTypeService(OrganTypeRepository organTypeRepository) {
        this.organTypeRepository = organTypeRepository;
    }

    /**
     * Create a new organ type
     */
    public OrganTypeDto createOrganType(OrganTypeDto organTypeDto) {
        // Check if organ type with same name already exists
        if (organTypeRepository.existsByNameIgnoreCase(organTypeDto.getName())) {
            throw new DuplicateResourceException("Organ type with name '" + organTypeDto.getName() + "' already exists");
        }

        OrganType organType = convertToEntity(organTypeDto);
        OrganType savedOrganType = organTypeRepository.save(organType);
        return convertToDto(savedOrganType);
    }

    /**
     * Get organ type by ID
     */
    @Transactional(readOnly = true)
    public OrganTypeDto getOrganTypeById(Long id) {
        OrganType organType = organTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organ type not found with id: " + id));
        return convertToDto(organType);
    }

    /**
     * Get organ type by name
     */
    @Transactional(readOnly = true)
    public OrganTypeDto getOrganTypeByName(String name) {
        OrganType organType = organTypeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Organ type not found with name: " + name));
        return convertToDto(organType);
    }

    /**
     * Get all organ types
     */
    @Transactional(readOnly = true)
    public List<OrganTypeDto> getAllOrganTypes() {
        return organTypeRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all active organ types
     */
    @Transactional(readOnly = true)
    public List<OrganTypeDto> getActiveOrganTypes() {
        return organTypeRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Search organ types with pagination
     */
    @Transactional(readOnly = true)
    public Page<OrganTypeDto> searchOrganTypes(String searchTerm, Boolean isActive, Pageable pageable) {
        Page<OrganType> organTypes = organTypeRepository.searchOrganTypes(searchTerm, isActive, pageable);
        return organTypes.map(this::convertToDto);
    }

    /**
     * Update organ type
     */
    public OrganTypeDto updateOrganType(Long id, OrganTypeDto organTypeDto) {
        OrganType existingOrganType = organTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organ type not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!existingOrganType.getName().equalsIgnoreCase(organTypeDto.getName()) &&
            organTypeRepository.existsByNameIgnoreCase(organTypeDto.getName())) {
            throw new DuplicateResourceException("Organ type with name '" + organTypeDto.getName() + "' already exists");
        }

        // Update fields
        existingOrganType.setName(organTypeDto.getName());
        existingOrganType.setDescription(organTypeDto.getDescription());
        existingOrganType.setPreservationTimeHours(organTypeDto.getPreservationTimeHours());
        existingOrganType.setCompatibilityFactors(organTypeDto.getCompatibilityFactors());
        existingOrganType.setIsActive(organTypeDto.getIsActive());

        OrganType updatedOrganType = organTypeRepository.save(existingOrganType);
        return convertToDto(updatedOrganType);
    }

    /**
     * Delete organ type (soft delete by setting isActive to false)
     */
    public void deleteOrganType(Long id) {
        OrganType organType = organTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organ type not found with id: " + id));
        
        organType.setIsActive(false);
        organTypeRepository.save(organType);
    }

    /**
     * Get organ types suitable for urgent transplantation
     */
    @Transactional(readOnly = true)
    public List<OrganTypeDto> getUrgentOrganTypes() {
        return organTypeRepository.findUrgentOrganTypes()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get organ types suitable for planned transplantation
     */
    @Transactional(readOnly = true)
    public List<OrganTypeDto> getPlannedOrganTypes() {
        return organTypeRepository.findPlannedOrganTypes()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get organ types by preservation time range
     */
    @Transactional(readOnly = true)
    public List<OrganTypeDto> getOrganTypesByPreservationTimeRange(Integer minHours, Integer maxHours) {
        return organTypeRepository.findByPreservationTimeRange(minHours, maxHours)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert OrganType entity to DTO
     */
    private OrganTypeDto convertToDto(OrganType organType) {
        OrganTypeDto dto = new OrganTypeDto();
        dto.setId(organType.getId());
        dto.setName(organType.getName());
        dto.setDescription(organType.getDescription());
        dto.setPreservationTimeHours(organType.getPreservationTimeHours());
        dto.setCompatibilityFactors(organType.getCompatibilityFactors());
        dto.setIsActive(organType.getIsActive());
        dto.setCreatedAt(organType.getCreatedAt());
        dto.setUpdatedAt(organType.getUpdatedAt());
        return dto;
    }

    /**
     * Convert OrganTypeDto to entity
     */
    private OrganType convertToEntity(OrganTypeDto dto) {
        OrganType organType = new OrganType();
        organType.setName(dto.getName());
        organType.setDescription(dto.getDescription());
        organType.setPreservationTimeHours(dto.getPreservationTimeHours());
        organType.setCompatibilityFactors(dto.getCompatibilityFactors());
        organType.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return organType;
    }
}
