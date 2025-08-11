package com.organlink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS Configuration for OrganLink API
 * 
 * This configuration allows cross-origin requests from the React frontend
 * to the Spring Boot backend during development and production.
 */
@Configuration
public class CorsConfig {

    /**
     * Configure CORS settings for the application
     * 
     * @return CorsConfigurationSource with appropriate settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow requests from the React frontend
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",           // Development
            "http://localhost:5173",           // Vite default
            "https://organlink.org",           // Production
            "https://hospital.organlink.org",  // Hospital portal
            "https://org.organlink.org",       // Organization portal
            "https://admin.organlink.org"      // Admin portal
        ));
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Expose these headers to the client
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "X-Tenant-ID", "Content-Disposition"
        ));
        
        // Cache preflight requests for 1 hour (3600 seconds)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration);
        
        return source;
    }
    
    /**
     * Create a CORS filter bean
     * 
     * @return CorsFilter with the configured source
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}