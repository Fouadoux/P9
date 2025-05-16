package com.glucovision.noteservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client to communicate with the Auth Service to retrieve internal JWT tokens
 * used for secure inter-service communication.
 *
 * <p>This client is typically used by other microservices to obtain a short-lived
 * internal token that grants limited access with the "ROLE_INTERNAL_SERVICE" authority.</p>
 *
 * <p>⚠️ Requires a valid API key provided via the {@code Internal-Api-Key} header.</p>
 *
 * Example usage:
 * <pre>
 * String token = internalAuthClient.getInternalToken("my-secure-api-key");
 * </pre>
 *
 * The `auth-service.url` should be configured in application properties or config server.
 */
@FeignClient(name = "auth-service", url = "${auth-service.url}")
public interface InternalAuthClient {

    /**
     * Retrieves a JWT token for internal service communication using a predefined API key.
     *
     * @param apiKey The internal API key for authentication (passed in the request header).
     * @return A JWT token string prefixed with "Bearer ".
     */
    @PostMapping("/internal-auth/internal-token")
    String getInternalToken(@RequestHeader("Internal-Api-Key") String apiKey);
}
