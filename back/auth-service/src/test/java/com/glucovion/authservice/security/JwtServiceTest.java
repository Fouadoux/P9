package com.glucovion.authservice.security;


import com.glucovion.authservice.entity.AppRole;
import com.glucovion.authservice.entity.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {


    private JwtService jwtService;
    private AppUser user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("01234567890123456789012345678901");
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(AppRole.USER);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnEmail_FromToken() {
        // Arrange
        String token = jwtService.generateToken(user);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals(user.getEmail(), username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        // Arrange
        String token = jwtService.generateToken(user);

        // Act
        boolean isValid = jwtService.isTokenValid(token, user);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForInvalidToken() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1piIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        // Arrange
        String expiredToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // 24 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
                .signWith(jwtService.signingKey, SignatureAlgorithm.HS256)
                .compact();

        // Act & Assert
        assertFalse(jwtService.isTokenValid(expiredToken, user));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForTokenWithWrongSignature() {
        // Arrange
        String wrongSigToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour from now
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256)) // Different key
                .compact();

        // Act & Assert
        assertFalse(jwtService.isTokenValid(wrongSigToken, user));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForMalformedToken() {
        // Arrange
        String malformedToken = "garbage.invalid.token";

        // Act & Assert
        assertFalse(jwtService.isTokenValid(malformedToken, user));
    }
}