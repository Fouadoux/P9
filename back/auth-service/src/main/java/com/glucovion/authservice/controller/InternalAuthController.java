package com.glucovion.authservice.controller;

import com.glucovion.authservice.security.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/internal-auth")
public class InternalAuthController {

 //   @Value("${server.internal.api.key}")
    private final String internalApiKey;

    private final JwtService jwtService;

    public InternalAuthController(JwtService jwtService,
                                  @Value("${server.internal.api.key}") String internalApiKey) {
        this.jwtService = jwtService;
        this.internalApiKey = internalApiKey;
        log.info(" ---------------------------------------- internal api key : {}", internalApiKey);
    }


    @PostMapping("/internal-token")
    public String generateInternalToken(@RequestHeader("Internal-Api-Key") String apiKey) {
        log.info("API KEY reçue : {}", apiKey);
        if(!apiKey.equals(internalApiKey)) {
            throw new RuntimeException("Invalid internal api key");
        }
        return jwtService.generateTokenWithRole("INTERNAL_SERVICE");
    }



}
