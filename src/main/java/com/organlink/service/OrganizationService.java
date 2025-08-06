package com.organlink.service;

import com.organlink.model.entity.Organization;
import com.organlink.repository.OrganizationRepository;
import com.organlink.exception.DuplicateResourceException;
import com.organlink.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Organization management
 * Handles business logic for organization operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all organizations
     */
    @Transactional(readOnly = true)
    public List<Organization> getAllOrganizations() {
        logger.info("📋 Getting all organizations");
        return organizationRepository.findAllByOrderByNameAsc();
    }

    /**
     * Get all active organizations
     */
    @Transactional(readOnly = true)
    public List<Organization> getActiveOrganizations() {
        logger.info("📋 Getting active organizations");
        return organizationRepository.findByIsActiveTrueOrderByNameAsc();
    }

    /**
     * Get organization by ID
     */
    @Transactional(readOnly = true)
    public Organization getOrganizationById(Long id) {
        logger.info("🔍 Getting organization by ID: {}", id);
        return organizationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + id));
    }

    /**
     * Get organization by organization ID
     */
    @Transactional(readOnly = true)
    public Organization getOrganizationByOrgId(String orgId) {
        logger.info("🔍 Getting organization by orgId: {}", orgId);
        return organizationRepository.findByOrgIdIgnoreCase(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found with orgId: " + orgId));
    }

    /**
     * Get organization by username
     */
    @Transactional(readOnly = true)
    public Organization getOrganizationByUsername(String username) {
        logger.info("🔍 Getting organization by username: {}", username);
        return organizationRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found with username: " + username));
    }

    /**
     * Create new organization
     */
    public Organization createOrganization(Organization organization) {
        logger.info("🏢 Creating organization: {}", organization.getName());

        // Validate uniqueness
        validateOrganizationUniqueness(organization);

        // Encode password
        organization.setPassword(passwordEncoder.encode(organization.getPassword()));

        // Set defaults
        if (organization.getIsActive() == null) {
            organization.setIsActive(true);
        }
        if (organization.getCanVote() == null) {
            organization.setCanVote(true);
        }
        if (organization.getCanPropose() == null) {
            organization.setCanPropose(true);
        }
        if (organization.getRegisterBlockchain() == null) {
            organization.setRegisterBlockchain(false);
        }

        Organization savedOrganization = organizationRepository.save(organization);
        logger.info("✅ Organization created successfully: {} (ID: {})", 
                   savedOrganization.getName(), savedOrganization.getId());
        
        return savedOrganization;
    }

    /**
     * Update organization
     */
    public Organization updateOrganization(Long id, Organization organizationUpdate) {
        logger.info("📝 Updating organization: {}", id);

        Organization existingOrganization = getOrganizationById(id);

        // Update fields if provided
        if (organizationUpdate.getName() != null && !organizationUpdate.getName().trim().isEmpty()) {
            // Check name uniqueness (excluding current organization)
            if (!existingOrganization.getName().equalsIgnoreCase(organizationUpdate.getName()) &&
                organizationRepository.existsByNameIgnoreCase(organizationUpdate.getName())) {
                throw new DuplicateResourceException("Organization name already exists: " + organizationUpdate.getName());
            }
            existingOrganization.setName(organizationUpdate.getName().trim());
        }

        if (organizationUpdate.getLocation() != null) {
            existingOrganization.setLocation(organizationUpdate.getLocation().trim());
        }

        if (organizationUpdate.getCanVote() != null) {
            existingOrganization.setCanVote(organizationUpdate.getCanVote());
        }

        if (organizationUpdate.getCanPropose() != null) {
            existingOrganization.setCanPropose(organizationUpdate.getCanPropose());
        }

        if (organizationUpdate.getRegisterBlockchain() != null) {
            existingOrganization.setRegisterBlockchain(organizationUpdate.getRegisterBlockchain());
        }

        if (organizationUpdate.getTransactionHash() != null) {
            existingOrganization.setTransactionHash(organizationUpdate.getTransactionHash());
        }

        if (organizationUpdate.getBlockchainAddress() != null) {
            existingOrganization.setBlockchainAddress(organizationUpdate.getBlockchainAddress());
        }

        if (organizationUpdate.getIsActive() != null) {
            existingOrganization.setIsActive(organizationUpdate.getIsActive());
        }

        Organization updatedOrganization = organizationRepository.save(existingOrganization);
        logger.info("✅ Organization updated successfully: {}", updatedOrganization.getName());
        
        return updatedOrganization;
    }

    /**
     * Reset organization password
     */
    public void resetPassword(Long id, String newPassword) {
        logger.info("🔑 Resetting password for organization: {}", id);

        Organization organization = getOrganizationById(id);
        organization.setPassword(passwordEncoder.encode(newPassword));
        organizationRepository.save(organization);

        logger.info("✅ Password reset successfully for organization: {}", organization.getName());
    }

    /**
     * Delete organization (hard delete - physically removes from database)
     */
    public void deleteOrganization(Long id) {
        logger.info("🗑️ Hard deleting organization: {}", id);

        Organization organization = getOrganizationById(id);
        String organizationName = organization.getName();

        // Actually delete from database
        organizationRepository.delete(organization);

        logger.info("✅ Organization permanently deleted from database: {}", organizationName);
    }

    /**
     * Search organizations
     */
    @Transactional(readOnly = true)
    public List<Organization> searchOrganizations(String searchTerm) {
        logger.info("🔍 Searching organizations with term: {}", searchTerm);
        return organizationRepository.searchActiveOrganizations(searchTerm);
    }

    /**
     * Get organizations by criteria
     */
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsByCriteria(String location, Boolean canVote, 
                                                        Boolean canPropose, Boolean isActive) {
        logger.info("🔍 Getting organizations by criteria - Location: {}, CanVote: {}, CanPropose: {}, IsActive: {}", 
                   location, canVote, canPropose, isActive);
        return organizationRepository.findByCriteria(location, canVote, canPropose, isActive);
    }

    /**
     * Get organization statistics
     */
    @Transactional(readOnly = true)
    public OrganizationStats getOrganizationStats() {
        logger.info("📊 Getting organization statistics");
        
        long totalOrganizations = organizationRepository.count();
        long activeOrganizations = organizationRepository.countByIsActiveTrue();
        long blockchainEnabledOrganizations = organizationRepository.countByRegisterBlockchainTrueAndIsActiveTrue();
        
        return new OrganizationStats(totalOrganizations, activeOrganizations, blockchainEnabledOrganizations);
    }

    /**
     * Validate organization uniqueness
     */
    private void validateOrganizationUniqueness(Organization organization) {
        if (organizationRepository.existsByOrgIdIgnoreCase(organization.getOrgId())) {
            throw new DuplicateResourceException("Organization ID already exists: " + organization.getOrgId());
        }
        
        if (organizationRepository.existsByNameIgnoreCase(organization.getName())) {
            throw new DuplicateResourceException("Organization name already exists: " + organization.getName());
        }
        
        if (organizationRepository.existsByUsernameIgnoreCase(organization.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + organization.getUsername());
        }
    }

    /**
     * Statistics DTO
     */
    public static class OrganizationStats {
        private final long totalOrganizations;
        private final long activeOrganizations;
        private final long blockchainEnabledOrganizations;

        public OrganizationStats(long totalOrganizations, long activeOrganizations, long blockchainEnabledOrganizations) {
            this.totalOrganizations = totalOrganizations;
            this.activeOrganizations = activeOrganizations;
            this.blockchainEnabledOrganizations = blockchainEnabledOrganizations;
        }

        public long getTotalOrganizations() { return totalOrganizations; }
        public long getActiveOrganizations() { return activeOrganizations; }
        public long getBlockchainEnabledOrganizations() { return blockchainEnabledOrganizations; }
    }
}
