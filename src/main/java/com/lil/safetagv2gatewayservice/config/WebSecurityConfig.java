package com.lil.safetagv2gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/practitioners/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/reviews/references/**").permitAll()

                        // Practitioners : écriture jamais exposée (import XML only)
                        .pathMatchers(HttpMethod.POST, "/api/v1/practitioners/**").denyAll()
                        .pathMatchers(HttpMethod.PUT, "/api/v1/practitioners/**").denyAll()
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/practitioners/**").denyAll()

                        // Interne uniquement (pas exposé via Gateway HTTP)
                        .pathMatchers("/api/v1/geocoding/**").denyAll()

                        // Modération : ADMIN requis
                        .pathMatchers("/api/v1/moderation/**").hasRole("ADMIN")

                        // Reste (reviews, internal/reviews) : authentifié
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                )
                .build();
    }
}