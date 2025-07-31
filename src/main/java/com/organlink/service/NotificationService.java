package com.organlink.service;

import com.organlink.service.AIOrganMatchingService.OrganMatchResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Notification Service for Cross-Hospital Match Alerts
 * 
 * YOUR DEMO SCENARIO:
 * 1. Chennai registers liver donor
 * 2. Delhi registers kidney patient
 * 3. AI finds matches
 * 4. Notifications sent to both hospitals
 * 5. Real-time alerts in dashboard
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // In-memory notification storage (in production, use database + WebSocket)
    private final Map<Long, List<MatchNotification>> hospitalNotifications = new ConcurrentHashMap<>();

    /**
     * Send match notification to hospitals
     */
    public void sendMatchNotification(Long donorHospitalId, Long patientHospitalId, 
                                    OrganMatchResult match) {
        
        logger.info("📢 Sending match notification: {} → {}", 
                   donorHospitalId, patientHospitalId);

        // Create notification for donor hospital
        MatchNotification donorNotification = new MatchNotification(
                "MATCH_FOUND_DONOR",
                "Match Found for Your Donor",
                String.format("Your donor %s has been matched with patient %s at %s hospital. " +
                             "Compatibility score: %.1f%%",
                             match.getDonor().getDonorName(),
                             match.getPatient().getPatientName(),
                             getHospitalName(patientHospitalId),
                             match.getCompatibilityScore() * 100),
                match,
                LocalDateTime.now(),
                false
        );

        // Create notification for patient hospital
        MatchNotification patientNotification = new MatchNotification(
                "MATCH_FOUND_PATIENT",
                "Match Found for Your Patient",
                String.format("A compatible donor %s has been found at %s hospital for your patient %s. " +
                             "Compatibility score: %.1f%%. Contact them immediately!",
                             match.getDonor().getDonorName(),
                             getHospitalName(donorHospitalId),
                             match.getPatient().getPatientName(),
                             match.getCompatibilityScore() * 100),
                match,
                LocalDateTime.now(),
                false
        );

        // Store notifications
        hospitalNotifications.computeIfAbsent(donorHospitalId, k -> new ArrayList<>())
                           .add(donorNotification);
        
        hospitalNotifications.computeIfAbsent(patientHospitalId, k -> new ArrayList<>())
                           .add(patientNotification);

        // In production, send real-time notifications via WebSocket
        logger.info("✅ Match notifications sent to hospitals {} and {}", 
                   donorHospitalId, patientHospitalId);
    }

    /**
     * Get notifications for hospital (for dashboard)
     */
    public List<MatchNotification> getHospitalNotifications(Long hospitalId) {
        List<MatchNotification> notifications = hospitalNotifications.getOrDefault(hospitalId, new ArrayList<>());
        
        // Sort by timestamp (newest first)
        notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        logger.info("Retrieved {} notifications for hospital {}", notifications.size(), hospitalId);
        return notifications;
    }

    /**
     * Get unread notifications count
     */
    public long getUnreadNotificationsCount(Long hospitalId) {
        List<MatchNotification> notifications = hospitalNotifications.getOrDefault(hospitalId, new ArrayList<>());
        return notifications.stream().filter(n -> !n.isRead()).count();
    }

    /**
     * Mark notification as read
     */
    public void markNotificationAsRead(Long hospitalId, String notificationId) {
        List<MatchNotification> notifications = hospitalNotifications.get(hospitalId);
        if (notifications != null) {
            notifications.stream()
                    .filter(n -> n.getId().equals(notificationId))
                    .findFirst()
                    .ifPresent(n -> n.setRead(true));
        }
    }

    /**
     * Mark all notifications as read for hospital
     */
    public void markAllNotificationsAsRead(Long hospitalId) {
        List<MatchNotification> notifications = hospitalNotifications.get(hospitalId);
        if (notifications != null) {
            notifications.forEach(n -> n.setRead(true));
        }
        logger.info("Marked all notifications as read for hospital {}", hospitalId);
    }

    /**
     * Send urgent notification for critical patients
     */
    public void sendUrgentNotification(Long hospitalId, String message) {
        MatchNotification urgentNotification = new MatchNotification(
                "URGENT_ALERT",
                "🚨 URGENT: Critical Patient Alert",
                message,
                null,
                LocalDateTime.now(),
                false
        );

        hospitalNotifications.computeIfAbsent(hospitalId, k -> new ArrayList<>())
                           .add(urgentNotification);

        logger.warn("🚨 URGENT notification sent to hospital {}: {}", hospitalId, message);
    }

    /**
     * Send policy update notification
     */
    public void sendPolicyUpdateNotification(String organType, String policyTitle, double votePercentage) {
        String message = String.format("Policy Update: '%s' for %s organs now has %.1f%% votes. %s",
                policyTitle, organType, votePercentage,
                votePercentage > 50 ? "This policy is now ACTIVE and will affect matching." : 
                                    "This policy is INACTIVE (needs >50% votes).");

        // Send to all hospitals
        for (Long hospitalId : Arrays.asList(1L, 2L)) { // Chennai and Mumbai
            MatchNotification policyNotification = new MatchNotification(
                    "POLICY_UPDATE",
                    "📋 Policy Update: " + organType.toUpperCase(),
                    message,
                    null,
                    LocalDateTime.now(),
                    false
            );

            hospitalNotifications.computeIfAbsent(hospitalId, k -> new ArrayList<>())
                               .add(policyNotification);
        }

        logger.info("📋 Policy update notification sent: {} ({}%)", policyTitle, votePercentage);
    }

    /**
     * Get hospital name by ID (simulated)
     */
    private String getHospitalName(Long hospitalId) {
        return switch (hospitalId.intValue()) {
            case 1 -> "Apollo Hospital Mumbai";
            case 2 -> "Apollo Hospital Chennai";
            default -> "Unknown Hospital";
        };
    }

    /**
     * Clear old notifications (cleanup job)
     */
    public void clearOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30); // Keep 30 days
        
        hospitalNotifications.values().forEach(notifications -> 
            notifications.removeIf(n -> n.getTimestamp().isBefore(cutoff))
        );
        
        logger.info("🧹 Cleared notifications older than 30 days");
    }

    /**
     * Notification data class
     */
    public static class MatchNotification {
        private final String id;
        private final String type;
        private final String title;
        private final String message;
        private final OrganMatchResult matchResult;
        private final LocalDateTime timestamp;
        private boolean read;

        public MatchNotification(String type, String title, String message, 
                               OrganMatchResult matchResult, LocalDateTime timestamp, boolean read) {
            this.id = UUID.randomUUID().toString();
            this.type = type;
            this.title = title;
            this.message = message;
            this.matchResult = matchResult;
            this.timestamp = timestamp;
            this.read = read;
        }

        // Getters and setters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public OrganMatchResult getMatchResult() { return matchResult; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }

        public String getTimeAgo() {
            // Simple time ago calculation
            long minutes = java.time.Duration.between(timestamp, LocalDateTime.now()).toMinutes();
            if (minutes < 1) return "Just now";
            if (minutes < 60) return minutes + " minutes ago";
            if (minutes < 1440) return (minutes / 60) + " hours ago";
            return (minutes / 1440) + " days ago";
        }

        public String getPriorityIcon() {
            return switch (type) {
                case "URGENT_ALERT" -> "🚨";
                case "MATCH_FOUND_DONOR", "MATCH_FOUND_PATIENT" -> "🎯";
                case "POLICY_UPDATE" -> "📋";
                default -> "📢";
            };
        }
    }
}
