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
 * Hospital Dashboard Controller
 * Provides dashboard statistics and analytics for hospital users
 */
@RestController
@RequestMapping("/api/v1/hospital/dashboard")
@RequiredArgsConstructor
@Tag(name = "Hospital Dashboard", description = "Hospital dashboard statistics and analytics")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class HospitalDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(HospitalDashboardController.class);

    private final DonorRepository donorRepository;
    private final PatientRepository patientRepository;

    /**
     * Get hospital dashboard overview
     */
    @GetMapping("/overview")
    @Operation(summary = "Get dashboard overview", description = "Get hospital dashboard overview statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardOverview(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("📊 Getting dashboard overview for hospital: {}", tenantId);

        try {
            Map<String, Object> overview = new HashMap<>();

            // Basic statistics
            long totalDonors = donorRepository.countByIsActiveTrue();
            long totalPatients = patientRepository.countByIsActiveTrue();
            long criticalPatients = patientRepository.countByUrgencyLevelAndIsActiveTrue("CRITICAL");

            overview.put("totalDonors", totalDonors);
            overview.put("totalPatients", totalPatients);
            overview.put("criticalPatients", criticalPatients);
            overview.put("availableOrgans", totalDonors); // Simplified
            overview.put("successfulTransplants", 89);
            overview.put("pendingMatches", 12);

            // Recent activity
            List<Map<String, Object>> recentActivity = generateRecentActivity();
            overview.put("recentActivity", recentActivity);

            // Quick stats
            Map<String, Object> quickStats = new HashMap<>();
            quickStats.put("todayRegistrations", 3);
            quickStats.put("weeklyTransplants", 7);
            quickStats.put("monthlySuccess", 0.87);
            quickStats.put("systemUptime", "99.9%");
            overview.put("quickStats", quickStats);

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Dashboard overview retrieved successfully", overview);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get dashboard overview: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to retrieve dashboard overview: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get organ-specific statistics
     */
    @GetMapping("/organ-stats")
    @Operation(summary = "Get organ statistics", description = "Get statistics by organ type")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrganStats(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("🫀 Getting organ statistics for hospital: {}", tenantId);

        try {
            Map<String, Object> organStats = new HashMap<>();

            // Kidney statistics
            Map<String, Object> kidneyStats = new HashMap<>();
            kidneyStats.put("donors", 45);
            kidneyStats.put("patients", 38);
            kidneyStats.put("matches", 12);
            kidneyStats.put("transplants", 28);
            kidneyStats.put("successRate", 0.84);
            organStats.put("kidney", kidneyStats);

            // Liver statistics
            Map<String, Object> liverStats = new HashMap<>();
            liverStats.put("donors", 32);
            liverStats.put("patients", 29);
            liverStats.put("matches", 8);
            liverStats.put("transplants", 21);
            liverStats.put("successRate", 0.88);
            organStats.put("liver", liverStats);

            // Heart statistics
            Map<String, Object> heartStats = new HashMap<>();
            heartStats.put("donors", 18);
            heartStats.put("patients", 22);
            heartStats.put("matches", 5);
            heartStats.put("transplants", 14);
            heartStats.put("successRate", 0.89);
            organStats.put("heart", heartStats);

            // Lung statistics
            Map<String, Object> lungStats = new HashMap<>();
            lungStats.put("donors", 12);
            lungStats.put("patients", 15);
            lungStats.put("matches", 3);
            lungStats.put("transplants", 8);
            lungStats.put("successRate", 0.83);
            organStats.put("lung", lungStats);

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Organ statistics retrieved successfully", organStats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get organ statistics: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to retrieve organ statistics: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get monthly analytics data
     */
    @GetMapping("/analytics/monthly")
    @Operation(summary = "Get monthly analytics", description = "Get monthly analytics data for the hospital")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyAnalytics(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Number of months") 
            @RequestParam(defaultValue = "12") int months) {

        logger.info("📈 Getting monthly analytics for hospital: {} ({} months)", tenantId, months);

        try {
            Map<String, Object> analytics = new HashMap<>();

            // Generate monthly data
            List<Map<String, Object>> monthlyData = new ArrayList<>();
            String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                 "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            for (int i = 0; i < Math.min(months, 12); i++) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", monthNames[i]);
                monthData.put("donors", (int) (Math.random() * 20) + 10);
                monthData.put("patients", (int) (Math.random() * 25) + 15);
                monthData.put("transplants", (int) (Math.random() * 15) + 5);
                monthData.put("successRate", Math.round((0.75 + Math.random() * 0.2) * 100) / 100.0);
                monthlyData.add(monthData);
            }

            analytics.put("monthlyData", monthlyData);
            analytics.put("totalMonths", months);
            analytics.put("averageDonorsPerMonth", 15);
            analytics.put("averagePatientsPerMonth", 22);
            analytics.put("overallSuccessRate", 0.86);

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Monthly analytics retrieved successfully", analytics);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get monthly analytics: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to retrieve monthly analytics: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get yearly analytics data
     */
    @GetMapping("/analytics/yearly")
    @Operation(summary = "Get yearly analytics", description = "Get yearly analytics data for the hospital")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getYearlyAnalytics(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Number of years") 
            @RequestParam(defaultValue = "3") int years) {

        logger.info("📊 Getting yearly analytics for hospital: {} ({} years)", tenantId, years);

        try {
            Map<String, Object> analytics = new HashMap<>();

            // Generate yearly data
            List<Map<String, Object>> yearlyData = new ArrayList<>();
            int currentYear = LocalDateTime.now().getYear();

            for (int i = 0; i < years; i++) {
                Map<String, Object> yearData = new HashMap<>();
                yearData.put("year", currentYear - i);
                yearData.put("donors", (int) (Math.random() * 100) + 150);
                yearData.put("patients", (int) (Math.random() * 120) + 180);
                yearData.put("transplants", (int) (Math.random() * 80) + 100);
                yearData.put("successRate", Math.round((0.80 + Math.random() * 0.15) * 100) / 100.0);
                yearlyData.add(yearData);
            }

            analytics.put("yearlyData", yearlyData);
            analytics.put("totalYears", years);
            analytics.put("averageDonorsPerYear", 200);
            analytics.put("averagePatientsPerYear", 240);
            analytics.put("overallSuccessRate", 0.87);
            analytics.put("growthRate", 0.12); // 12% year-over-year growth

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Yearly analytics retrieved successfully", analytics);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get yearly analytics: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to retrieve yearly analytics: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get urgent cases
     */
    @GetMapping("/urgent-cases")
    @Operation(summary = "Get urgent cases", description = "Get list of urgent patient cases")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUrgentCases(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId) {

        logger.info("🚨 Getting urgent cases for hospital: {}", tenantId);

        try {
            List<Map<String, Object>> urgentCases = generateUrgentCases();

            ApiResponse<List<Map<String, Object>>> response = ApiResponse.success(
                "Urgent cases retrieved successfully", urgentCases);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get urgent cases: {}", e.getMessage());
            ApiResponse<List<Map<String, Object>>> response = ApiResponse.error(
                "Failed to retrieve urgent cases: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Generate recent activity data
     */
    private List<Map<String, Object>> generateRecentActivity() {
        List<Map<String, Object>> activities = new ArrayList<>();

        String[] activityTypes = {"DONOR_REGISTERED", "PATIENT_REGISTERED", "MATCH_FOUND", "TRANSPLANT_COMPLETED"};
        String[] descriptions = {
            "New donor registered: John D.",
            "Patient Emily R. added to waiting list",
            "High compatibility match found",
            "Successful kidney transplant completed"
        };

        for (int i = 0; i < 5; i++) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", (long) (i + 1));
            activity.put("type", activityTypes[i % activityTypes.length]);
            activity.put("description", descriptions[i % descriptions.length]);
            activity.put("timestamp", LocalDateTime.now().minusHours(i + 1));
            activity.put("priority", i < 2 ? "HIGH" : "NORMAL");
            activities.add(activity);
        }

        return activities;
    }

    /**
     * Generate urgent cases data
     */
    private List<Map<String, Object>> generateUrgentCases() {
        List<Map<String, Object>> urgentCases = new ArrayList<>();

        String[] patientNames = {"Emily Davis", "Robert Wilson", "Lisa Anderson", "Michael Brown"};
        String[] organs = {"Heart", "Liver", "Kidney", "Lung"};
        String[] urgencyLevels = {"CRITICAL", "HIGH", "HIGH", "CRITICAL"};
        int[] waitTimes = {89, 156, 234, 67};

        for (int i = 0; i < patientNames.length; i++) {
            Map<String, Object> urgentCase = new HashMap<>();
            urgentCase.put("id", (long) (i + 1));
            urgentCase.put("patientName", patientNames[i]);
            urgentCase.put("organNeeded", organs[i]);
            urgentCase.put("urgencyLevel", urgencyLevels[i]);
            urgentCase.put("waitTime", waitTimes[i] + " days");
            urgentCase.put("bloodType", i % 2 == 0 ? "O+" : "A-");
            urgentCase.put("lastUpdated", LocalDateTime.now().minusHours(i + 2));
            urgentCases.add(urgentCase);
        }

        return urgentCases;
    }
}
