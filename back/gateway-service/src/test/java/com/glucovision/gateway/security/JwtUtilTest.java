package com.glucovision.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "jwt.secret=2baf4d3b0e9b42c68fe6d9e9bdcdfcbf2baf4d3b0e9b42c68fe6d9e9bdcdfcbf"
})
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secretKey;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = Jwts.builder()
                .setSubject("user@example.com")
                .claim("roles", "ROLE_USER")
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

    @Test
    void tokenMissingRoleClaim() {
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();

        assertThrows(RuntimeException.class, () -> jwtUtil.extractRole(token));
    }

    @Test
    void tokenWithInvalidSignature() {
        // Clé de 32 caractères → 256 bits
        String fakeKey = "0123456789abcdef0123456789abcdef";

        String invalidToken = Jwts.builder()
                .setSubject("user@example.com")
                .claim("roles", "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor(fakeKey.getBytes()))
                .compact();

        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }

}