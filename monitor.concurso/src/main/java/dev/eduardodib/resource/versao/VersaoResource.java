package dev.eduardodib.resource.versao;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/versao")
@Produces(MediaType.APPLICATION_JSON)
public class VersaoResource {

    @ConfigProperty(name = "quarkus.application.version", defaultValue = "dev")
    String versao;

    @GET
    public Response versao() {
        return Response.ok(Map.of("versao", versao)).build();
    }
}