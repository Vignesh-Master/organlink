package com.organlink.service;

import com.organlink.exception.ResourceNotFoundException;
import com.organlink.model.dto.StateDto;
import com.organlink.model.entity.State;
import com.organlink.utils.mapper.StateMapper;
import com.organlink.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for State management operations
 * Handles business logic for state-related operations
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StateService {

    private static final Logger logger = LoggerFactory.getLogger(StateService.class);

    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    /**
     * Get all states
     */
    public List<StateDto> getAllStates() {
        logger.debug("Fetching all states");

        List<State> states = stateRepository.findAllWithCountry();
        return states.stream()
                .map(stateMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get states by country ID
     */
    public List<StateDto> getStatesByCountryId(Long countryId) {
        logger.debug("Fetching states for country ID: {}", countryId);

        List<State> states = stateRepository.findByCountryIdOrderByNameAsc(countryId);
        return states.stream()
                .map(stateMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get state by ID
     */
    public StateDto getStateById(Long id) {
        logger.debug("Fetching state by ID: {}", id);
        
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with ID: " + id));
        
        return stateMapper.toDto(state);
    }

    /**
     * Get state by code
     */
    public StateDto getStateByCode(String code) {
        logger.debug("Fetching state by code: {}", code);

        State state = stateRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with code: " + code));

        return stateMapper.toDto(state);
    }

    /**
     * Search states by name
     */
    public List<StateDto> searchStatesByName(String name) {
        logger.debug("Searching states with name containing: {}", name);

        List<State> states = stateRepository.findByNameContainingIgnoreCaseOrderByName(name);
        return states.stream()
                .map(stateMapper::toDto)
                .collect(Collectors.toList());
    }
}
