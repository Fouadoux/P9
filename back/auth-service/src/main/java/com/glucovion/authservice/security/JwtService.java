package com.glucovion.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Log4j2
@Service
public class JwtService {

    final Key signingKey;

    @Autowired
    public JwtService(@Value("${jwt.secret.key}") String secretKey) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret key cannot be null or empty");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (token == null) {  // Supprimez la vérification du "Bearer "
            return false;
        }

        try {
            final String username = extractUsername(token);
            log.info("username is " + username);
            final boolean isExpired = isTokenExpired(token);

            log.debug("Validating token for {} - username match: {}, expired: {}",
                    userDetails.getUsername(),
                    username.equals(userDetails.getUsername()),
                    isExpired);

            return username.equals(userDetails.getUsername()) && !isExpired;
        } catch (ExpiredJwtException ex) {
            log.debug("Token expiré: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            log.debug("Token malformé: {}", ex.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException | SignatureException ex) {
            log.debug("Erreur de validation JWT: {}", ex.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return buildToken(claims, userDetails.getUsername());
    }

    public String generateTokenWithRole(String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(role));
        return buildToken(claims, "internal-service");
    }

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