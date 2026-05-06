package com.lil.safetagv2gatewayservice;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.contains("/api/v1/auth/login") || (path.contains("api/v1/users/register"))) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        String token = authHeader.substring(7);
        io.jsonwebtoken.Claims claims;
        try {
            claims = jwtUtil.getClaims(token);
        } catch (Exception e) {
            System.err.println("❌ Erreur validation JWT : " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // 4. Ajout des informations dans les Headers via un Wrapper
        jakarta.servlet.http.HttpServletRequestWrapper wrappedRequest = new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("X-User-Id".equalsIgnoreCase(name)) return claims.getSubject();
                if ("X-User-Role".equalsIgnoreCase(name)) return claims.get("role", String.class);
                return super.getHeader(name);
            }
        };

        // 5. On passe la requête modifiée vers les microservices
        filterChain.doFilter(wrappedRequest, response);
    }
}
