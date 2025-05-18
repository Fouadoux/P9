package com.glucovision.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * Utility class for handling JWT token operations such as validation,
 * claims extraction, and decoding the user's identity and role.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret:}") String secretKey) {
        this.SECRET_KEY = secretKey;
    }

    /**
     * Builds the signing key from the configured secret key.
     *
     * @return the HMAC signing key
     */
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Validates a JWT token by attempting to parse its claims.
     *
     * @param token the JWT token string
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses and retrieves the claims from a JWT token.
     *
     * @param token the JWT token
     * @return the {@link Claims} object
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    public Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the subject (typically email or username) from the token.
     *
     * @param token the JWT token
     * @return the subject (email/username)
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extracts the user's role from the JWT claims.
     *
     * @param token the JWT token
     * @return the user's role
     * @throws RuntimeException if the role is missing in the token
     */
    public String extractRole(String token) {
        Claims claims = getClaims(token);
        String role = claims.get("roles", String.class);
        if (role == null) {
            throw new RuntimeException("Missing 'roles' claim in JWT");
        }
        return role;
    }
}
