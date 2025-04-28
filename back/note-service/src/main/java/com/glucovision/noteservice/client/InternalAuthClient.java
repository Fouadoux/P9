package com.glucovision.noteservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "http://localhost:8087")
public interface InternalAuthClient {

    @PostMapping("/internal-auth/internal-token")
    String getInternalToken(@RequestHeader("Internal-Api-Key") String apiKey);
}
