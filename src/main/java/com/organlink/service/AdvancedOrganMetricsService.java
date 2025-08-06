package com.organlink.service;

import com.organlink.model.entity.Donor;
import com.organlink.model.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

/**
 * Advanced Organ Metrics Service
 * Implements standard medical metrics: KDPI, KDRI, EPTS, MELD, etc.
 * 
 * YOUR CORRECT REQUIREMENT:
 * - Standard 7-8 metrics per organ type
 * - KDPI (Kidney Donor Profile Index)
 * - KDRI (Kidney Donor Risk Index) 
 * - EPTS (Estimated Post-Transplant Survival)
 * - MELD (Model for End-Stage Liver Disease)
 * - Geographic distance calculations
 * - Dynamic policy integration
 */
@Service
@RequiredArgsConstructor
public class AdvancedOrganMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(AdvancedOrganMetricsService.class);

    /**
     * Calculate comprehensive organ compatibility metrics
     */
    public OrganCompatibilityMetrics calculateOrganMetrics(Donor donor, Patient patient, 
                                                          Map<String, Double> activePolicies) {
        
        String organType = patient.getOrganType().getName().toLowerCase();
        logger.info("🧮 Calculating {} metrics for {} → {}",
                   organType, donor.getFullName(), patient.getFullName());

        OrganCompatibilityMetrics metrics = new OrganCompatibilityMetrics();
        
        // Calculate standard metrics based on organ type
        switch (organType) {
            case "kidney" -> calculateKidneyMetrics(donor, patient, metrics, activePolicies);
            case "liver" -> calculateLiverMetrics(donor, patient, metrics, activePolicies);
            case "heart" -> calculateHeartMetrics(donor, patient, metrics, activePolicies);
            case "lung" -> calculateLungMetrics(donor, patient, metrics, activePolicies);
            case "cornea" -> calculateCorneaMetrics(donor, patient, metrics, activePolicies);
            default -> calculateGeneralMetrics(donor, patient, metrics, activePolicies);
        }

        // Calculate overall compatibility score
        metrics.setOverallScore(calculateOverallScore(metrics));
        
        logger.info("✅ Metrics calculated: Overall Score = {:.2f}", metrics.getOverallScore());
        return metrics;
    }

    /**
     * KIDNEY METRICS: KDPI, KDRI, EPTS + Geographic + Policies
     */
    private void calculateKidneyMetrics(Donor donor, Patient patient, 
                                       OrganCompatibilityMetrics metrics, 
                                       Map<String, Double> activePolicies) {
        
        logger.debug("Calculating kidney-specific metrics");

        // 1. KDPI (Kidney Donor Profile Index) - 0 to 100
        double kdpi = calculateKDPI(donor);
        metrics.addMetric("KDPI", kdpi);

        // 2. KDRI (Kidney Donor Risk Index) - typically 0.5 to 3.0
        double kdri = calculateKDRI(donor);
        metrics.addMetric("KDRI", kdri);

        // 3. EPTS (Estimated Post-Transplant Survival) - 0 to 100
        double epts = calculateEPTS(patient);
        metrics.addMetric("EPTS", epts);

        // 4. HLA Compatibility (0 to 6 matches)
        double hlaMatches = calculateHLAMatches(donor, patient);
        metrics.addMetric("HLA_MATCHES", hlaMatches);

        // 5. Blood Type Compatibility
        double bloodCompatibility = calculateBloodCompatibility(donor, patient);
        metrics.addMetric("BLOOD_COMPATIBILITY", bloodCompatibility);

        // 6. Geographic Distance Score
        double geoScore = calculateGeographicDistance(donor, patient);
        metrics.addMetric("GEOGRAPHIC_DISTANCE", geoScore);

        // 7. Waiting Time Priority
        double waitingTimeScore = calculateWaitingTimeScore(patient);
        metrics.addMetric("WAITING_TIME", waitingTimeScore);

        // 8. Age Compatibility
        double ageCompatibility = calculateAgeCompatibility(donor, patient);
        metrics.addMetric("AGE_COMPATIBILITY", ageCompatibility);

        // 9. DYNAMIC POLICIES: Add policy-based metrics
        applyDynamicPolicies("kidney", activePolicies, metrics);
    }

    /**
     * LIVER METRICS: MELD Score + Standard Metrics + Policies
     */
    private void calculateLiverMetrics(Donor donor, Patient patient, 
                                      OrganCompatibilityMetrics metrics, 
                                      Map<String, Double> activePolicies) {
        
        logger.debug("Calculating liver-specific metrics");

        // 1. MELD Score (Model for End-Stage Liver Disease) - 6 to 40
        double meldScore = calculateMELDScore(patient);
        metrics.addMetric("MELD_SCORE", meldScore);

        // 2. Liver Size Compatibility
        double sizeCompatibility = calculateLiverSizeCompatibility(donor, patient);
        metrics.addMetric("SIZE_COMPATIBILITY", sizeCompatibility);

        // 3. Blood Type Compatibility
        double bloodCompatibility = calculateBloodCompatibility(donor, patient);
        metrics.addMetric("BLOOD_COMPATIBILITY", bloodCompatibility);

        // 4. Geographic Distance
        double geoScore = calculateGeographicDistance(donor, patient);
        metrics.addMetric("GEOGRAPHIC_DISTANCE", geoScore);

        // 5. Donor Liver Quality
        double liverQuality = calculateLiverQuality(donor);
        metrics.addMetric("LIVER_QUALITY", liverQuality);

        // 6. Urgency Score
        double urgencyScore = calculateUrgencyScore(patient);
        metrics.addMetric("URGENCY_SCORE", urgencyScore);

        // 7. Age Compatibility
        double ageCompatibility = calculateAgeCompatibility(donor, patient);
        metrics.addMetric("AGE_COMPATIBILITY", ageCompatibility);

        // 8. DYNAMIC POLICIES
        applyDynamicPolicies("liver", activePolicies, metrics);
    }

    /**
     * HEART METRICS: Cardiac-specific metrics + Policies
     */
    private void calculateHeartMetrics(Donor donor, Patient patient, 
                                      OrganCompatibilityMetrics metrics, 
                                      Map<String, Double> activePolicies) {
        
        logger.debug("Calculating heart-specific metrics");

        // 1. Heart Size Compatibility
        double sizeCompatibility = calculateHeartSizeCompatibility(donor, patient);
        metrics.addMetric("HEART_SIZE_COMPATIBILITY", sizeCompatibility);

        // 2. Blood Type Compatibility
        double bloodCompatibility = calculateBloodCompatibility(donor, patient);
        metrics.addMetric("BLOOD_COMPATIBILITY", bloodCompatibility);

        // 3. Geographic Distance (Critical for heart - 4-6 hour limit)
        double geoScore = calculateGeographicDistance(donor, patient);
        metrics.addMetric("GEOGRAPHIC_DISTANCE", geoScore);

        // 4. Cardiac Function Score
        double cardiacFunction = calculateCardiacFunctionScore(donor);
        metrics.addMetric("CARDIAC_FUNCTION", cardiacFunction);

        // 5. Urgency Score (Critical for heart patients)
        double urgencyScore = calculateUrgencyScore(patient);
        metrics.addMetric("URGENCY_SCORE", urgencyScore);

        // 6. Age Compatibility
        double ageCompatibility = calculateAgeCompatibility(donor, patient);
        metrics.addMetric("AGE_COMPATIBILITY", ageCompatibility);

        // 7. Ischemic Time Prediction
        double ischemicTimeScore = calculateIschemicTimeScore(donor, patient);
        metrics.addMetric("ISCHEMIC_TIME", ischemicTimeScore);

        // 8. DYNAMIC POLICIES
        applyDynamicPolicies("heart", activePolicies, metrics);
    }

    /**
     * Apply dynamic policies from Global Collaboration Portal
     * This is where your >50% voted policies become AI metrics
     */
    private void applyDynamicPolicies(String organType, Map<String, Double> activePolicies, 
                                     OrganCompatibilityMetrics metrics) {
        
        if (activePolicies == null || activePolicies.isEmpty()) {
            logger.debug("No active policies for {} - using standard metrics only", organType);
            return;
        }

        logger.info("🏛️ Applying {} active policies for {} matching", 
                   activePolicies.size(), organType);

        for (Map.Entry<String, Double> policy : activePolicies.entrySet()) {
            String policyName = policy.getKey();
            Double policyValue = policy.getValue();
            
            // Add policy as additional metric
            metrics.addMetric("POLICY_" + policyName, policyValue);
            
            logger.debug("Applied policy metric: {} = {}", policyName, policyValue);
        }
    }

    /**
     * Calculate KDPI (Kidney Donor Profile Index)
     * Range: 0-100 (lower is better)
     */
    private double calculateKDPI(Donor donor) {
        // Simplified KDPI calculation (in production, use official UNOS formula)
        double kdpi = 0.0;
        
        // Age factor (most significant)
        int age = donor.calculateAge();
        if (age <= 35) kdpi += 10;
        else if (age <= 50) kdpi += 25;
        else if (age <= 65) kdpi += 50;
        else kdpi += 85;
        
        // Donor type factor - simplified since we don't have this field in new entity
        // In production, this would be determined by other factors

        // Medical history factor - simplified since we don't have this field in new entity
        // In production, this would come from medical records
        
        return Math.min(100, kdpi);
    }

    /**
     * Calculate KDRI (Kidney Donor Risk Index)
     * Range: 0.5-3.0 (lower is better)
     */
    private double calculateKDRI(Donor donor) {
        // Simplified KDRI calculation
        double kdri = 1.0; // Base value
        
        // Age adjustment
        int age = donor.calculateAge();
        if (age > 50) {
            kdri += (age - 50) * 0.01;
        }

        // Donor type adjustment - simplified
        // In production, this would be determined by other factors
        
        return Math.min(3.0, kdri);
    }

    /**
     * Calculate EPTS (Estimated Post-Transplant Survival)
     * Range: 0-100 (lower is better for patient)
     */
    private double calculateEPTS(Patient patient) {
        // Simplified EPTS calculation
        double epts = 0.0;
        
        // Age factor
        int age = patient.calculateAge();
        if (age <= 25) epts += 5;
        else if (age <= 40) epts += 15;
        else if (age <= 60) epts += 35;
        else epts += 65;

        // Urgency factor
        if (Patient.UrgencyLevel.CRITICAL.equals(patient.getUrgencyLevel())) {
            epts += 20;
        }
        
        return Math.min(100, epts);
    }

    /**
     * Calculate MELD Score for liver patients
     * Range: 6-40 (higher means more urgent)
     */
    private double calculateMELDScore(Patient patient) {
        // Simplified MELD calculation (in production, use lab values)
        double meld = 10; // Base score
        
        // Urgency adjustment
        switch (patient.getUrgencyLevel()) {
            case CRITICAL -> meld += 25;
            case HIGH -> meld += 15;
            case MEDIUM -> meld += 8;
            case LOW -> meld += 3;
        }

        // Age adjustment
        if (patient.calculateAge() > 60) {
            meld += 5;
        }
        
        return Math.min(40, meld);
    }

    /**
     * Calculate geographic distance score
     * Range: 0-1 (higher is better - closer distance)
     */
    private double calculateGeographicDistance(Donor donor, Patient patient) {
        // Simplified distance calculation based on hospital IDs
        // In production, use actual GPS coordinates
        
        // Simplified hospital distance calculation
        long hospitalDistance = 1L; // In production, calculate actual distance
        
        if (hospitalDistance == 0) return 1.0; // Same hospital
        if (hospitalDistance == 1) return 0.8; // Adjacent regions
        if (hospitalDistance <= 3) return 0.6; // Regional
        if (hospitalDistance <= 5) return 0.4; // National
        return 0.2; // International
    }

    // Additional metric calculation methods...
    private double calculateHLAMatches(Donor donor, Patient patient) {
        // Simulate HLA matching (0-6 matches)
        return Math.random() * 6;
    }

    private double calculateBloodCompatibility(Donor donor, Patient patient) {
        // Blood type compatibility logic
        if (donor.getBloodGroup().equals(patient.getBloodGroup())) return 1.0;
        if ("O-".equals(donor.getBloodGroup())) return 0.9; // Universal donor
        if ("AB+".equals(patient.getBloodGroup())) return 0.9; // Universal recipient
        return 0.3; // Incompatible
    }

    private double calculateWaitingTimeScore(Patient patient) {
        // Waiting time priority (longer wait = higher score)
        if (patient.getRegistrationDate() != null) {
            long daysSinceRegistration = Period.between(
                patient.getRegistrationDate().toLocalDate(),
                LocalDate.now()
            ).getDays();
            return Math.min(1.0, daysSinceRegistration / 365.0); // Max 1 year
        }
        return 0.0;
    }

    private double calculateAgeCompatibility(Donor donor, Patient patient) {
        int ageDiff = Math.abs(donor.calculateAge() - patient.calculateAge());
        if (ageDiff <= 10) return 1.0;
        if (ageDiff <= 20) return 0.8;
        if (ageDiff <= 30) return 0.6;
        return 0.4;
    }

    private double calculateUrgencyScore(Patient patient) {
        return switch (patient.getUrgencyLevel()) {
            case CRITICAL -> 1.0;
            case HIGH -> 0.8;
            case MEDIUM -> 0.6;
            case LOW -> 0.4;
            default -> 0.2;
        };
    }

    // Placeholder methods for organ-specific calculations
    private void calculateLungMetrics(Donor donor, Patient patient, OrganCompatibilityMetrics metrics, Map<String, Double> activePolicies) {
        calculateGeneralMetrics(donor, patient, metrics, activePolicies);
    }

    private void calculateCorneaMetrics(Donor donor, Patient patient, OrganCompatibilityMetrics metrics, Map<String, Double> activePolicies) {
        calculateGeneralMetrics(donor, patient, metrics, activePolicies);
    }

    private void calculateGeneralMetrics(Donor donor, Patient patient, OrganCompatibilityMetrics metrics, Map<String, Double> activePolicies) {
        metrics.addMetric("BLOOD_COMPATIBILITY", calculateBloodCompatibility(donor, patient));
        metrics.addMetric("AGE_COMPATIBILITY", calculateAgeCompatibility(donor, patient));
        metrics.addMetric("GEOGRAPHIC_DISTANCE", calculateGeographicDistance(donor, patient));
        applyDynamicPolicies("general", activePolicies, metrics);
    }

    private double calculateLiverSizeCompatibility(Donor donor, Patient patient) { return 0.8; }
    private double calculateLiverQuality(Donor donor) { return 0.9; }
    private double calculateHeartSizeCompatibility(Donor donor, Patient patient) { return 0.85; }
    private double calculateCardiacFunctionScore(Donor donor) { return 0.9; }
    private double calculateIschemicTimeScore(Donor donor, Patient patient) { return 0.8; }

    /**
     * Calculate overall compatibility score from all metrics
     */
    private double calculateOverallScore(OrganCompatibilityMetrics metrics) {
        if (metrics.getMetrics().isEmpty()) return 0.0;
        
        double totalScore = 0.0;
        int count = 0;
        
        for (Double value : metrics.getMetrics().values()) {
            totalScore += value;
            count++;
        }
        
        return count > 0 ? totalScore / count : 0.0;
    }

    /**
     * Metrics container class
     */
    public static class OrganCompatibilityMetrics {
        private final Map<String, Double> metrics = new HashMap<>();
        private double overallScore = 0.0;

        public void addMetric(String name, double value) {
            metrics.put(name, value);
        }

        public Map<String, Double> getMetrics() { return metrics; }
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        
        public double getMetric(String name) {
            return metrics.getOrDefault(name, 0.0);
        }
    }
}
