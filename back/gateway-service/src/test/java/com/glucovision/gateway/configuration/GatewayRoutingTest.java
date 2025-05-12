package com.glucovision.gateway.configuration;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.core.env.Environment;
import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class GatewayRoutingTest {
    @Autowired
    Environment env;

    @Autowired
    private WebTestClient webTestClient;

    static MockWebServer mockPatientService;

    @BeforeAll
    static void setup() throws IOException {
        mockPatientService = new MockWebServer();
        mockPatientService.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockPatientService.shutdown();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        String uri = mockPatientService.url("/").toString();
        registry.add("spring.cloud.gateway.routes[0].id", () -> "patient");
        registry.add("spring.cloud.gateway.routes[0].uri", () -> uri);
        registry.add("spring.cloud.gateway.routes[0].predicates[0]", () -> "Path=/api/patients/**");
    }

    @BeforeEach
    void logActiveProfiles() {
        System.out.println("Active profiles: " + Arrays.toString(env.getActiveProfiles()));
    }

    @Test
    void routeToPatientService() {


        mockPatientService.enqueue(new MockResponse()
                .setBody("[{\"id\":1,\"firstName\":\"Fouad\",\"lastName\":\"Turbo\"}]")
                .addHeader("Content-Type", "application/json"));

        webTestClient.get()
                .uri("/api/patients")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].firstName").isEqualTo("Fouad");
    }

    @Test
    void routeToUnknownService_shouldReturnNotFound() {
        webTestClient.get()
                .uri("/api/unknown-route")
                .exchange()
                .expectStatus().isNotFound();
    }

}
