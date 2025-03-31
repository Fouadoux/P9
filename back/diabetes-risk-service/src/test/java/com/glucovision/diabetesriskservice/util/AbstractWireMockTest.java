package com.glucovision.diabetesriskservice.util;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractWireMockTest {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();

        // On redéfinit les URLs utilisées par Feign (tu adapteras les noms des propriétés à ton projet)
        System.setProperty("note-service.url", wireMockServer.baseUrl());
        System.setProperty("patient-service.url", wireMockServer.baseUrl());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

}
