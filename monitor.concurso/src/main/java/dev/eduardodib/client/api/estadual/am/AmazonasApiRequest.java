package dev.eduardodib.client.api.estadual.am;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "am-diario")
@Path("/apibusca/multidiarios/busca")
public interface AmazonasApiRequest {

    @GET
    AmazonasApiResponse buscar(
            @QueryParam("page") int page,
            @QueryParam("q") String query,
            @QueryParam("exata") int exata,
            @QueryParam("data_init") String dataInit,
            @QueryParam("data_end") String dataEnd
    );
}
