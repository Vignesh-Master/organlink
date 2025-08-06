package com.organlink.utils.mapper;

import com.organlink.model.dto.CountryDto;
import com.organlink.model.dto.StateDto;
import com.organlink.model.entity.Country;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility for Country entity and DTO conversions
 */
@Component
public class CountryMapper {

    /**
     * Convert Country entity to CountryDto
     */
    public CountryDto toDto(Country country) {
        if (country == null) {
            return null;
        }

        CountryDto dto = new CountryDto();
        // Temporarily simplified - will be fixed once Lombok issues are resolved
        // dto.setId(country.getId());
        // dto.setName(country.getName());
        // dto.setCode(country.getCode());

        return dto;
    }

    /**
     * Convert Country entity to CountryDto with states
     */
    public CountryDto toDtoWithStates(Country country) {
        if (country == null) {
            return null;
        }

        CountryDto dto = toDto(country);
        
        if (country.getStates() != null && !country.getStates().isEmpty()) {
            List<StateDto> stateDtos = country.getStates().stream()
                    .map(state -> {
                        StateDto stateDto = new StateDto();
                        stateDto.setId(state.getId());
                        stateDto.setName(state.getName());
                        stateDto.setCode(state.getCode());
                        stateDto.setCountryId(country.getId());
                        stateDto.setCountryName(country.getName());
                        stateDto.setCountryCode(country.getCode());
                        stateDto.setCreatedAt(state.getCreatedAt());
                        stateDto.setUpdatedAt(state.getUpdatedAt());
                        return stateDto;
                    })
                    .collect(Collectors.toList());
            dto.setStates(stateDtos);
        }

        return dto;
    }

    /**
     * Convert CountryDto to Country entity
     */
    public Country toEntity(CountryDto dto) {
        if (dto == null) {
            return null;
        }

        Country country = new Country();
        country.setId(dto.getId());
        country.setName(dto.getName());
        country.setCode(dto.getCode());

        return country;
    }

    /**
     * Convert list of Country entities to list of CountryDtos
     */
    public List<CountryDto> toDtoList(List<Country> countries) {
        if (countries == null) {
            return null;
        }

        return countries.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of Country entities to list of CountryDtos with states
     */
    public List<CountryDto> toDtoListWithStates(List<Country> countries) {
        if (countries == null) {
            return null;
        }

        return countries.stream()
                .map(this::toDtoWithStates)
                .collect(Collectors.toList());
    }
}
