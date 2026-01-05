package com.hotelCare.hostelCare.config.securityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelCare.hostelCare.config.JWTConfig.JWTAuthenticationFilter;
import com.hotelCare.hostelCare.exception.ExceptionResponse;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.time.LocalDateTime;
@Configuration
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Value("${api.version}")
    private String apiVersion;

    // Password encoder for storing hashed passwords
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager for authentication logic
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");

            ExceptionResponse error = new ExceptionResponse(
                    LocalDateTime.now(),
                    "Access denied",
                    "You do not have permission to access this resource",
                    HttpStatus.FORBIDDEN.value(),
                    "FORBIDDEN",
                    request.getRequestURI(),
                    request.getMethod()
            );

            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(error)
            );
        };
    }

    // Main security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String apiBasePath =  "/api/" + apiVersion;
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint security
                .authorizeHttpRequests(auth -> auth
                        // 🔥 ALLOW EVERYTHING UNDER /api
                        .requestMatchers("/api/**").permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        // Public endpoints
                        .requestMatchers(
                                apiBasePath + "/auth/**"
                        ).permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Swagger / OpenAPI (optional)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()


                        // Everything else requires authentication
                        .anyRequest().denyAll()
                )

               .exceptionHandling(ex -> ex
                   .accessDeniedHandler(accessDeniedHandler())
               );
        // Add JWT authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

