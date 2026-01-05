package com.hotelCare.hostelCare.config.JWTConfig;
import com.hotelCare.hostelCare.enums.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
@Configuration
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String ROLE_CLAIM = "role";

    private final JWTTokenGenerationLogic jwtTokenGenerationLogic;

    public JWTAuthenticationFilter(JWTTokenGenerationLogic jwtTokenGenerationLogic) {
        this.jwtTokenGenerationLogic = jwtTokenGenerationLogic;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // If authentication already exists, skip JWT processing
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        extractTokenFromCookies(request)
                .ifPresent(token -> authenticateUser(token, request));

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT from cookies.
     */
    private Optional<String> extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * Authenticates the user using JWT.
     */
    private void authenticateUser(String token, HttpServletRequest request) {
        try {
            Claims claims = jwtTokenGenerationLogic.validateToken(token);

            String username = claims.getSubject();
            String roleValue = claims.get(ROLE_CLAIM, String.class);

            if (username == null || roleValue == null) {
                return;
            }

            UserRole userRole = UserRole.valueOf(roleValue);
            String authority = "ROLE_" + userRole.name();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(authority))
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            logger.warn("JWT token validation failed: {}");
            SecurityContextHolder.clearContext();
        }
    }
}
