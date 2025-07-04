package com.glucovion.authservice.controller;

import com.glucovion.authservice.config.SecurityConfig;
import com.glucovion.authservice.security.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @Bean
    @Primary  // Surcharge le UserDetailsService de la config principale
    public UserDetailsService testUserDetailsService() {
        // Crée des utilisateurs en mémoire spécifiques pour les tests
        return new InMemoryUserDetailsManager(
                User.withUsername("testuser")
                        .password("{noop}password")  // {noop} pour simplifier les tests
                        .roles("USER")
                        .build(),

                User.withUsername("admin")
                        .password("{noop}admin")
                        .roles("ADMIN", "USER")
                        .build()
        );
    }

    @Primary  // Surcharge du JwtService pour les tests
    @Bean
    public JwtService testJwtService() {
        return new JwtService("test-secret-key-at-least-32-characters-long") {
            // Implémentation simplifiée ou mock pour les tests
            @Override
            public String generateToken(UserDetails userDetails) {
                return "mock-token";
            }

            @Override
            public boolean isTokenValid(String token, UserDetails userDetails) {
                return true;
            }
        };
    }
}