package com.glucovision.gateway.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Global CORS configuration for the Gateway Service.
 * <p>
 * This configuration ensures that frontend applications (e.g., Angular on port 4200)
 * can interact with the backend microservices through the gateway by allowing
 * cross-origin requests with specific methods, headers, and credentials.
 */
@Slf4j
@Configuration
public class CorsGlobalConfiguration {

    /**
     * Creates a {@link CorsWebFilter} bean with predefined CORS rules applied to all routes.
     *
     * @return the configured CORS web filter
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        log.info("🔧 Initializing global CORS configuration...");

        CorsConfiguration corsConfig = new CorsConfiguration();

        List<String> allowedOrigins = List.of("http://localhost:4200");
        List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
        List<String> allowedHeaders = List.of(
                "Authorization", "Cache-Control", "Content-Type",
                "X-Requested-With", "Accept", "Origin",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"
        );
        List<String> exposedHeaders = List.of("Authorization", "Content-Disposition");

        corsConfig.setAllowedOrigins(allowedOrigins);
        corsConfig.setAllowedMethods(allowedMethods);
        corsConfig.setAllowedHeaders(allowedHeaders);
        corsConfig.setExposedHeaders(exposedHeaders);
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L); // 1 hour

        log.debug("✅ Allowed origins: {}", allowedOrigins);
        log.debug("✅ Allowed methods: {}", allowedMethods);
        log.debug("✅ Allowed headers: {}", allowedHeaders);
        log.debug("✅ Exposed headers: {}", exposedHeaders);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        log.info("✅ Global CORS configuration successfully registered.");

        return new CorsWebFilter(source);
    }
}
