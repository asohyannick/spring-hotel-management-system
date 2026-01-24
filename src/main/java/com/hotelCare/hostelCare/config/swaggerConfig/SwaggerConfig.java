package com.hotelCare.hostelCare.config.swaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI hostelCareOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local environment")
                ))
                .addTagsItem(new Tag()
                        .name("Authentication & User Management")
                        .description("Endpoints for registration, login, verification, password reset, and token management."))
                .addTagsItem(new Tag()
                        .name("Bookings Management Endpoints")
                        .description("Endpoints for managing hostels, rooms, tenants, and related operations."))
                .addTagsItem(new Tag()
                        .name("Customer Profile Management Endpoints")
                        .description("This section is responsible to managed the endpoints belonging to customer profile.")
                )
                .addTagsItem(new Tag()
                        .name("Employee Management Endpoints")
                        .description("This section is responsible to managed the endpoints belonging  to employee management.")
                )
                .info(new Info()
                        .title("HostelCare Backend API Documentation")
                        .version("1.0.0")
                        .description("""
                                Welcome to the HostelCare REST API documentation.

                                This API provides endpoints for authentication, user management, and hostel operations.
                                
                                Designed and developed by Asoh Yannick — Backend Java Developer with a passion for
                                Spring Boot, Spring Cloud, Spring Security, Spring Data JPA, and Spring AI.
                                """)
                        .termsOfService("https://example.com/terms")

                        .contact(new Contact()
                                .name("Asoh Yannick")
                                .email("keepcoding200@gmail.com")
                                .url("https://www.linkedin.com/in/asohyannick/"))

                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}

