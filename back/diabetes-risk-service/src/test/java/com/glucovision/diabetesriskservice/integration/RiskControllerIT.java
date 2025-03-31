package com.glucovision.diabetesriskservice.integration;

import com.glucovision.diabetesriskservice.util.AbstractWireMockTest;


import com.glucovision.diabetesriskservice.util.AbstractWireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RiskControllerIT extends AbstractWireMockTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnRiskLevel_whenPatientAndNotesExist() {
        // GIVEN – WireMock simule les réponses
        stubFor(get(urlEqualTo("/patients/1"))
                .willReturn(okJson("""
                    {
                        "id": 1,
                        "firstName": "John",
                        "lastName": "Doe",
                        "birthDate": "1980-05-10",
                        "gender": "M"
                    }
                """)));

        stubFor(get(urlEqualTo("/notes/patient/1"))
                .willReturn(okJson("""
                    [
                        { "patientId": 1, "comments": "Patient feels tired and dizzy" },
                        { "patientId": 1, "comments": "Weight loss observed" }
                    ]
                """)));

        // WHEN – appel réel via RestTemplate
        String url = "http://localhost:" + port + "/risk/patient/1";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // THEN – vérification
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("riskLevel");
        assertThat(response.getBody()).contains("BORDERLINE"); // si tu veux être plus précis
    }

}
