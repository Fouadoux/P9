package com.glucovion.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for generating, parsing, and validating JWT tokens.
 * <p>
 * Uses a symmetric signing key (HMAC) loaded from application properties.
 * </p>
 */
@Log4j2
@Service
public class JwtService {

    /** Key used to sign and validate JWTs */
    final Key signingKey;

    /**
     * Constructs the JwtService with a secret key from configuration.
     *
     * @param secretKey the secret key from application properties
     */
    @Autowired
    public JwtService(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret key cannot be null or empty");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Extracts the username (subject) from a token.
     *
     * @param token the JWT token
     * @return the subject (email)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the token.
     *
     * @param token          the JWT token
     * @param claimsResolver the function to extract the claim
     * @param <T>            type of the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and validates the token, returning all claims.
     *
     * @param token the JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates a token against a given user's details.
     *
     * @param token        the JWT token
     * @param userDetails  the user to compare with
     * @return true if the token is valid and matches the user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (token == null) {
            return false;
        }

        try {
            final String username = extractUsername(token);
            final boolean isExpired = isTokenExpired(token);

            log.debug("Validating token for {} - username match: {}, expired: {}",
                    userDetails.getUsername(),
                    username.equals(userDetails.getUsername()),
                    isExpired);

            return username.equals(userDetails.getUsername()) && !isExpired;
        } catch (ExpiredJwtException ex) {
            log.debug("Expired token: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            log.debug("Malformed token: {}", ex.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException | SignatureException ex) {
            log.debug("JWT validation error: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Checks if a token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a token.
     *
     * @param token the JWT token
     * @return the expiration {@link Date}
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generates a token for a given user, using their role.
     *
     * @param userDetails the authenticated user
     * @return a JWT token valid for 24 hours
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return buildToken(claims, userDetails.getUsername());
    }

    /**
     * Generates a token with a specific role (used for internal service authentication).
     *
     * @param role the role to include
     * @return a JWT token
     */
    public String generateTokenWithRole(String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(role));
        return buildToken(claims, "internal-service");
    }

    /**
     * Builds a token with given claims and subject.
     *
     * @param claims  the payload data
     * @param subject the token subject (e.g. email or service name)
     * @return a signed JWT token
     */
    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
