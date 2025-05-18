package com.glucovision.gateway.configuration;

import com.glucovision.gateway.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

/**
 * Gateway routing and security configuration for GlucoVision.
 * <p>
 * This configuration defines all route mappings to internal microservices,
 * and conditionally registers the {@link JwtAuthenticationFilter} if available.
 */
@Configuration
@Slf4j
@Profile("!test")
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Constructor with optional JWT authentication filter.
     * The filter may be absent in test environments or if security is disabled.
     *
     * @param jwtAuthFilter the optional JWT authentication filter
     */
    public GatewayConfig(Optional<JwtAuthenticationFilter> jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter.orElse(null);
        if (this.jwtAuthFilter == null) {
            log.warn("[GATEWAY] ⚠️ JwtAuthenticationFilter not injected (test profile or security disabled)");
        } else {
            log.info("[GATEWAY] ✅ JwtAuthenticationFilter successfully injected.");
        }
    }

    /**
     * Defines the route mappings between API paths and internal service URIs.
     *
     * @param builder Spring Cloud Gateway route builder
     * @return configured {@link RouteLocator}
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        log.info("[GATEWAY] 🔀 Initializing route mappings...");

        return builder.routes()
                .route("patient-route", r -> r.path("/api/patients/**")
                        .uri("http://patient-service:8080"))
                .route("note-route", r -> r.path("/api/notes/**")
                        .uri("http://note-service:8080"))
                .route("diabetes-risk-route", r -> r.path("/api/risk/**")
                        .uri("http://diabetes-risk-service:8080"))
                .route("auth-route", r -> r.path("/api/auth/**", "/api/users/**")
                        .uri("http://auth-service:8080"))
                .build();
    }

    /**
     * Registers the JWT authentication filter as a global filter,
     * if it has been injected successfully.
     *
     * @return the JWT filter applied globally to all routes
     */
    @Bean
    @ConditionalOnBean(JwtAuthenticationFilter.class)
    public GlobalFilter jwtGlobalFilter() {
        log.info("[GATEWAY] 🔐 Registering JwtAuthenticationFilter as GlobalFilter");
        return jwtAuthFilter;
    }
}
