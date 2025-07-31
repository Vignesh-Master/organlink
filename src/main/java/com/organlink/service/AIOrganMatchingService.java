package com.organlink.service;

import com.organlink.model.entity.Donor;
import com.organlink.model.entity.Patient;
import com.organlink.repository.DonorRepository;
import com.organlink.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-Powered Organ Matching Service using Random Forest
 * 
 * YOUR DEMO SCENARIO:
 * 1. Login Chennai Hospital → Register Liver Donor
 * 2. Login Delhi Hospital → Register Kidney Patient  
 * 3. AI finds cross-hospital matches
 * 4. Notifications sent to compatible hospitals
 * 5. Policy-based filtering applied
 */
@Service
@RequiredArgsConstructor
public class AIOrganMatchingService {

    private static final Logger logger = LoggerFactory.getLogger(AIOrganMatchingService.class);

    private final DonorRepository donorRepository;
    private final PatientRepository patientRepository;
    private final PolicyService policyService;
    private final NotificationService notificationService;

    // Random Forest model for compatibility scoring
    private RandomForest matchingModel;
    private Instances trainingDataset;

    /**
     * MAIN DEMO METHOD: Find matches when patient is registered
     * This is what happens when Delhi hospital registers kidney patient
     */
    public List<OrganMatchResult> findOptimalMatches(Patient patient) {
        logger.info("🤖 AI MATCHING: Finding matches for {} needing {}", 
                   patient.getPatientName(), patient.getOrganNeeded());

        try {
            // Step 1: Get all available donors for the needed organ (cross-hospital)
            List<Donor> availableDonors = donorRepository
                    .findByOrganAvailableAndStatusAndIsActiveTrue(
                            patient.getOrganNeeded(), "AVAILABLE");

            logger.info("Found {} available {} donors across all hospitals", 
                       availableDonors.size(), patient.getOrganNeeded());

            // Step 2: Apply policy-based filtering
            List<Donor> policyFilteredDonors = applyPolicyFiltering(availableDonors, patient);

            // Step 3: Calculate AI compatibility scores using Random Forest
            List<OrganMatchResult> matches = new ArrayList<>();
            
            for (Donor donor : policyFilteredDonors) {
                double compatibilityScore = calculateAICompatibilityScore(donor, patient);
                
                if (compatibilityScore >= 0.7) { // 70% threshold for viable match
                    OrganMatchResult match = new OrganMatchResult(
                            donor, patient, compatibilityScore,
                            calculateDistanceScore(donor, patient),
                            calculateUrgencyScore(patient),
                            calculateBloodCompatibility(donor, patient)
                    );
                    matches.add(match);
                }
            }

            // Step 4: Sort by compatibility score (best matches first)
            matches.sort((a, b) -> Double.compare(b.getCompatibilityScore(), a.getCompatibilityScore()));

            // Step 5: Send notifications to hospitals with matches
            if (!matches.isEmpty()) {
                sendMatchNotifications(matches, patient);
            }

            logger.info("🎯 AI MATCHING COMPLETE: Found {} optimal matches for {}", 
                       matches.size(), patient.getPatientName());

            return matches;

        } catch (Exception e) {
            logger.error("AI matching failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Apply policy-based filtering (your requirement)
     * Only considers policies with >50% votes for relevant organ type
     */
    private List<Donor> applyPolicyFiltering(List<Donor> donors, Patient patient) {
        logger.info("🏛️ Applying policy-based filtering for {}", patient.getOrganNeeded());

        // Get active policies for this organ type with >50% votes
        List<String> activePolicies = policyService.getActivePolicies(patient.getOrganNeeded());
        
        if (activePolicies.isEmpty()) {
            logger.info("No active policies for {}, using all donors", patient.getOrganNeeded());
            return donors;
        }

        // Apply policy filters
        List<Donor> filteredDonors = donors.stream()
                .filter(donor -> policyService.isDonorCompliantWithPolicies(donor, activePolicies))
                .collect(Collectors.toList());

        logger.info("Policy filtering: {} → {} donors", donors.size(), filteredDonors.size());
        return filteredDonors;
    }

    /**
     * Calculate AI compatibility score using Random Forest
     * This is where the ML magic happens!
     */
    private double calculateAICompatibilityScore(Donor donor, Patient patient) {
        try {
            // Initialize model if not already done
            if (matchingModel == null) {
                initializeRandomForestModel();
            }

            // Create feature vector for this donor-patient pair
            Instance instance = createFeatureVector(donor, patient);
            
            // Get probability distribution from Random Forest
            double[] probabilities = matchingModel.distributionForInstance(instance);
            
            // Return probability of positive match (class 1)
            double score = probabilities.length > 1 ? probabilities[1] : probabilities[0];
            
            logger.debug("AI Compatibility Score: {:.3f} for {} → {}", 
                        score, donor.getDonorName(), patient.getPatientName());
            
            return score;

        } catch (Exception e) {
            logger.error("AI scoring failed, using fallback: {}", e.getMessage());
            return calculateFallbackCompatibilityScore(donor, patient);
        }
    }

    /**
     * Initialize Random Forest model with training data
     */
    private void initializeRandomForestModel() {
        try {
            logger.info("🌲 Initializing Random Forest model for organ matching");

            // Create dataset structure
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(new Attribute("bloodCompatibility"));
            attributes.add(new Attribute("ageCompatibility"));
            attributes.add(new Attribute("hlaCompatibility"));
            attributes.add(new Attribute("urgencyScore"));
            attributes.add(new Attribute("distanceScore"));
            attributes.add(new Attribute("organQuality"));
            
            // Class attribute (match success)
            ArrayList<String> classValues = new ArrayList<>();
            classValues.add("NO_MATCH");
            classValues.add("MATCH");
            attributes.add(new Attribute("matchResult", classValues));

            // Create training dataset
            trainingDataset = new Instances("OrganMatching", attributes, 0);
            trainingDataset.setClassIndex(trainingDataset.numAttributes() - 1);

            // Add synthetic training data (in production, use real historical data)
            addSyntheticTrainingData();

            // Train Random Forest model
            matchingModel = new RandomForest();
            matchingModel.setNumIterations(100); // Number of trees
            matchingModel.setMaxDepth(10);
            matchingModel.buildClassifier(trainingDataset);

            logger.info("✅ Random Forest model trained with {} instances", 
                       trainingDataset.numInstances());

        } catch (Exception e) {
            logger.error("Failed to initialize Random Forest model: {}", e.getMessage());
        }
    }

    /**
     * Create feature vector for donor-patient pair
     */
    private Instance createFeatureVector(Donor donor, Patient patient) {
        Instance instance = new DenseInstance(7); // 6 features + 1 class
        instance.setDataset(trainingDataset);

        // Feature 1: Blood compatibility (0.0 to 1.0)
        instance.setValue(0, calculateBloodCompatibility(donor, patient));
        
        // Feature 2: Age compatibility (0.0 to 1.0)
        instance.setValue(1, calculateAgeCompatibility(donor, patient));
        
        // Feature 3: HLA compatibility (simulated, 0.0 to 1.0)
        instance.setValue(2, calculateHLACompatibility(donor, patient));
        
        // Feature 4: Urgency score (0.0 to 1.0)
        instance.setValue(3, calculateUrgencyScore(patient));
        
        // Feature 5: Distance score (0.0 to 1.0, higher = closer)
        instance.setValue(4, calculateDistanceScore(donor, patient));
        
        // Feature 6: Organ quality (simulated, 0.0 to 1.0)
        instance.setValue(5, calculateOrganQuality(donor));

        return instance;
    }

    /**
     * Add synthetic training data for Random Forest
     * In production, use real historical matching data
     */
    private void addSyntheticTrainingData() {
        // Generate 1000 synthetic training examples
        Random random = new Random(42); // Fixed seed for reproducibility
        
        for (int i = 0; i < 1000; i++) {
            Instance instance = new DenseInstance(7);
            instance.setDataset(trainingDataset);
            
            // Generate random features
            double bloodComp = random.nextDouble();
            double ageComp = random.nextDouble();
            double hlaComp = random.nextDouble();
            double urgency = random.nextDouble();
            double distance = random.nextDouble();
            double quality = random.nextDouble();
            
            instance.setValue(0, bloodComp);
            instance.setValue(1, ageComp);
            instance.setValue(2, hlaComp);
            instance.setValue(3, urgency);
            instance.setValue(4, distance);
            instance.setValue(5, quality);
            
            // Determine class based on feature combination
            double score = (bloodComp * 0.3) + (hlaComp * 0.25) + (ageComp * 0.15) + 
                          (urgency * 0.15) + (distance * 0.1) + (quality * 0.05);
            
            String matchResult = score > 0.6 ? "MATCH" : "NO_MATCH";
            instance.setValue(6, matchResult);
            
            trainingDataset.add(instance);
        }
    }

    /**
     * Calculate blood type compatibility score
     */
    private double calculateBloodCompatibility(Donor donor, Patient patient) {
        String donorBlood = donor.getBloodType();
        String patientBlood = patient.getBloodType();
        
        // Perfect match
        if (donorBlood.equals(patientBlood)) {
            return 1.0;
        }
        
        // Universal donor/recipient rules
        if ("O-".equals(donorBlood)) return 0.9; // Universal donor
        if ("AB+".equals(patientBlood)) return 0.9; // Universal recipient
        
        // ABO compatibility
        if (donorBlood.charAt(0) == patientBlood.charAt(0)) return 0.7;
        
        // Rh compatibility
        if (donorBlood.charAt(1) == patientBlood.charAt(1)) return 0.5;
        
        return 0.2; // Incompatible
    }

    /**
     * Calculate age compatibility score
     */
    private double calculateAgeCompatibility(Donor donor, Patient patient) {
        int ageDiff = Math.abs(donor.getAge() - patient.getAge());
        
        if (ageDiff <= 5) return 1.0;
        if (ageDiff <= 10) return 0.8;
        if (ageDiff <= 20) return 0.6;
        if (ageDiff <= 30) return 0.4;
        return 0.2;
    }

    /**
     * Calculate HLA compatibility (simulated)
     */
    private double calculateHLACompatibility(Donor donor, Patient patient) {
        // In production, this would analyze actual HLA typing
        // For demo, simulate based on names (deterministic)
        int donorHash = donor.getDonorName().hashCode();
        int patientHash = patient.getPatientName().hashCode();
        int similarity = Math.abs(donorHash % 100 - patientHash % 100);
        
        return Math.max(0.0, 1.0 - (similarity / 100.0));
    }

    /**
     * Calculate urgency score for patient
     */
    private double calculateUrgencyScore(Patient patient) {
        return switch (patient.getUrgencyLevel()) {
            case "CRITICAL" -> 1.0;
            case "HIGH" -> 0.8;
            case "MEDIUM" -> 0.6;
            case "LOW" -> 0.4;
            default -> 0.2;
        };
    }

    /**
     * Calculate distance score between hospitals (simulated)
     */
    private double calculateDistanceScore(Donor donor, Patient patient) {
        // In production, use actual GPS coordinates
        // For demo, simulate based on hospital IDs
        long hospitalDistance = Math.abs(donor.getHospitalId() - patient.getHospitalId());
        return Math.max(0.1, 1.0 - (hospitalDistance * 0.1));
    }

    /**
     * Calculate organ quality score
     */
    private double calculateOrganQuality(Donor donor) {
        double qualityScore = 1.0;
        
        // Age factor
        if (donor.getAge() > 60) qualityScore -= 0.2;
        if (donor.getAge() > 70) qualityScore -= 0.3;
        
        // Donor type factor
        if ("DECEASED".equals(donor.getDonorType())) {
            qualityScore -= 0.1;
        }
        
        return Math.max(0.1, qualityScore);
    }

    /**
     * Fallback compatibility calculation if AI fails
     */
    private double calculateFallbackCompatibilityScore(Donor donor, Patient patient) {
        double bloodScore = calculateBloodCompatibility(donor, patient);
        double ageScore = calculateAgeCompatibility(donor, patient);
        double urgencyScore = calculateUrgencyScore(patient);
        
        return (bloodScore * 0.5) + (ageScore * 0.3) + (urgencyScore * 0.2);
    }

    /**
     * Send notifications to hospitals with matches
     */
    private void sendMatchNotifications(List<OrganMatchResult> matches, Patient patient) {
        logger.info("📢 Sending match notifications for {} matches", matches.size());
        
        for (OrganMatchResult match : matches) {
            notificationService.sendMatchNotification(
                    match.getDonor().getHospitalId(),
                    match.getPatient().getHospitalId(),
                    match
            );
        }
    }

    /**
     * Match result data class
     */
    public static class OrganMatchResult {
        private final Donor donor;
        private final Patient patient;
        private final double compatibilityScore;
        private final double distanceScore;
        private final double urgencyScore;
        private final double bloodCompatibility;

        public OrganMatchResult(Donor donor, Patient patient, double compatibilityScore,
                              double distanceScore, double urgencyScore, double bloodCompatibility) {
            this.donor = donor;
            this.patient = patient;
            this.compatibilityScore = compatibilityScore;
            this.distanceScore = distanceScore;
            this.urgencyScore = urgencyScore;
            this.bloodCompatibility = bloodCompatibility;
        }

        // Getters
        public Donor getDonor() { return donor; }
        public Patient getPatient() { return patient; }
        public double getCompatibilityScore() { return compatibilityScore; }
        public double getDistanceScore() { return distanceScore; }
        public double getUrgencyScore() { return urgencyScore; }
        public double getBloodCompatibility() { return bloodCompatibility; }
        
        public String getMatchSummary() {
            return String.format("Match: %.1f%% compatible, %s → %s (%s to %s)",
                    compatibilityScore * 100,
                    donor.getDonorName(),
                    patient.getPatientName(),
                    donor.getHospital().getName(),
                    patient.getHospital().getName());
        }
    }
}
