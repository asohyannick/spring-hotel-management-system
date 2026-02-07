package com.hotelCare.hostelCare.config.JavaMailSenderConfig;
import com.hotelCare.hostelCare.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class ElasticEmailConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public String validateElasticEmailConfig(
            @Value("${elastic.api-key}") String apiKey,
            @Value("${elastic.from-email}") String fromEmail,
            @Value("${elastic.from-name}") String fromName
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException("Elastic Email API key is missing");
        }
        if (fromEmail == null || fromEmail.isBlank()) {
            throw new BadRequestException("Elastic Email from-email is missing");
        }
        if (fromName == null || fromName.isBlank()) {
            throw new BadRequestException("Elastic Email from-name is missing");
        }
        return "Elastic Email configuration validated";
    }
}