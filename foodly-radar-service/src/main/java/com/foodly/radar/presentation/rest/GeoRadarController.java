package com.foodly.radar.presentation.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/radar")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeoRadarController {
    @GET
    @Path("/status")
    public Response getStatus() {
        return Response.ok("{\"message\": \"Foodly Radar Service está activo en la nube local\"}").build();
    }
}