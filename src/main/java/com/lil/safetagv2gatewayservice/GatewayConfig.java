package com.lil.safetagv2gatewayservice;

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

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
                .route(path("/api/v1/users/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://localhost:8084"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return GatewayRouterFunctions.route("auth-service")
                .route(path("/api/v1/auth/login"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://localhost:8084"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> rppsServiceRoute() {
        return GatewayRouterFunctions.route("rpps-service")
                .route(path("/api/v1/practitioners/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> reviewServiceRoute() {
        return GatewayRouterFunctions.route("review-service")
                .route(path("/api/v1/reviews/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://localhost:8082"))
                .build();

    }
    @Bean
    public RouterFunction<ServerResponse> moderationServiceRoute() {
        return GatewayRouterFunctions.route("moderation-service")
                .route(path("/api/v1/moderation/reviews"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://localhost:8083"))
                .build();

    }


}
