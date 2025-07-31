package com.organlink.service;

import com.organlink.model.entity.Donor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Policy Service for Global Collaboration Portal
 * Handles policy-based filtering with >50% vote requirement
 * 
 * YOUR REQUIREMENT:
 * - Only policies with >50% votes are considered
 * - Policies are organ-specific (kidney policies only affect kidney matching)
 * - AI considers policies only for relevant organ types
 */
@Service
public class PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    // Simulated policy database (in production, this would be from MySQL)
    private final Map<String, List<OrganPolicy>> organPolicies = new HashMap<>();

    /**
     * Initialize with sample policies for demo
     */
    public PolicyService() {
        initializeSamplePolicies();
    }

    /**
     * Get active policies for specific organ type with >50% votes
     */
    public List<String> getActivePolicies(String organType) {
        logger.info("🏛️ Getting active policies for organ type: {}", organType);

        List<OrganPolicy> policies = organPolicies.getOrDefault(organType.toLowerCase(), new ArrayList<>());
        
        List<String> activePolicies = policies.stream()
                .filter(policy -> policy.getVotePercentage() > 50.0) // >50% requirement
                .filter(OrganPolicy::isActive)
                .map(OrganPolicy::getPolicyId)
                .toList();

        logger.info("Found {} active policies for {}: {}", 
                   activePolicies.size(), organType, activePolicies);

        return activePolicies;
    }

    /**
     * Check if donor complies with active policies
     */
    public boolean isDonorCompliantWithPolicies(Donor donor, List<String> activePolicies) {
        if (activePolicies.isEmpty()) {
            return true; // No policies to check
        }

        for (String policyId : activePolicies) {
            if (!isDonorCompliantWithPolicy(donor, policyId)) {
                logger.debug("Donor {} failed policy compliance: {}", 
                           donor.getDonorName(), policyId);
                return false;
            }
        }

        return true;
    }

    /**
     * Check compliance with specific policy
     */
    private boolean isDonorCompliantWithPolicy(Donor donor, String policyId) {
        return switch (policyId) {
            case "KIDNEY_AGE_LIMIT" -> donor.getAge() <= 65; // Kidney donors must be ≤65
            case "LIVER_HEALTH_CHECK" -> checkLiverHealthCompliance(donor);
            case "HEART_EMERGENCY_PRIORITY" -> checkHeartEmergencyCompliance(donor);
            case "CORNEA_QUALITY_STANDARD" -> checkCorneaQualityCompliance(donor);
            case "KIDNEY_HLA_MATCHING" -> checkHLAMatchingCompliance(donor);
            default -> true; // Unknown policy, allow by default
        };
    }

    /**
     * Liver health compliance check
     */
    private boolean checkLiverHealthCompliance(Donor donor) {
        // Simulate liver health check
        String medicalHistory = donor.getMedicalHistory();
        if (medicalHistory != null) {
            return !medicalHistory.toLowerCase().contains("hepatitis") &&
                   !medicalHistory.toLowerCase().contains("cirrhosis");
        }
        return true;
    }

    /**
     * Heart emergency priority compliance
     */
    private boolean checkHeartEmergencyCompliance(Donor donor) {
        // Heart donors must be from emergency cases for this policy
        return "DECEASED".equals(donor.getDonorType()) &&
               donor.getCauseOfDeath() != null &&
               (donor.getCauseOfDeath().toLowerCase().contains("accident") ||
                donor.getCauseOfDeath().toLowerCase().contains("trauma"));
    }

    /**
     * Cornea quality standard compliance
     */
    private boolean checkCorneaQualityCompliance(Donor donor) {
        // Cornea donors must be within 6 hours of death
        if ("DECEASED".equals(donor.getDonorType()) && donor.getTimeOfDeath() != null) {
            // Simulate time check (in production, calculate actual time difference)
            return donor.getAge() <= 75; // Age limit for cornea
        }
        return true;
    }

    /**
     * HLA matching compliance
     */
    private boolean checkHLAMatchingCompliance(Donor donor) {
        // Require HLA typing for kidney donors
        return donor.getHlaTyping() != null && !donor.getHlaTyping().trim().isEmpty();
    }

    /**
     * Add new policy (for admin interface)
     */
    public void addPolicy(String organType, OrganPolicy policy) {
        logger.info("Adding new policy for {}: {}", organType, policy.getPolicyId());
        
        organPolicies.computeIfAbsent(organType.toLowerCase(), k -> new ArrayList<>()).add(policy);
    }

    /**
     * Update policy vote percentage
     */
    public void updatePolicyVotes(String organType, String policyId, double votePercentage) {
        logger.info("Updating votes for policy {}: {}%", policyId, votePercentage);
        
        List<OrganPolicy> policies = organPolicies.get(organType.toLowerCase());
        if (policies != null) {
            policies.stream()
                    .filter(p -> p.getPolicyId().equals(policyId))
                    .findFirst()
                    .ifPresent(policy -> policy.setVotePercentage(votePercentage));
        }
    }

    /**
     * Get all policies for organ type (for admin dashboard)
     */
    public List<OrganPolicy> getAllPolicies(String organType) {
        return organPolicies.getOrDefault(organType.toLowerCase(), new ArrayList<>());
    }

    /**
     * Initialize sample policies for demo
     */
    private void initializeSamplePolicies() {
        logger.info("🏛️ Initializing sample policies for demo");

        // Kidney policies
        List<OrganPolicy> kidneyPolicies = Arrays.asList(
                new OrganPolicy("KIDNEY_AGE_LIMIT", "Kidney Donor Age Limit (≤65 years)", 
                               "Kidney donors must be 65 years or younger", 75.5, true),
                new OrganPolicy("KIDNEY_HLA_MATCHING", "Mandatory HLA Typing for Kidneys", 
                               "All kidney donors must have HLA typing completed", 82.3, true),
                new OrganPolicy("KIDNEY_DISTANCE_LIMIT", "Maximum Distance for Kidney Transport", 
                               "Kidney transport limited to 500km radius", 45.2, false) // <50%, not active
        );

        // Liver policies
        List<OrganPolicy> liverPolicies = Arrays.asList(
                new OrganPolicy("LIVER_HEALTH_CHECK", "Liver Health Screening", 
                               "Exclude donors with hepatitis or cirrhosis history", 88.7, true),
                new OrganPolicy("LIVER_SIZE_MATCHING", "Liver Size Compatibility", 
                               "Match liver size within 20% of recipient requirement", 67.4, true)
        );

        // Heart policies
        List<OrganPolicy> heartPolicies = Arrays.asList(
                new OrganPolicy("HEART_EMERGENCY_PRIORITY", "Emergency Heart Donor Priority", 
                               "Prioritize heart donors from trauma/accident cases", 91.2, true),
                new OrganPolicy("HEART_AGE_STRICT", "Strict Heart Donor Age Limit", 
                               "Heart donors must be under 50 years", 42.8, false) // <50%, not active
        );

        // Cornea policies
        List<OrganPolicy> corneaPolicies = Arrays.asList(
                new OrganPolicy("CORNEA_QUALITY_STANDARD", "Cornea Quality Standards", 
                               "Cornea donors must be within 6 hours of death", 78.9, true),
                new OrganPolicy("CORNEA_AGE_LIMIT", "Cornea Donor Age Limit", 
                               "Cornea donors must be under 75 years", 65.1, true)
        );

        // Store policies
        organPolicies.put("kidney", kidneyPolicies);
        organPolicies.put("liver", liverPolicies);
        organPolicies.put("heart", heartPolicies);
        organPolicies.put("cornea", corneaPolicies);

        logger.info("✅ Initialized policies for {} organ types", organPolicies.size());
    }

    /**
     * Policy data class
     */
    public static class OrganPolicy {
        private String policyId;
        private String title;
        private String description;
        private double votePercentage;
        private boolean active;

        public OrganPolicy(String policyId, String title, String description, 
                          double votePercentage, boolean active) {
            this.policyId = policyId;
            this.title = title;
            this.description = description;
            this.votePercentage = votePercentage;
            this.active = active;
        }

        // Getters and setters
        public String getPolicyId() { return policyId; }
        public void setPolicyId(String policyId) { this.policyId = policyId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public double getVotePercentage() { return votePercentage; }
        public void setVotePercentage(double votePercentage) { this.votePercentage = votePercentage; }
        
        public boolean isActive() { return active && votePercentage > 50.0; }
        public void setActive(boolean active) { this.active = active; }

        @Override
        public String toString() {
            return String.format("%s (%.1f%% votes, %s)", 
                    title, votePercentage, isActive() ? "ACTIVE" : "INACTIVE");
        }
    }
}
