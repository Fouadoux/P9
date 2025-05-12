package com.glucovision.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testValidToken() {
        ReflectionTestUtils.setField(filter, "securityEnabled", true);

        when(chain.filter(any())).thenReturn(Mono.empty());

        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/patients")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
        );

        when(jwtUtil.isTokenValid("valid-token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid-token")).thenReturn("user@test.com");
        when(jwtUtil.extractRole("valid-token")).thenReturn("ROLE_USER");

        // Act
        filter.filter(exchange, chain).block();

        // Assert
        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(exchangeCaptor.capture());

        ServerWebExchange mutatedExchange = exchangeCaptor.getValue();
        assertNotNull(mutatedExchange.getRequest().getHeaders().getFirst("X-auth-email"));
        assertNotNull(mutatedExchange.getRequest().getHeaders().getFirst("X-auth-role"));
    }

    @Test
    void malformedTokenReturns401() {
        ReflectionTestUtils.setField(filter, "securityEnabled", true);

        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/patients")
                        .header(HttpHeaders.AUTHORIZATION, "InvalidFormat")
        );

        filter.filter(exchange, chain).block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }

    @Test
    void expiredTokenReturns401() {
        ReflectionTestUtils.setField(filter, "securityEnabled", true);

        when(jwtUtil.isTokenValid(any())).thenReturn(false);

        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/patients")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer expired-token")
        );

        filter.filter(exchange, chain).block();
        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }

    @Test
    void publicEndpointNoTokenRequired() {
        ReflectionTestUtils.setField(filter, "securityEnabled", true);
        when(chain.filter(any())).thenReturn(Mono.empty());

        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/public/api/status")
        );

        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange); // Doit passer sans vérification
    }
}