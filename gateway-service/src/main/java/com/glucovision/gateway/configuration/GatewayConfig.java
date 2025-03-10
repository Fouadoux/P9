package com.glucovision.gateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("patient_route",r -> r.path("/patients/**").uri("lb://paient-service"))
                .route("history_route",r -> r.path("/history/**").uri("lb://history-service"))
                .route("diabetes_risk_route",r->r.path("/diabetes/**").uri("lb://diabetes-service"))
                .build();
    }
}
