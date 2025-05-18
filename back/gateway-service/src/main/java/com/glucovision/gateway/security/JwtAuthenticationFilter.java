package com.glucovision.gateway.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global JWT authentication filter for Spring Cloud Gateway.
 * <p>
 * Intercepts all incoming requests and validates JWT tokens.
 * If the token is valid, user information is propagated in custom headers.
 * If the token is missing or invalid, a 401 Unauthorized response is returned.
 * <p>
 * This filter is bypassed when {@code security.enabled=false} (e.g., in test mode).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Value("${security.enabled:true}")
    private boolean securityEnabled;

    /**
     * Logs a message when the filter is initialized.
     */
    @PostConstruct
    public void init() {
        log.info("[GATEWAY] ✅ JwtAuthenticationFilter initialized.");
    }

    /**
     * Applies JWT validation logic to all requests except for public endpoints.
     *
     * @param exchange the current server exchange
     * @param chain    the filter chain to execute
     * @return the filtered exchange, or an immediate 401 response if authentication fails
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!securityEnabled) {
            log.warn("[GATEWAY] ⚠️ JWT filtering is disabled (security.enabled=false)");
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();
        log.info("[GATEWAY] Incoming request: {} {}", exchange.getRequest().getMethod(), path);

        if (path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.startsWith("/public")) {
            log.debug("[GATEWAY] Public endpoint – bypassing filter: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("[GATEWAY] Bearer token received");

            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                log.info("[GATEWAY] ✅ Valid token – User: {}, Role: {}", email, role);

                exchange = exchange.mutate()
                        .request(r -> r.headers(headers -> {
                            headers.add("X-auth-email", email);
                            headers.add("X-auth-role", role);
                        }))
                        .build();

                return chain.filter(exchange);
            } else {
                log.error("[GATEWAY] ❌ Invalid token");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            log.error("[GATEWAY] ❌ Missing or malformed Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Defines the order of the filter (higher priority = lower number).
     *
     * @return order value
     */
    @Override
    public int getOrder() {
        return -1; // High priority
    }
}
