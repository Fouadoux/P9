package com.glucovision.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsGlobalConfiguration {

  /*  @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:4200"); // Ton front Angular
        corsConfig.addAllowedMethod("*"); // GET, POST, PUT, DELETE etc.
        corsConfig.addAllowedHeader("*"); // Tous les headers (ex : Authorization)
        corsConfig.setAllowCredentials(true); // Important si tu envoies des cookies ou tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

   */


    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Configurez plusieurs origines si nécessaire
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost:4200"));

        // Méthodes autorisées (préférez l'explicite plutôt que '*')
        corsConfig.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Headers autorisés
        corsConfig.setAllowedHeaders(List.of(
                "Authorization", "Cache-Control", "Content-Type",
                "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Headers exposés au frontend
        corsConfig.setExposedHeaders(List.of(
                "Authorization", "Content-Disposition"
        ));

        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);  // Cache des options CORS pendant 1 heure

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}


