package com.glucovion.authservice.controller;

import com.glucovion.authservice.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling internal authentication requests between microservices.
 * This endpoint is protected using an internal API key and should never be exposed to public clients.
 */
@Slf4j
@RestController
@RequestMapping("/internal-auth")
public class InternalAuthController {

    private final String internalApiKey;
    private final JwtService jwtService;

    /**
     * Constructs the InternalAuthController with required dependencies.
     *
     * @param jwtService      Service used to generate JWT tokens.
     * @param internalApiKey  Secret API key used to authenticate internal service calls.
     */
    public InternalAuthController(JwtService jwtService,
                                  @Value("${internal.api.key}") String internalApiKey) {
        this.jwtService = jwtService;
        this.internalApiKey = internalApiKey;
    }

    /**
     * Generates a JWT token with the "INTERNAL_SERVICE" role for use in inter-service communication.
     *
     * @param apiKey   The internal API key provided in the request header.
     * @param request  The HTTP request, used to log the origin IP.
     * @return A signed JWT token with internal service privileges.
     * @throws RuntimeException if the provided API key is invalid.
     */
    @PostMapping("/internal-token")
    public String generateInternalToken(@RequestHeader("Internal-Api-Key") String apiKey,
                                        HttpServletRequest request) {
        log.info("[POST] Internal token requested from IP: {}", request.getRemoteAddr());

        if (!apiKey.equals(internalApiKey)) {
            log.warn("❌ Invalid internal API key attempt from IP: {}", request.getRemoteAddr());
            throw new RuntimeException("Invalid internal API key");
        }

        String token = jwtService.generateTokenWithRole("INTERNAL_SERVICE");
        log.info("✅ Internal JWT token generated successfully");
        return token;
    }
}
