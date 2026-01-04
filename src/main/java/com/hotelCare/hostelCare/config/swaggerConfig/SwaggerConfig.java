package com.hotelCare.hostelCare.config.swaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addTagsItem(new Tag()
                        .name("Authentication & User Management Endpoints")
                        .description("All login, signup, verification & token-related endpoints ❤️"))
                .info(new Info()
                        .title("HostelCare API Documentation")
                        .version("1.0.0")
                        .description("API documentation for the HostelCare application")
                        .contact(new Contact()
                                .name("Aso Yannick")
                                .email("keepcoding@gmail.com")
                                .url("https://www.linkedin.com/in/asohyannick/"))
                        .license(new License()
                                .name("Love License ❤️")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}
