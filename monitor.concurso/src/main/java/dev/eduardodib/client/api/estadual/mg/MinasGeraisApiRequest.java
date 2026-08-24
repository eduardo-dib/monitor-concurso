package dev.eduardodib.client.api.estadual.mg;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v1/Pesquisa")
@RegisterRestClient(configKey = "mg-diario")
public interface MinasGeraisApiRequest {

    @GET
    @Path("/PesquisarJornaisPaginados")
    @Produces(MediaType.APPLICATION_JSON)
    MinasGeraisApiResponse buscar(
            @HeaderParam("Authorization") String authorization,
            @QueryParam("DataPublicacaoInicial") String dataInicial,
            @QueryParam("DataPublicacaoFinal") String dataFinal,
            @QueryParam("TextoPesquisa") String textoPesquisa,
            @QueryParam("DiarioExecutivo") boolean diarioExecutivo,
            @QueryParam("DiarioMunicipios") boolean diarioMunicipios,
            @QueryParam("DiarioTerceiros") boolean diarioTerceiros,
            @QueryParam("EdicaoExtra") boolean edicaoExtra,
            @QueryParam("PaginaAtual") int paginaAtual,
            @QueryParam("TamanhoPagina") int tamanhoPagina
    );
}
