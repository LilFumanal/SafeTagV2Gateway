package com.lil.safetagv2gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import io.jsonwebtoken.io.Decoders;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String jwtSecret;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKeySpec key = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}