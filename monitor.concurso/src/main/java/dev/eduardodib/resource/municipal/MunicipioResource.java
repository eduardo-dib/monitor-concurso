package dev.eduardodib.resource.municipal;

import dev.eduardodib.service.municipio.MunicipioQueridoDiarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/municipios")
@Produces(MediaType.APPLICATION_JSON)
public class MunicipioResource {

    @Inject
    MunicipioQueridoDiarioService municipioService;

    @GET
    public Response listar(@QueryParam("estado") String estado) {
        var lista = (estado != null && !estado.isBlank())
                ? municipioService.listarPorEstado(estado)
                : municipioService.listarTodos();
        return Response.ok(lista).build();
    }
}