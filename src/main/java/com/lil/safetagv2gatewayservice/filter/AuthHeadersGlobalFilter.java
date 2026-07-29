package com.lil.safetagv2gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class AuthHeadersGlobalFilter {

    @Bean
    public GlobalFilter authHeadersGlobalFilter() {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(auth -> {
                    Jwt jwt = auth.getToken();
                    String userId = jwt.getSubject();
                    String role = auth.getAuthorities().stream()
                            .findFirst()
                            .map(GrantedAuthority::getAuthority)
                            .orElse("");

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Role", role)
                            .build();

                    return exchange.mutate().request(mutatedRequest).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}