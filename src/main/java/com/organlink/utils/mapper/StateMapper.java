package com.organlink.utils.mapper;

import com.organlink.model.dto.StateDto;
import com.organlink.model.entity.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility for State entity and DTO conversions
 */
@Component
public class StateMapper {

    /**
     * Convert State entity to StateDto
     */
    public StateDto toDto(State state) {
        if (state == null) {
            return null;
        }

        StateDto dto = new StateDto();
        dto.setId(state.getId());
        dto.setName(state.getName());
        dto.setCode(state.getCode());
        dto.setCreatedAt(state.getCreatedAt());
        dto.setUpdatedAt(state.getUpdatedAt());

        // Set country information if available
        if (state.getCountry() != null) {
            dto.setCountryId(state.getCountry().getId());
            dto.setCountryName(state.getCountry().getName());
            dto.setCountryCode(state.getCountry().getCode());
        }

        return dto;
    }

    /**
     * Convert StateDto to State entity
     */
    public State toEntity(StateDto dto) {
        if (dto == null) {
            return null;
        }

        State state = new State();
        state.setId(dto.getId());
        state.setName(dto.getName());
        state.setCode(dto.getCode());

        // Note: Country relationship should be set separately in the service layer
        return state;
    }

    /**
     * Convert list of State entities to list of StateDtos
     */
    public List<StateDto> toDtoList(List<State> states) {
        if (states == null) {
            return null;
        }

        return states.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update existing State entity with data from StateDto
     */
    public void updateEntityFromDto(State state, StateDto dto) {
        if (state == null || dto == null) {
            return;
        }

        if (dto.getName() != null) {
            state.setName(dto.getName());
        }
        if (dto.getCode() != null) {
            state.setCode(dto.getCode());
        }
    }
}
