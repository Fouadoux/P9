package com.glucovision.diabetesriskservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url ="${auth-service.url}")
public interface InternalAuthClient {

    @PostMapping("/internal-auth/internal-token")
    String getInternalToken(@RequestHeader("Internal-Api-Key") String apiKey);
}
