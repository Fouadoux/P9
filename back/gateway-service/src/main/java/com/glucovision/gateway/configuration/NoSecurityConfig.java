package com.glucovision.gateway.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for test environments.
 * <p>
 * This configuration disables CSRF protection and allows all exchanges without authentication.
 * It is only active when the "test" Spring profile is enabled.
 */
@Slf4j
@Configuration
@Profile("test")
public class NoSecurityConfig {

    /**
     * Configures a permissive security filter chain for tests.
     *
     * @param http the reactive HTTP security configuration
     * @return a {@link SecurityWebFilterChain} allowing all requests
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        log.warn("[GATEWAY] ⚠️ Running in TEST profile: security is DISABLED.");
        return http
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
