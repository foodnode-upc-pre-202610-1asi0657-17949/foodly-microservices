package com.foodly.radar.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtValidationFilter implements ContainerRequestFilter {

    private static final String SECRET_KEY = "TuLlaveSecretaSuperSeguraParaElEcosistemaFoodlyMeroMonorepo2026";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        if (path.endsWith("/radar/status")) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "Token de autorización requerido");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            requestContext.setProperty("userEmail", email);

        } catch (Exception e) {
            abortWithUnauthorized(requestContext, "Token JWT inválido o expirado");
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .entity("{\"error\":\"" + message + "\"}")
                .build());
    }
}