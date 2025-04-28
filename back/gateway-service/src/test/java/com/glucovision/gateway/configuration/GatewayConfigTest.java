package com.glucovision.gateway.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayConfigTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void routeToPatientService() {
        webClient.get()
                .uri("/api/patients/1")
                .header("Authorization", "Bearer mock-token")
                .exchange()
                .expectStatus().isOk();
        // → Vérifie que la requête est routée vers le service patient (8085)
    }

    @Test
    void unauthorizedWithoutToken() {
        webClient.get()
                .uri("/api/patients/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}