package dev.eduardodib.client.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/gazettes")
@RegisterRestClient(configKey = "querido-diario")
public interface ApiRequest {

    @GET
    ApiResponse buscar(
            @QueryParam("querystring") String query,
            @QueryParam("state") String estado,
            @QueryParam("size") int size,
            @QueryParam("offset") int offset,
            @QueryParam("published_since") String publishedSince
    );
}