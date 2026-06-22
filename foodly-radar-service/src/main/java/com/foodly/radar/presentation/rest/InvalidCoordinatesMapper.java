package com.foodly.radar.presentation.rest;

import com.foodly.radar.application.exception.InvalidCoordinatesException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ExceptionMapper;

@Provider
public class InvalidCoordinatesMapper implements ExceptionMapper<InvalidCoordinatesException> {

    @Override
    public Response toResponse(InvalidCoordinatesException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"Validación GeoRadar: " + exception.getMessage() + "\"}")
                .build();
    }
}