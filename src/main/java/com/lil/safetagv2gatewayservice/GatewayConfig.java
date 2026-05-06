package com.lil.safetagv2gatewayservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;

import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Value("${USER_SERVICE_URL:http://localhost:8084}")
    private String userServiceUrl;

    @Value("${RPPS_SERVICE_URL:http://localhost:8081}")
    private String rppsServiceUrl;

    @Value("${REVIEW_SERVICE_URL:http://localhost:8082}")
    private String reviewServiceUrl;

    @Value("${MODERATION_SERVICE_URL:http://localhost:8083}")
    private String moderationServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
                .route(path("/api/v1/users/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(userServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return GatewayRouterFunctions.route("auth-service")
                .route(path("/api/v1/auth/login"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(userServiceUrl)) // Utilise le même service si c'est le cas
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> rppsServiceRoute() {
        return GatewayRouterFunctions.route("rpps-service")
                .route(path("/api/v1/practitioners/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(rppsServiceUrl))
                .build();
    }

    // ... Fais de même pour les autres services (review et moderation)
}
