package dev.eduardodib.client.api.estadual.es;

import dev.eduardodib.client.api.estadual.go.GoiasApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/transparencia/v1")
@RegisterRestClient(configKey = "es-diario")
public interface EspiritoSantoApiRequest {

    @GET
    @Path("/buscas")
    @Produces(MediaType.APPLICATION_JSON)
    GoiasApiResponse buscar(
            @QueryParam("termo") String termo,
            @QueryParam("pagina") int pagina,
            @QueryParam("limite") int limite,
            @QueryParam("data-inicio") String dataInicio,
            @QueryParam("data-fim") String dataFim
    );

    @GET
    @Path("/diarios/{diarioId}/edicoes")
    @Produces(MediaType.APPLICATION_JSON)
    List<EdicaoDTO> buscarEdicoes(
            @PathParam("diarioId") long diarioId,
            @QueryParam("data") String data,
            @QueryParam("edicao") String edicao
    );
}
