package dev.eduardodib.resource.monitoramento;


import dev.eduardodib.client.api.estadual.df.DistritoFederalApiIntegration;
import dev.eduardodib.client.api.estadual.go.GoiasApiIntegration;
import dev.eduardodib.client.api.municipal.ApiResponse;
import dev.eduardodib.exception.ErrorException;
import dev.eduardodib.scraper.DiarioOficialScraper;
import dev.eduardodib.scraper.parana.DioeParanaScraper;
import dev.eduardodib.service.monitoramento.MonitoramentoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/monitoramento")
public class MonitoramentoResource {

    @Inject
    MonitoramentoService monitoramentoService;

    @Inject
    DioeParanaScraper dioeParanaScraper;

    @Inject
    GoiasApiIntegration goiasApiIntegration;

    @Inject
    DistritoFederalApiIntegration distritoFederalApiIntegration;

    @GET
    @Path("/buscar")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse buscar(
            @QueryParam("query") String query,
            @QueryParam("estado") String estado
    ) {
        return monitoramentoService.buscarPublicacoes(query, estado);
    }

    @GET
    @Path("/buscar-estadual-pr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadual(
            @QueryParam("query") String query,
            @QueryParam("estado") String estado
    ) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    dioeParanaScraper.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário estadual", "/monitoramento/buscar-estadual", e);
        }
    }

    ;

    @GET
    @Path("/buscar-estadual-go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualGo(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    goiasApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário estadual GO", "/monitoramento/buscar-estadual-go", e);
        }
    }

    @GET
    @Path("/buscar-estadual-df")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualDf(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    distritoFederalApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do DF", "/monitoramento/buscar-estadual-df", e);
        }
    }


}