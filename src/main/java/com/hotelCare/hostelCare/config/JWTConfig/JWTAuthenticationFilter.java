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

        String token = null;

        // Extract JWT from "accessToken" cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> jwtCookie =
                    Arrays.stream(cookies)
                            .filter(c -> "accessToken".equals(c.getName()))
                            .findFirst();

            if (jwtCookie.isPresent()) {
                token = jwtCookie.get().getValue();
            }
        }

        if (token != null) {
            try {
                Claims claims = jwtTokenGenerationLogic.validateToken(token);
                String username = claims.getSubject();
                String roleStr = claims.get("role", String.class); // e.g. "ADMIN", "DEVOPS"
                System.out.println("Cookies: " + Arrays.toString(request.getCookies()));
                System.out.println("Token found? " + (token != null));
                System.out.println("Cookies: " + Arrays.toString(request.getCookies()));
                System.out.println("Token found? " + (token != null));


                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserRole userRole;
                    try {
                        userRole = UserRole.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        userRole = UserRole.CUSTOMER;
                    }
                    String authority = "ROLE_" + userRole.name();
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority(authority))
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("Username is null or context already has authentication");
                }
            } catch (Exception e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
