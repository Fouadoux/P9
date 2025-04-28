package com.glucovision.diabetesriskservice.config;

import com.glucovision.diabetesriskservice.client.InternalAuthClient;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    private final InternalAuthClient internalAuthClient;
    private final String apiKey;

    public FeignClientInterceptor(InternalAuthClient internalAuthClient,
                                  @Value("${internal.api.key}") String apiKey) {
        this.internalAuthClient = internalAuthClient;
        this.apiKey = apiKey;
    }


    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.path().toString();

        if (url.contains("/internal-auth/internal-token")) {
            return;
        }

        String token = internalAuthClient.getInternalToken(apiKey);
        requestTemplate.header("Authorization", "Bearer " + token);
    }


}
