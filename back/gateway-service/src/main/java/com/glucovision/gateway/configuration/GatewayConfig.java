package com.glucovision.gateway.configuration;

import com.glucovision.gateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("patient-route",r -> r.path("/api/patients/**").uri("http://localhost:8085"))
                .route("history_route",r -> r.path("/api/notes/**").uri("http://localhost:8082"))
                .route("diabetes_risk_route",r->r.path("/api/risk/**").uri("http://localhost:8084"))
                .route("auth-service",r -> r.path("/api/auth/**").uri("http://localhost:8087"))
                .build();
    }

    @Bean
    public GlobalFilter jwtGlobalFilter() {
        return jwtAuthFilter;
    }

}
