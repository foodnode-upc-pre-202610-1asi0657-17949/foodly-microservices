package com.foodly.business.presentation.rest;

import com.foodly.business.application.dto.HuariqueResponseDto;
import com.foodly.business.application.dto.MenuUpdateDto;
import com.foodly.business.application.service.BusinessService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
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
}