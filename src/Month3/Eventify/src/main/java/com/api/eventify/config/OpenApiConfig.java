package com.api.eventify.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventifyOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Eventify Support");
        contact.setEmail("support@eventify.com");

        License license = new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
            .title("Eventify API")
            .version("2.0.0")
            .description(
                "Secure Event Management System REST API - " +
                    "Manage events and participants with user authentication and authorization. " +
                    "Users can only access their own events and participants."
            )
            .contact(contact)
            .license(license);

        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .name("Bearer Authentication")
            .description(
                "Enter JWT Bearer token in the format: Bearer {token}"
            );

        Components components = new Components().addSecuritySchemes(
            "Bearer Authentication",
            securityScheme
        );

        return new OpenAPI()
            .info(info)
            .servers(List.of(localServer))
            .components(components);
    }
}
