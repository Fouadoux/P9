package com.glucovision.gateway.configuration;

import com.glucovision.gateway.security.JwtAuthenticationFilter;
import com.glucovision.gateway.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewaySecurityEnabledTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void unauthorizedWithoutToken() {
        webClient.get()
                .uri("/api/patients")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}

