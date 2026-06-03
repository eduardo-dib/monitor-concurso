package dev.eduardodib.client.api.estadual.mt;

import dev.eduardodib.client.api.estadual.go.GoiasApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/transparencia/v1")
@RegisterRestClient(configKey = "mt-diario")
public interface MatoGrossoApiRequest {

    @GET
    @Path("/buscas")
    GoiasApiResponse buscar(
            @QueryParam("termo") String termo,
            @QueryParam("data-inicio") String dataInicio,
            @QueryParam("data-fim") String dataFim,
            @QueryParam("pagina") int pagina,
            @QueryParam("limite") int limite
    );
}
