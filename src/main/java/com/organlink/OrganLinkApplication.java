package com.organlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for OrganLink - Organ Donation Management System
 * 
 * This application provides a comprehensive platform for hospitals to manage
 * organ donors, recipients, and matching processes with AI-powered algorithms.
 * 
 * Features:
 * - Multi-tenant hospital management
 * - Donor and recipient registration
 * - AI-based organ matching
 * - Cross-hospital coordination
 * - Medical compliance and audit logging
 * 
 * @author OrganLink Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class OrganLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganLinkApplication.class, args);
        
        System.out.println("""
            
            🏥 ===================================================
            🚀 OrganLink - Organ Donation Management System
            🏥 ===================================================
            
            ✅ Application Started Successfully!
            
            📍 Server: http://localhost:8080
            📋 API Base: http://localhost:8080/api/v1
            📚 Swagger UI: http://localhost:8080/swagger-ui.html
            🔍 Health Check: http://localhost:8080/api/v1/actuator/health
            
            🏥 Multi-tenant Hospital Management System
            🫀 AI-Powered Organ Matching
            👥 Cross-Hospital Coordination
            📊 Medical Compliance & Audit Logging
            
            🏥 ===================================================
            """);
    }
}
