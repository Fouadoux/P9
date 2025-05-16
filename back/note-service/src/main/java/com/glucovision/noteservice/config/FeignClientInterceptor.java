package com.glucovision.noteservice.config;

import com.glucovision.noteservice.client.InternalAuthClient;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Intercepts all outgoing Feign client requests and adds an internal authorization token
 * to the "Authorization" header.
 * <p>
 * The token is retrieved dynamically from the {@link InternalAuthClient} using a pre-configured API key.
 * This interceptor skips the call to the internal token endpoint itself to prevent circular dependencies.
 */
@Slf4j
@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    private final InternalAuthClient internalAuthClient;
    private final String apiKey;

    /**
     * Constructor injecting the internal auth client and API key from configuration.
     *
     * @param internalAuthClient the client used to retrieve internal tokens
     * @param apiKey the API key used to authenticate internal token requests
     */
    public FeignClientInterceptor(InternalAuthClient internalAuthClient,
                                  @Value("${internal.api.key}") String apiKey) {
        this.internalAuthClient = internalAuthClient;
        this.apiKey = apiKey;
    }

    /**
     * Adds the Authorization header with a Bearer token to all outgoing requests,
     * except the internal token retrieval endpoint.
     *
     * @param requestTemplate the request template to modify
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.path().toString();

        // Avoid adding Authorization header when retrieving the internal token itself
        if (url.contains("/internal-auth/internal-token")) {
            log.debug("Skipping token injection for internal token request.");
            return;
        }

        log.debug("Preparing to inject Authorization header for request to: {}", url);

        try {
            String token = internalAuthClient.getInternalToken(apiKey);
            requestTemplate.header("Authorization", "Bearer " + token);
            log.debug("Successfully injected Authorization header.");
        } catch (Exception e) {
            log.error("Failed to retrieve internal token for request to {}: {}", url, e.getMessage());
            throw e;
        }
    }
}
