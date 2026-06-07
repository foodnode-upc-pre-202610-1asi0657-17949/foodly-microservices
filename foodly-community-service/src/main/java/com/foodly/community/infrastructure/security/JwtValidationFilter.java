package com.foodly.community.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Provider // Interceptor global nativo de WildFly
public class JwtValidationFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtValidationFilter.class);
    private static final String SECRET_KEY_STR = "TuClaveSecretaSuperLargaYSeguraParaFirmarLosTokensJWTDeFoodly123!";

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Bypass para recursos del sistema (Swagger/OpenAPI si los tuvieras)
        if (path.startsWith("openapi") || path.startsWith("swagger")) {
            return;
        }

        // REGLA ESPECIAL COMUNIDAD: Permitir GET /huariques/{id}/reviews públicamente sin token
        if (path.contains("huariques") && path.contains("reviews") && "GET".equalsIgnoreCase(method)) {
            return;
        }

        String authorizationHeader = requestContext.getHeaderString("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "Token de autorización requerido para interactuar en la comunidad");
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY_STR.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            // Validación de Rol Empresarial (Exige ser CLIENT para comentar)
            if (roles == null || (!roles.contains("CLIENT") && !roles.contains("CUSTOMER"))) {
                requestContext.abortWith(Response
                        .status(Response.Status.FORBIDDEN)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{\"error\":\"Acceso denegado, requiere rol de Cliente para publicar reviews\"}")
                        .build());
                return;
            }

            // Inyectamos los atributos en el request
            servletRequest.setAttribute("username", username);
            servletRequest.setAttribute("roles", roles);

        } catch (JwtException e) {
            log.warn("Token JWT inválido en Community Service: {}", e.getMessage());
            abortWithUnauthorized(requestContext, "Token inválido o expirado");
        } catch (Exception e) {
            log.error("Error crítico validando el token en el módulo social", e);
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response
                .status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"" + message + "\"}")
                .build());
    }
}