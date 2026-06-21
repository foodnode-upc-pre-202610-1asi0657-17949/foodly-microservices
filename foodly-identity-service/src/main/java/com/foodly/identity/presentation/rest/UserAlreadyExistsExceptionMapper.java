package com.foodly.identity.presentation.rest;

import com.foodly.identity.application.exception.UserAlreadyExistsException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Provider
public class UserAlreadyExistsExceptionMapper implements ExceptionMapper<UserAlreadyExistsException> {

    @Override
    public Response toResponse(UserAlreadyExistsException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", Response.Status.CONFLICT.getStatusCode());
        body.put("error", "Conflict");
        body.put("message", exception.getMessage());

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}