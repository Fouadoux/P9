package com.glucovision.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret.key}")
    private String secretKey;

    private String validToken;

    @BeforeEach
    void setUp() {
        // Génère un token valide avec la même clé que celle configurée
        validToken = Jwts.builder()
                .setSubject("user@example.com")
                .claim("role", "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    @Test
    void testValidToken() {
        assertTrue(jwtUtil.isTokenValid(validToken));
        assertEquals("user@example.com", jwtUtil.extractUsername(validToken));
        assertEquals("ROLE_USER", jwtUtil.extractRole(validToken));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("token-invalide"));
    }
}