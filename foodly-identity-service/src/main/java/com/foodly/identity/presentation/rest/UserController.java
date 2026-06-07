package com.foodly.identity.presentation.rest;

import com.foodly.identity.application.dto.UserProfileDto;
import com.foodly.identity.application.service.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Inject
    private AuthService authService;

    @GET
    @Path("/me")
    public Response getMyProfile(@Context HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        log.debug("[UserController] GET /api/users/me | userId={}", userId);
        try {
            UserProfileDto profile = authService.getUserProfile(userId);
            return Response.ok(profile).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getLocalizedMessage()).build();
        }
    }
}