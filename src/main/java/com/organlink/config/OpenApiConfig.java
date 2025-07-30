package com.organlink.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for API documentation
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI organLinkOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort + "/api/v1");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setEmail("support@organlink.com");
        contact.setName("OrganLink Support Team");
        contact.setUrl("https://www.organlink.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("OrganLink API")
                .version("1.0.0")
                .contact(contact)
                .description("Comprehensive API for OrganLink - Organ Donation Management System. " +
                           "This API provides endpoints for managing hospitals, donors, recipients, " +
                           "and AI-powered organ matching in a multi-tenant environment.")
                .termsOfService("https://www.organlink.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
