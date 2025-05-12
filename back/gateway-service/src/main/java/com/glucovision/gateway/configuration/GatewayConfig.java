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

@Configuration
@Slf4j
@Profile("!test")
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public GatewayConfig(Optional<JwtAuthenticationFilter> jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter.orElse(null);
        if (this.jwtAuthFilter == null) {
            log.warn("[GATEWAY] ⚠️ JwtAuthenticationFilter non injecté (profil test ou désactivé)");
        }
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
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

    @Bean
    @ConditionalOnBean(JwtAuthenticationFilter.class)
    public GlobalFilter jwtGlobalFilter() {
        return jwtAuthFilter;
    }
}
