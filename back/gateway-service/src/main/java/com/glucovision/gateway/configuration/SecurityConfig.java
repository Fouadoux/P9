package com.glucovision.gateway.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * WebFlux security configuration for the Gateway Service.
 * <p>
 * This configuration defines public and protected endpoints for incoming requests.
 * Endpoints related to authentication and internal service communication are publicly accessible,
 * while all other routes require JWT-based authentication.
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain for HTTP requests.
     *
     * @param http the reactive server HTTP security configuration
     * @return the configured {@link SecurityWebFilterChain}
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("[GATEWAY SECURITY] 🔐 Initializing WebFlux security configuration");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/api/auth/**",
                                "/api/users/**",
                                "/actuator/**",
                                "/api/patients/**",
                                "/api/notes/**",
                                "/api/risk/**"
                        )
                        .permitAll()
                        .anyExchange()
                        .authenticated()
                )
                .build();
    }
}
