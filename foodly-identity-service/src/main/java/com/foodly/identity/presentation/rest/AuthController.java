package com.foodly.identity.presentation.rest;

import com.foodly.identity.application.dto.AuthResponseDto;
import com.foodly.identity.application.dto.LoginRequestDto;
import com.foodly.identity.application.dto.RegisterRequestDto;
import com.foodly.identity.application.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Inject
    private AuthService authService;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequestDto request) {
        log.info("[AuthController] POST /api/auth/register | email={}", request.getEmail());
        try {
            AuthResponseDto response = authService.register(request);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getLocalizedMessage()).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequestDto request) {
        log.info("[AuthController] POST /api/auth/login | email={}", request.getEmail());
        try {
            AuthResponseDto response = authService.login(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getLocalizedMessage()).build();
        }
    }
}