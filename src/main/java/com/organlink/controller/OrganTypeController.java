package com.organlink.controller;

import com.organlink.model.dto.OrganTypeDto;
import com.organlink.service.OrganTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for OrganType operations
 * Provides endpoints for managing organ types in the system
 */
@RestController
@RequestMapping("/organ-types")
@Tag(name = "Organ Types", description = "Operations related to organ type management")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrganTypeController {

    private final OrganTypeService organTypeService;

    @Autowired
    public OrganTypeController(OrganTypeService organTypeService) {
        this.organTypeService = organTypeService;
    }

    @Operation(summary = "Create a new organ type", description = "Creates a new organ type in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organ type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Organ type with same name already exists")
    })
    @PostMapping
    public ResponseEntity<OrganTypeDto> createOrganType(
            @Valid @RequestBody OrganTypeDto organTypeDto) {
        OrganTypeDto createdOrganType = organTypeService.createOrganType(organTypeDto);
        return new ResponseEntity<>(createdOrganType, HttpStatus.CREATED);
    }

    @Operation(summary = "Get organ type by ID", description = "Retrieves an organ type by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organ type found"),
            @ApiResponse(responseCode = "404", description = "Organ type not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrganTypeDto> getOrganTypeById(
            @Parameter(description = "Organ type ID") @PathVariable Long id) {
        OrganTypeDto organType = organTypeService.getOrganTypeById(id);
        return ResponseEntity.ok(organType);
    }

    @Operation(summary = "Get organ type by name", description = "Retrieves an organ type by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organ type found"),
            @ApiResponse(responseCode = "404", description = "Organ type not found")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<OrganTypeDto> getOrganTypeByName(
            @Parameter(description = "Organ type name") @PathVariable String name) {
        OrganTypeDto organType = organTypeService.getOrganTypeByName(name);
        return ResponseEntity.ok(organType);
    }

    @Operation(summary = "Get all organ types", description = "Retrieves all organ types in the system")
    @ApiResponse(responseCode = "200", description = "List of organ types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<OrganTypeDto>> getAllOrganTypes() {
        List<OrganTypeDto> organTypes = organTypeService.getAllOrganTypes();
        return ResponseEntity.ok(organTypes);
    }

    @Operation(summary = "Get active organ types", description = "Retrieves all active organ types")
    @ApiResponse(responseCode = "200", description = "List of active organ types retrieved successfully")
    @GetMapping("/active")
    public ResponseEntity<List<OrganTypeDto>> getActiveOrganTypes() {
        List<OrganTypeDto> organTypes = organTypeService.getActiveOrganTypes();
        return ResponseEntity.ok(organTypes);
    }

    @Operation(summary = "Search organ types", description = "Search organ types with pagination")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<Page<OrganTypeDto>> searchOrganTypes(
            @Parameter(description = "Search term") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrganTypeDto> organTypes = organTypeService.searchOrganTypes(searchTerm, isActive, pageable);
        return ResponseEntity.ok(organTypes);
    }

    @Operation(summary = "Update organ type", description = "Updates an existing organ type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organ type updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Organ type not found"),
            @ApiResponse(responseCode = "409", description = "Organ type with same name already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrganTypeDto> updateOrganType(
            @Parameter(description = "Organ type ID") @PathVariable Long id,
            @Valid @RequestBody OrganTypeDto organTypeDto) {
        OrganTypeDto updatedOrganType = organTypeService.updateOrganType(id, organTypeDto);
        return ResponseEntity.ok(updatedOrganType);
    }

    @Operation(summary = "Delete organ type", description = "Soft deletes an organ type (sets isActive to false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Organ type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Organ type not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganType(
            @Parameter(description = "Organ type ID") @PathVariable Long id) {
        organTypeService.deleteOrganType(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get urgent organ types", description = "Retrieves organ types suitable for urgent transplantation")
    @ApiResponse(responseCode = "200", description = "List of urgent organ types retrieved successfully")
    @GetMapping("/urgent")
    public ResponseEntity<List<OrganTypeDto>> getUrgentOrganTypes() {
        List<OrganTypeDto> organTypes = organTypeService.getUrgentOrganTypes();
        return ResponseEntity.ok(organTypes);
    }

    @Operation(summary = "Get planned organ types", description = "Retrieves organ types suitable for planned transplantation")
    @ApiResponse(responseCode = "200", description = "List of planned organ types retrieved successfully")
    @GetMapping("/planned")
    public ResponseEntity<List<OrganTypeDto>> getPlannedOrganTypes() {
        List<OrganTypeDto> organTypes = organTypeService.getPlannedOrganTypes();
        return ResponseEntity.ok(organTypes);
    }

    @Operation(summary = "Get organ types by preservation time range", 
               description = "Retrieves organ types within specified preservation time range")
    @ApiResponse(responseCode = "200", description = "List of organ types retrieved successfully")
    @GetMapping("/preservation-time")
    public ResponseEntity<List<OrganTypeDto>> getOrganTypesByPreservationTimeRange(
            @Parameter(description = "Minimum preservation time in hours") @RequestParam Integer minHours,
            @Parameter(description = "Maximum preservation time in hours") @RequestParam Integer maxHours) {
        List<OrganTypeDto> organTypes = organTypeService.getOrganTypesByPreservationTimeRange(minHours, maxHours);
        return ResponseEntity.ok(organTypes);
    }
}
