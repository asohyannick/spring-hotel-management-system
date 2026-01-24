package com.hotelCare.hostelCare.config.securityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelCare.hostelCare.config.JWTConfig.JWTAuthenticationFilter;
import com.hotelCare.hostelCare.enums.UserRole;
import com.hotelCare.hostelCare.exception.ExceptionResponse;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
@Configuration
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Value("${api.version}")
    private String apiVersion;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            ExceptionResponse error = new ExceptionResponse(
                    LocalDateTime.now(),
                    "Unauthorized",
                    "Authentication is required to access this resource",
                    HttpStatus.UNAUTHORIZED.value(),
                    "UNAUTHORIZED",
                    request.getRequestURI(),
                    request.getMethod()
            );

            response.getWriter().write(objectMapper.writeValueAsString(error));
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            ExceptionResponse error = new ExceptionResponse(
                    LocalDateTime.now(),
                    "Access denied",
                    "You do not have permission to access this resource",
                    HttpStatus.FORBIDDEN.value(),
                    "FORBIDDEN",
                    request.getRequestURI(),
                    request.getMethod()
            );

            response.getWriter().write(objectMapper.writeValueAsString(error));
        };
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String apiBasePath = "/api/" + apiVersion;
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                apiBasePath + "/auth/register",
                                apiBasePath + "/auth/login",
                                apiBasePath + "/auth/logout",
                                apiBasePath + "/auth/verify-otp",
                                apiBasePath + "/auth/admin-login",
                                apiBasePath + "/auth/resend-otp",
                                apiBasePath + "/auth/forgot-password",
                                apiBasePath + "/auth/*/reset-password",
                                apiBasePath + "/auth/google-login",
                                apiBasePath + "/bookings/search-bookings"
                        ).permitAll()
                        .requestMatchers(
                                apiBasePath + "/bookings/create-booking",
                                apiBasePath + "/bookings/all-bookings",
                                apiBasePath + "/bookings/fetch-booking/*",
                                apiBasePath + "/bookings/update-booking/*",
                                apiBasePath + "/bookings/delete-booking/*"
                        ).hasAnyRole(
                                UserRole.CUSTOMER.name(),
                                UserRole.SUPER_ADMIN.name()
                        )
                        .requestMatchers(
                                apiBasePath + "/bookings/approve-booking/*",
                                apiBasePath + "/bookings/reject-booking/*",
                                apiBasePath + "/bookings/fetch-approved-bookings",
                                apiBasePath + "/bookings/fetch-rejected-bookings",
                                apiBasePath + "/bookings/total-bookings"
                                ).hasRole(
                                UserRole.SUPER_ADMIN.name()
                        )
                        .requestMatchers(
                                apiBasePath + "/auth/all-users",
                                apiBasePath + "/auth/fetch-user/*",
                                apiBasePath + "/auth/delete-user/*",
                                apiBasePath + "/auth/block-user/*",
                                apiBasePath + "/auth/unblock-user/*",
                                apiBasePath + "/auth/count-users"
                        ).hasAnyRole(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name())
                        .requestMatchers(
                                apiBasePath + "/profile/create-profile/*",
                                apiBasePath + "/profile/fetch-profile/*",
                                apiBasePath + "/profile/update-profile/*"

                        ).hasAnyRole(
                                UserRole.CUSTOMER.name(),
                                UserRole.SUPER_ADMIN.name()
                        )

                        .requestMatchers(
                                apiBasePath + "/profile/fetch-profiles",
                                apiBasePath + "/profile/total-profiles",
                                apiBasePath + "/profile/fetch-profile-byUserId/*",
                                apiBasePath + "/profile/delete-profile/*",
                                apiBasePath + "/profile/total-profiles"

                        )
                        .hasRole(UserRole.SUPER_ADMIN.name())

                        .requestMatchers(
                               apiBasePath +  "/employee/add-employee",
                                apiBasePath + "/employee/fetch-employees",
                                apiBasePath + "/employee/fetch-employee/*",
                                apiBasePath + "/employee/update-employee/*",
                                apiBasePath + "/employee/delete-employee/*",
                                apiBasePath + "/employee/total-employees",
                                apiBasePath + "/employee/search-employee"
                        ).hasRole(
                                UserRole.SUPER_ADMIN.name()
                        )

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().denyAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(accessDeniedHandler())
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

