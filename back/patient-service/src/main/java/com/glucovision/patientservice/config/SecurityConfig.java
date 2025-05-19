package com.glucovision.patientservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuration class for securing the Patient Service using Spring Security and JWT authentication.
 * <p>
 * This setup includes:
 * <ul>
 *     <li>Role-based access control for patient-related endpoints.</li>
 *     <li>Stateless authentication using signed JWT tokens (HMAC SHA-256).</li>
 *     <li>Custom converter for extracting user roles from JWT claims.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final String jwtSecret;

    /**
     * Constructor-based injection of the JWT secret key from the application configuration.
     *
     * @param jwtSecret the symmetric key used for JWT validation
     */
    public SecurityConfig(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    /**
     * Defines the main security filter chain, securing HTTP endpoints based on user roles.
     *
     * @param http the security configuration object
     * @param jwtAuthenticationConverter the custom JWT converter extracting authorities
     * @return a configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        log.info("[SECURITY] Configuring SecurityFilterChain for Patient Service");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/patients/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_INTERNAL_SERVICE")

                        .requestMatchers(HttpMethod.POST, "/api/patients/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/patients/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/patients/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/actuator/**","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }

    /**
     * Provides a {@link JwtAuthenticationConverter} configured with a custom role converter.
     *
     * @return a customized JWT authentication converter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomRoleConverter());
        log.debug("[SECURITY] JwtAuthenticationConverter initialized with CustomRoleConverter");
        return converter;
    }

    /**
     * Initializes the {@link JwtDecoder} using the configured symmetric HMAC-SHA256 secret.
     *
     * @return the JWT decoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        log.info("[SECURITY] JwtDecoder initialized with HMAC secret");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
