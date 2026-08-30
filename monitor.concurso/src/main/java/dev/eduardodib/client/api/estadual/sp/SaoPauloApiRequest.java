package dev.eduardodib.client.api.estadual.sp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "sp-diario")
@Path("/v2/advanced-search/publications")
public interface SaoPauloApiRequest {

    @GET
    SaoPauloApiResponse buscar(
            @QueryParam("periodStartingDate") String periodStartingDate,
            @QueryParam("PageNumber") int pageNumber,
            @QueryParam("Terms[0]") String termo,
            @QueryParam("FromDate") String fromDate,
            @QueryParam("ToDate") String toDate,
            @QueryParam("PageSize") int pageSize,
            @QueryParam("SortField") String sortField
    );
}