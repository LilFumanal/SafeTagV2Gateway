package com.lil.safetagv2gatewayservice;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GatewayConfig {

    @Value("${USER_SERVICE_URL:http://localhost:8084}")
    private String userServiceUrl;

    @Value("${RPPS_SERVICE_URL:http://localhost:8081}")
    private String rppsServiceUrl;

    @Value("${REVIEW_SERVICE_URL:http://localhost:8082}")
    private String reviewServiceUrl;

    private final AuthenticationFilter authFilter;

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

    public GatewayConfig(AuthenticationFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        log.info("🚀 Configuration des routes Gateway");
        log.info("RPPS Service URL: {}", rppsServiceUrl);
        log.info("User Service URL: {}", userServiceUrl);

        return builder.routes()
                .route("rpps-service", r -> r.path("/api/v1/rpps/**")
                        .filters(f -> f.filter(authFilter))
                        .uri(rppsServiceUrl))

                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri(userServiceUrl))
                .route("user-service", r -> r.path("/api/v1/auth/login")
                        .filters(f -> f.filter(authFilter))
                        .uri(userServiceUrl))

                .route("review-service", r -> r.path("/api/v1/reviews/**")
                        .filters(f -> f.filter(authFilter))
                        .uri(reviewServiceUrl))

                .build();
    }

}
