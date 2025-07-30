package com.organlink.utils.mapper;

import com.organlink.model.dto.HospitalDto;
import com.organlink.model.entity.Hospital;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility for Hospital entity and DTO conversions
 */
@Component
public class HospitalMapper {

    /**
     * Convert Hospital entity to HospitalDto
     */
    public HospitalDto toDto(Hospital hospital) {
        if (hospital == null) {
            return null;
        }

        HospitalDto dto = new HospitalDto();
        dto.setId(hospital.getId());
        dto.setName(hospital.getName());
        dto.setCode(hospital.getCode());
        dto.setAddress(hospital.getAddress());
        dto.setPhone(hospital.getPhone());
        dto.setEmail(hospital.getEmail());
        dto.setLicenseNumber(hospital.getLicenseNumber());
        dto.setIsActive(hospital.getIsActive());
        dto.setTenantId(hospital.getTenantId());
        dto.setCreatedAt(hospital.getCreatedAt());
        dto.setUpdatedAt(hospital.getUpdatedAt());

        // Set state information if available
        if (hospital.getState() != null) {
            dto.setStateId(hospital.getState().getId());
            dto.setStateName(hospital.getState().getName());
            dto.setStateCode(hospital.getState().getCode());

            // Set country information if available
            if (hospital.getState().getCountry() != null) {
                dto.setCountryName(hospital.getState().getCountry().getName());
                dto.setCountryCode(hospital.getState().getCountry().getCode());
            }
        }

        return dto;
    }

    /**
     * Convert HospitalDto to Hospital entity
     */
    public Hospital toEntity(HospitalDto dto) {
        if (dto == null) {
            return null;
        }

        Hospital hospital = new Hospital();
        hospital.setId(dto.getId());
        hospital.setName(dto.getName());
        hospital.setCode(dto.getCode());
        hospital.setAddress(dto.getAddress());
        hospital.setPhone(dto.getPhone());
        hospital.setEmail(dto.getEmail());
        hospital.setLicenseNumber(dto.getLicenseNumber());
        hospital.setIsActive(dto.getIsActive());
        hospital.setTenantId(dto.getTenantId());

        // Note: State relationship should be set separately in the service layer
        return hospital;
    }

    /**
     * Convert list of Hospital entities to list of HospitalDtos
     */
    public List<HospitalDto> toDtoList(List<Hospital> hospitals) {
        if (hospitals == null) {
            return null;
        }

        return hospitals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update existing Hospital entity with data from HospitalDto
     */
    public void updateEntityFromDto(Hospital hospital, HospitalDto dto) {
        if (hospital == null || dto == null) {
            return;
        }

        if (dto.getName() != null) {
            hospital.setName(dto.getName());
        }
        if (dto.getCode() != null) {
            hospital.setCode(dto.getCode());
        }
        if (dto.getAddress() != null) {
            hospital.setAddress(dto.getAddress());
        }
        if (dto.getPhone() != null) {
            hospital.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            hospital.setEmail(dto.getEmail());
        }
        if (dto.getLicenseNumber() != null) {
            hospital.setLicenseNumber(dto.getLicenseNumber());
        }
        if (dto.getIsActive() != null) {
            hospital.setIsActive(dto.getIsActive());
        }
        if (dto.getTenantId() != null) {
            hospital.setTenantId(dto.getTenantId());
        }
    }
}
