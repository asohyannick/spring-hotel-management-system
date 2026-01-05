package com.hotelCare.hostelCare.config.JWTConfig;
import com.hotelCare.hostelCare.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
/**
 * Handles JWT token generation, validation, and extraction for the application.
 */
@Configuration
public class JWTTokenGenerationLogic {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /**
     * Returns the SecretKey used for signing JWTs.
     */
    public SecretKey getJwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an access token containing the user's email and role.
     */
    public String generateAccessToken(String email, UserRole role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(new Date(now)).expiration(new Date(now + accessTokenExpirationMs))
                .signWith(getJwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a refresh token containing only the user's email.
     */
    public String generateRefreshToken(String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now)).expiration(new Date(now + refreshTokenExpirationMs))
                .signWith(getJwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT and returns its claims.
     */
    public Claims validateToken(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(getJwtSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the username (email) from the token.
     */
    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    /**
     * Extracts a custom claim (e.g., role) from the token.
     */
    public String extractRole(String token) {
        return (String) validateToken(token).get("role");
    }

    /**
     * Checks if a token is expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = validateToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
