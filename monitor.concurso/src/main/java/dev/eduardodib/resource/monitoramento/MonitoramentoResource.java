package dev.eduardodib.resource.monitoramento;


import dev.eduardodib.client.api.ApiResponse;
import dev.eduardodib.service.monitoramento.MonitoramentoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/monitoramento")
public class MonitoramentoResource {

    @Inject
    MonitoramentoService monitoramentoService;

    @GET
    @Path("/buscar")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse buscar(
            @QueryParam("query") String query,
            @QueryParam("estado") String estado
    ) {
        return monitoramentoService.buscarPublicacoes(query, estado);
    }
}