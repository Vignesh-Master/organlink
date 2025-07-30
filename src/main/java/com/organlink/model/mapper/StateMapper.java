package com.organlink.model.mapper;

import com.organlink.model.dto.StateDto;
import com.organlink.model.entity.State;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for State entity and DTO conversion
 */
@Mapper(componentModel = "spring")
public interface StateMapper {

    StateMapper INSTANCE = Mappers.getMapper(StateMapper.class);

    /**
     * Convert State entity to StateDto
     */
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "country.name", target = "countryName")
    StateDto toDto(State state);

    /**
     * Convert StateDto to State entity
     */
    @Mapping(source = "countryId", target = "country.id")
    State toEntity(StateDto stateDto);
}
