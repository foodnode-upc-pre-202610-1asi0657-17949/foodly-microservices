package com.foodly.identity.infrastructure.security;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Provider // Registra de manera global este filtro en el motor JAX-RS de WildFly
public class JwtAuthFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    private JwtProvider jwtProvider;

    @Context
    private HttpServletRequest servletRequest; // Inyección nativa del contexto HTTP

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Bypass para rutas públicas (Autenticación y recursos del sistema)
        if (path.startsWith("auth/") || path.startsWith("openapi") || path.startsWith("swagger")) {
            return;
        }

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            abortWithUnauthorized(requestContext, "Token de autenticación requerido");
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isEmpty()) {
            abortWithUnauthorized(requestContext, "Token JWT vacío");
            return;
        }

        if (!jwtProvider.validateToken(token)) {
            abortWithUnauthorized(requestContext, "Token JWT inválido o expirado");
            return;
        }

        // Extracción de claims desde el token validado
        String userId = jwtProvider.getUserId(token);
        String email = jwtProvider.getEmail(token);
        List<String> roles = jwtProvider.getRoles(token);

        // Adjuntar atributos al request para que UserController los lea con @Context
        servletRequest.setAttribute("userId", userId);
        servletRequest.setAttribute("email", email);
        servletRequest.setAttribute("roles", roles);
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response
                .status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"" + message + "\"}")
                .build());
    }
}