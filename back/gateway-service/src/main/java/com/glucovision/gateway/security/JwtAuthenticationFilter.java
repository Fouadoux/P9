package com.glucovision.gateway.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Profile("!test")
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @PostConstruct
    public void init() {
        log.info("[GATEWAY] ✅ JwtAuthenticationFilter initialisé !");
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    @Value("${security.enabled:true}") // par défaut à true pour éviter la cata en prod
    private boolean securityEnabled;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!securityEnabled) {
            log.warn("[GATEWAY] ⚠️ Filtrage JWT désactivé (mode test)");
            return chain.filter(exchange);
        }
        String path = exchange.getRequest().getPath().value();
        logger.info("[GATEWAY] JwtAuthenticationFilter - Requête interceptée : {} {}",
                exchange.getRequest().getMethod(), path);

        if (path.startsWith("/public")) {
            logger.info("[GATEWAY] Requête publique détectée : {}", path);
            return chain.filter(exchange);
        }
        // Ignorer les requêtes vers /api/auth/login
        if (path.equals("/api/auth/login")) {
            logger.info("[GATEWAY] Requête vers /api/auth/login ignorée.");
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.info("[GATEWAY] Token reçu : {}", token);

            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                logger.info("[GATEWAY] Token valide ✅");
                logger.info("[GATEWAY] Email : {}", email);
                logger.info("[GATEWAY] Role : {}", role);

                // Ajout des infos dans les headers
                exchange = exchange.mutate()
                        .request(r -> r.headers(headers -> {
                            headers.add("X-auth-email", email);
                            headers.add("X-auth-role", role);
                        }))
                        .build();

                return chain.filter(exchange);
            } else {
                logger.error("[GATEWAY] Token invalide ❌");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            logger.error("[GATEWAY] Pas de header Authorization ou format incorrect.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }


    @Override
    public int getOrder() {
        return -1; // priorité haute
    }
}
