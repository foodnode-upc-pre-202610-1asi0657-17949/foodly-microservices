package com.foodly.community.presentation.rest;

import com.foodly.community.application.dto.ReviewRequestDto;
import com.foodly.community.application.dto.ReviewResponseDto;
import com.foodly.community.application.service.CommunityService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/community")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommunityController {

    @Inject
    private CommunityService communityService;

    @GET
    @Path("/huariques/{huariqueId}/reviews")
    public Response getHuariqueReviews(@PathParam("huariqueId") String huariqueId) {
        List<ReviewResponseDto> reviews = communityService.getHuariqueReviews(huariqueId);
        return Response.ok(reviews).build();
    }

    @POST
    @Path("/huariques/{huariqueId}/reviews")
    public Response addReview(@PathParam("huariqueId") String huariqueId,
                              @Context HttpServletRequest request,
                              @Valid ReviewRequestDto reviewDto) {
        String username = (String) request.getAttribute("username");
        ReviewResponseDto response = communityService.addReview(huariqueId, username, reviewDto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/favorites/{huariqueId}")
    public Response addFavorite(@PathParam("huariqueId") String huariqueId, @Context HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        communityService.addFavorite(username, huariqueId);
        return Response.ok("{\"message\":\"Agregado a favoritos con éxito\"}").build();
    }

    @DELETE
    @Path("/favorites/{huariqueId}")
    public Response removeFavorite(@PathParam("huariqueId") String huariqueId, @Context HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        communityService.removeFavorite(username, huariqueId);
        return Response.ok("{\"message\":\"Removido de favoritos con éxito\"}").build();
    }

    @GET
    @Path("/favorites/me")
    public Response getMyFavorites(@Context HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        List<String> favorites = communityService.getCustomerFavorites(username);
        return Response.ok(favorites).build();
    }
}