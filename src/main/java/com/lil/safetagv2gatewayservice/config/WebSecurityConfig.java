package com.lil.safetagv2gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/rpps/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/reviews/references/**").permitAll()
                        .pathMatchers("/api/v1/geocoding/**").denyAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/rpps/**").denyAll()
                        .pathMatchers(HttpMethod.PUT, "/api/v1/rpps/**").denyAll()
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/rpps/**").denyAll()
                        .pathMatchers("/api/v1/moderation/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverter()))
                )
                .build();
    }
}