package com.organlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling real-time notifications via WebSocket
 */
@Controller
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send a notification to a specific hospital
     * 
     * @param hospitalId The hospital ID to send the notification to
     * @param message The notification message
     * @return Response indicating success
     */
    @GetMapping("/send/{hospitalId}")
    @ResponseBody
    public Map<String, Object> sendNotification(@PathVariable String hospitalId, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "System Notification");
        notification.put("message", message);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("type", "SYSTEM");
        
        // Send to a specific hospital's queue
        messagingTemplate.convertAndSendToUser(
            hospitalId, 
            "/queue/notifications", 
            notification
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Notification sent successfully");
        return response;
    }

    /**
     * Broadcast a notification to all connected clients
     * 
     * @param notification The notification payload
     */
    @MessageMapping("/broadcast")
    public void broadcastNotification(@Payload Map<String, Object> notification) {
        // Add timestamp if not present
        if (!notification.containsKey("timestamp")) {
            notification.put("timestamp", LocalDateTime.now());
        }
        
        // Broadcast to all subscribers of the topic
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Send a notification to a specific user
     * 
     * @param notification The notification payload containing userId
     */
    @MessageMapping("/send")
    public void sendUserNotification(@Payload Map<String, Object> notification) {
        String userId = (String) notification.get("userId");
        
        if (userId != null) {
            // Add timestamp if not present
            if (!notification.containsKey("timestamp")) {
                notification.put("timestamp", LocalDateTime.now());
            }
            
            // Remove userId from the payload
            notification.remove("userId");
            
            // Send to specific user's queue
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                notification
            );
        }
    }
}