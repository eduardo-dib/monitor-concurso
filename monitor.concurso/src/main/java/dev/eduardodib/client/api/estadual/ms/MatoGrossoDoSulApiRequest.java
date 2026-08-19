package dev.eduardodib.client.api.estadual.ms;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/diarios")
@RegisterRestClient(configKey = "ms-diario")
public interface MatoGrossoDoSulApiRequest {

    @GET
    @Path("/busca-diarios")
    MatoGrossoDoSulApiResponse buscar(
            @QueryParam("tipo") int tipo,
            @QueryParam("texto") String texto,
            @QueryParam("dataInicial") String dataInicial,
            @QueryParam("dataFinal") String dataFinal,
            @QueryParam("pagina") int pagina,
            @QueryParam("registrosPorPagina") int registrosPorPagina
    );
}
