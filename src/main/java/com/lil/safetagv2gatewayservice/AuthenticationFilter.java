package com.lil.safetagv2gatewayservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GatewayFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final JwtUtil jwtUtils;

    // Injection de JwtUtils
    public AuthenticationFilter(JwtUtil jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("➡️ AuthenticationFilter exécute la requête pour le chemin : {}", exchange.getRequest().getURI().getPath());
        String path = exchange.getRequest().getURI().getPath();

        // Liste des endpoints publics (sans authentification)
        List<String> openApiEndpoints = List.of(
                "/api/v1/users/register",
                "/api/v1/auth/login"
        );

        // Si le chemin est dans la liste, on laisse passer directement
        if (openApiEndpoints.contains(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("--- [GATEWAY-DEBUG] Token manquant ou mal formé ---");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            // 1. Validation et extraction des claims
            var claims = jwtUtils.getClaims(token);
            String userId = claims.getSubject();
            // On récupère le rôle (assure-toi que la clé "role" ou "roles" correspond à ton JwtUtils)
            String userRole = claims.get("roles", String.class);

            // 2. Injection des headers réels
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", userRole)
                    .build();
            logger.info("--- [GATEWAY-DEBUG] Token valide, transfert vers le service ---");
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            logger.error("❌ Erreur de validation du token : {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}

