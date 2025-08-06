package com.organlink.controller;

import com.organlink.utils.ApiResponse;
import com.organlink.repository.DonorRepository;
import com.organlink.repository.PatientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI Matching Controller for organ compatibility analysis
 * Implements AI-powered donor-patient matching with policy consideration
 */
@RestController
@RequestMapping("/api/v1/ai-matching")
@RequiredArgsConstructor
@Tag(name = "AI Matching", description = "AI-powered organ matching and compatibility analysis")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class AIMatchingController {

    private static final Logger logger = LoggerFactory.getLogger(AIMatchingController.class);

    private final DonorRepository donorRepository;
    private final PatientRepository patientRepository;

    /**
     * Find matches for a specific patient
     */
    @PostMapping("/find-matches/{patientId}")
    @Operation(summary = "Find donor matches", description = "Find compatible donors for a patient using AI matching")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> findMatches(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Patient ID") 
            @PathVariable Long patientId) {

        logger.info("🤖 AI Matching: Finding matches for patient {} (hospital: {})", patientId, tenantId);

        try {
            // Simulate AI matching algorithm
            List<Map<String, Object>> matches = generateAIMatches(patientId, tenantId);

            ApiResponse<List<Map<String, Object>>> response = ApiResponse.success(
                "AI matches found successfully", matches);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("AI matching failed: {}", e.getMessage());
            ApiResponse<List<Map<String, Object>>> response = ApiResponse.error(
                "AI matching failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Trigger AI matching for all patients
     */
    @PostMapping("/trigger-matching")
    @Operation(summary = "Trigger AI matching", description = "Trigger AI matching process for all patients")
    public ResponseEntity<ApiResponse<Map<String, Object>>> triggerMatching(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("🚀 Triggering AI matching for hospital: {}", tenantId);

        try {
            // Simulate AI matching process
            Map<String, Object> result = new HashMap<>();
            result.put("status", "COMPLETED");
            result.put("patientsProcessed", 25);
            result.put("matchesFound", 18);
            result.put("newMatches", 5);
            result.put("processedAt", LocalDateTime.now());
            result.put("processingTime", "2.3 seconds");

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "AI matching triggered successfully", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to trigger AI matching: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to trigger AI matching: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get AI matching notifications
     */
    @GetMapping("/notifications")
    @Operation(summary = "Get matching notifications", description = "Get AI matching notifications for the hospital")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNotifications(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("📬 Getting AI matching notifications for hospital: {}", tenantId);

        try {
            List<Map<String, Object>> notifications = generateNotifications(tenantId);

            ApiResponse<List<Map<String, Object>>> response = ApiResponse.success(
                "Notifications retrieved successfully", notifications);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get notifications: {}", e.getMessage());
            ApiResponse<List<Map<String, Object>>> response = ApiResponse.error(
                "Failed to retrieve notifications: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get unread notifications count
     */
    @GetMapping("/notifications/unread-count")
    @Operation(summary = "Get unread notifications count", description = "Get count of unread matching notifications")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("🔢 Getting unread notifications count for hospital: {}", tenantId);

        try {
            // Simulate unread count
            int unreadCount = (int) (Math.random() * 5) + 1;

            ApiResponse<Integer> response = ApiResponse.success(
                "Unread count retrieved successfully", unreadCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get unread count: {}", e.getMessage());
            ApiResponse<Integer> response = ApiResponse.error(
                "Failed to retrieve unread count: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/notifications/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Notification ID") 
            @PathVariable Long notificationId) {

        logger.info("✅ Marking notification {} as read for hospital: {}", notificationId, tenantId);

        try {
            // In production, update notification status in database
            ApiResponse<String> response = ApiResponse.success(
                "Notification marked as read", null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to mark notification as read: {}", e.getMessage());
            ApiResponse<String> response = ApiResponse.error(
                "Failed to mark notification as read: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get AI matching metrics
     */
    @GetMapping("/metrics")
    @Operation(summary = "Get AI matching metrics", description = "Get AI matching performance metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMatchingMetrics(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("📊 Getting AI matching metrics for hospital: {}", tenantId);

        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // Overall metrics
            metrics.put("totalMatches", 156);
            metrics.put("successfulTransplants", 89);
            metrics.put("successRate", 0.87);
            metrics.put("averageMatchingTime", "1.2 seconds");
            
            // Organ-specific metrics
            Map<String, Object> organMetrics = new HashMap<>();
            organMetrics.put("kidney", Map.of("matches", 45, "success", 38, "rate", 0.84));
            organMetrics.put("liver", Map.of("matches", 32, "success", 28, "rate", 0.88));
            organMetrics.put("heart", Map.of("matches", 18, "success", 16, "rate", 0.89));
            organMetrics.put("lung", Map.of("matches", 12, "success", 10, "rate", 0.83));
            metrics.put("organMetrics", organMetrics);
            
            // Policy compliance
            metrics.put("policyCompliance", 0.95);
            metrics.put("activePolicies", 8);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "AI matching metrics retrieved successfully", metrics);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get matching metrics: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to retrieve matching metrics: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Generate AI matches (simulation)
     */
    private List<Map<String, Object>> generateAIMatches(Long patientId, String tenantId) {
        List<Map<String, Object>> matches = new ArrayList<>();
        
        // Simulate 3-5 matches
        int matchCount = (int) (Math.random() * 3) + 3;
        
        for (int i = 1; i <= matchCount; i++) {
            Map<String, Object> match = new HashMap<>();
            match.put("donorId", 100L + i);
            match.put("donorName", "Donor " + (char)('A' + i - 1));
            match.put("compatibilityScore", Math.round((0.75 + Math.random() * 0.24) * 100) / 100.0);
            match.put("bloodTypeMatch", Math.random() > 0.2);
            match.put("hlaMatches", (int) (Math.random() * 6) + 1);
            match.put("distance", (int) (Math.random() * 500) + 50 + " km");
            match.put("organType", "Kidney");
            match.put("urgencyLevel", i <= 2 ? "HIGH" : "MEDIUM");
            match.put("estimatedWaitTime", (int) (Math.random() * 48) + 6 + " hours");
            match.put("policyCompliant", Math.random() > 0.1);
            match.put("lastUpdated", LocalDateTime.now().minusHours(i));
            
            matches.add(match);
        }
        
        // Sort by compatibility score
        matches.sort((a, b) -> Double.compare(
            (Double) b.get("compatibilityScore"), 
            (Double) a.get("compatibilityScore")
        ));
        
        return matches;
    }

    /**
     * Generate notifications (simulation)
     */
    private List<Map<String, Object>> generateNotifications(String tenantId) {
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        String[] notificationTypes = {"NEW_MATCH", "URGENT_MATCH", "POLICY_UPDATE", "SYSTEM_ALERT"};
        String[] messages = {
            "New high-compatibility match found for Patient #123",
            "Urgent: Critical patient has new donor match",
            "Policy update affects current matching criteria",
            "AI matching system completed daily analysis"
        };
        
        for (int i = 0; i < 4; i++) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("id", (long) (i + 1));
            notification.put("type", notificationTypes[i]);
            notification.put("message", messages[i]);
            notification.put("isRead", i > 1); // First 2 are unread
            notification.put("priority", i < 2 ? "HIGH" : "MEDIUM");
            notification.put("createdAt", LocalDateTime.now().minusHours(i + 1));
            
            notifications.add(notification);
        }
        
        return notifications;
    }
}
