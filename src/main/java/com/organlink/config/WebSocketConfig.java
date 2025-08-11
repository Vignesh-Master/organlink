package com.organlink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for OrganLink
 * 
 * This configuration enables real-time communication between the frontend and backend
 * for features like live organ matching notifications, status updates, and alerts.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker options
     * 
     * @param registry the MessageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set prefix for messages FROM server TO client
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Set prefix for messages FROM client TO server
        registry.setApplicationDestinationPrefixes("/app");
        
        // Enable user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints
     * 
     * @param registry the StompEndpointRegistry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                    "http://localhost:3000",           // Development
                    "http://localhost:5173",           // Vite default
                    "https://organlink.org",           // Production
                    "https://hospital.organlink.org",  // Hospital portal
                    "https://org.organlink.org",       // Organization portal
                    "https://admin.organlink.org"      // Admin portal
                )
                .withSockJS(); // Fallback options for browsers that don't support WebSocket
    }
}