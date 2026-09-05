package dev.eduardodib.resource.aviso;

import dev.eduardodib.domain.aviso.AvisoEntity;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/avisos")
@Produces(MediaType.APPLICATION_JSON)
public class AvisoResource {

    @GET
    public Response listar() {
        List<AvisoEntity> avisos = AvisoEntity.findAtivos();
        return Response.ok(avisos).build();
    }
}
