package com.glucovision.diabetesriskservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client interface for communicating with the Authentication Service.
 * <p>
 * This client is used internally to request a JWT token for inter-service authentication,
 * using a predefined internal API key.
 * </p>
 */
@FeignClient(name = "auth-service", url = "${auth-service.url}")
public interface InternalAuthClient {

    /**
     * Requests an internal JWT token from the Authentication Service.
     *
     * @param apiKey The internal API key used to authenticate the request.
     * @return A JWT token as a String, intended for internal service communication.
     */
    @PostMapping("/internal-auth/internal-token")
    String getInternalToken(@RequestHeader("Internal-Api-Key") String apiKey);
}
