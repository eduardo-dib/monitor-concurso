package dev.eduardodib.client.api.estadual.pe;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "pe-diario")
@Path("/diariooficial/public/search")
@Consumes(MediaType.APPLICATION_JSON)
public interface PernambucoApiRequest {

    @POST
    PernambucoApiResponse buscar(PernambucoSearchBody body);
}
