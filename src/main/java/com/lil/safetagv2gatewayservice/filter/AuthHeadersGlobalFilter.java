package com.lil.safetagv2gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;


@Slf4j
@Configuration
public class AuthHeadersGlobalFilter {

    @Bean
    public GlobalFilter authHeadersFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            log.info("[AuthFilter] Path: {}", path);

            return ReactiveSecurityContextHolder.getContext()
                    .doOnNext(ctx -> log.info("[AuthFilter] SecurityContext found"))
                    .switchIfEmpty(Mono.fromRunnable(() -> log.info("[AuthFilter] NO SecurityContext")))
                    .map(SecurityContext::getAuthentication)
                    .filter(auth -> auth instanceof JwtAuthenticationToken)
                    .map(auth -> (JwtAuthenticationToken) auth)
                    .map(auth -> {
                        Jwt jwt = auth.getToken();
                        String userId = jwt.getSubject();
                        String role = auth.getAuthorities().stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority)
                                .orElse("");

                        log.info("[AuthFilter] Adding headers - UserId: {}, Role: {}", userId, role);

                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                .header("X-User-Role", role)
                                .build();

                        return exchange.mutate().request(mutatedRequest).build();
                    })
                    .defaultIfEmpty(exchange)
                    .flatMap(chain::filter);
        };
    }
}