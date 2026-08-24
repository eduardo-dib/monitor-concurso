package dev.eduardodib.client.api.estadual.mg;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@Path("/api/v1/Autenticacao")
@RegisterRestClient(configKey = "mg-diario")
public interface MinasGeraisAutenticacaoRequest {

    @POST
    @Path("/Autenticar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    MinasGeraisAutenticacaoResponse autenticar(Map<String, Object> corpoVazio);
}
