package com.glucovion.authservice.config;

import com.glucovion.authservice.security.JwtAuthenticationFilter;
import com.glucovion.authservice.security.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures Spring Security for the Auth Service.
 *
 * - Enables stateless JWT authentication.
 * - Defines public and protected endpoints.
 * - Sets up custom JWT filter and password encoding.
 * - Logs security setup steps for debugging and traceability.
 *
 * Public endpoints: /api/auth/** and /internal-auth/**
 * Admin-only endpoints: /api/users/**
 * All other endpoints require authentication.
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("[SECURITY] Initializing BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("[SECURITY] Loading AuthenticationManager");
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("[SECURITY] Configuring SecurityFilterChain");
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(userDetailsService, jwtService);
        log.info("[SECURITY] JWT filter added before UsernamePasswordAuthenticationFilter");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/internal-auth/**").permitAll()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
