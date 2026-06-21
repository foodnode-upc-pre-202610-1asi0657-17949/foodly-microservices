package com.foodly.business.presentation.rest;

import com.foodly.business.application.dto.HuariqueCreateDto;
import com.foodly.business.application.dto.HuariqueProfileUpdateDto;
import com.foodly.business.application.dto.HuariqueResponseDto;
import com.foodly.business.application.dto.MenuUpdateDto;
import com.foodly.business.application.service.BusinessService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/huariques")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HuariqueController {

    @Inject
    private BusinessService businessService;

    @Context
    private HttpServletRequest servletRequest;

    @GET
    public Response getAllHuariques() {
        List<HuariqueResponseDto> huariques = businessService.getAllHuariques();
        return Response.ok(huariques).build();
    }

    @GET
    @Path("/{id}/menu")
    public Response getHuariqueMenu(@PathParam("id") String id) {
        Optional<HuariqueResponseDto> menuOpt = businessService.getHuariqueMenu(id);
        if (menuOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Huarique no encontrado\"}").build();
        }
        return Response.ok(menuOpt.get()).build();
    }

    @PUT
    @Path("/{id}/menu")
    public Response updateMenu(@PathParam("id") String id, @Valid MenuUpdateDto updateDto) {
        boolean updated = businessService.updateMenu(id, updateDto);
        if (!updated) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No se pudo actualizar, local no encontrado\"}").build();
        }
        return Response.ok("{\"message\":\"Menú actualizado con éxito y evento publicado\"}").build();
    }

    @GET
    @Path("/me")
    public Response getMyHuarique() {
        Response forbidden = requireOwnerRole();
        if (forbidden != null) return forbidden;

        String ownerId = currentUserId();
        Optional<HuariqueResponseDto> huariqueOpt = businessService.getHuariqueByOwner(ownerId);
        if (huariqueOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Todavía no tienes un local registrado\"}").build();
        }
        return Response.ok(huariqueOpt.get()).build();
    }

    @POST
    public Response createHuarique(@Valid HuariqueCreateDto createDto) {
        Response forbidden = requireOwnerRole();
        if (forbidden != null) return forbidden;

        String ownerId = currentUserId();
        Optional<HuariqueResponseDto> created = businessService.createHuarique(ownerId, createDto);
        if (created.isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"Ya tienes un local registrado\"}").build();
        }
        return Response.status(Response.Status.CREATED).entity(created.get()).build();
    }

    @PUT
    @Path("/me")
    public Response updateMyHuarique(@Valid HuariqueProfileUpdateDto updateDto) {
        Response forbidden = requireOwnerRole();
        if (forbidden != null) return forbidden;

        String ownerId = currentUserId();
        Optional<HuariqueResponseDto> updated = businessService.updateProfile(ownerId, updateDto);
        if (updated.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Todavía no tienes un local registrado\"}").build();
        }
        return Response.ok(updated.get()).build();
    }

    private String currentUserId() {
        return (String) servletRequest.getAttribute("username");
    }

    @SuppressWarnings("unchecked")
    private Response requireOwnerRole() {
        List<String> roles = (List<String>) servletRequest.getAttribute("roles");
        if (roles == null || !roles.contains("HUARIQUE_ADMIN")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"Solo los dueños de huarique pueden realizar esta acción\"}").build();
        }
        return null;
    }
}