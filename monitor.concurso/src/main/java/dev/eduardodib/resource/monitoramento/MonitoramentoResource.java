package dev.eduardodib.resource.monitoramento;


import dev.eduardodib.client.api.estadual.al.AlagoasApiIntegration;
import dev.eduardodib.client.api.estadual.ba.BahiaApiIntegration;
import dev.eduardodib.client.api.estadual.ce.CearaApiIntegration;
import dev.eduardodib.client.api.estadual.df.DistritoFederalApiIntegration;
import dev.eduardodib.client.api.estadual.es.EspiritoSantoApiIntegration;
import dev.eduardodib.client.api.estadual.go.GoiasApiIntegration;
import dev.eduardodib.client.api.estadual.ma.MaranhaoApiIntegration;
import dev.eduardodib.client.api.estadual.pi.PiauiApiIntegration;
import dev.eduardodib.client.api.estadual.rn.RioGrandeNorteApiIntegration;
import dev.eduardodib.client.api.municipal.ApiResponse;
import dev.eduardodib.exception.ErrorException;
import dev.eduardodib.scraper.DiarioOficialScraper;
import dev.eduardodib.scraper.ce.CearaScraper;
import dev.eduardodib.scraper.parana.DioeParanaScraper;
import dev.eduardodib.scraper.to.TocantinsScraper;
import dev.eduardodib.service.monitoramento.MonitoramentoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jose4j.jwt.consumer.ErrorCodeValidator;

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

    @Inject
    AlagoasApiIntegration alagoasApiIntegration;

    @Inject
    BahiaApiIntegration bahiaApiIntegration;


    @Inject
    CearaApiIntegration cearaApiIntegration;

    @Inject
    MaranhaoApiIntegration maranhaoApiIntegration;

    @Inject
    PiauiApiIntegration piauiApiIntegration;

    @Inject
    RioGrandeNorteApiIntegration rioGrandeNorteApiIntegration;

    @Inject
    TocantinsScraper tocantinsScraper;

    @Inject
    EspiritoSantoApiIntegration espiritoSantoApiIntegration;


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



    @GET
    @Path("/buscar-estadual-al")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualAl(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    alagoasApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de AL", "/monitoramento/buscar-estadual-al", e);
        }
    }

    @GET
    @Path("/buscar-estadual-ba")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualBa(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    bahiaApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário da BA", "/monitoramento/buscar-estadual-ba", e);
        }
    }

    @GET
    @Path("/buscar-estadual-ce")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualCe(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    cearaApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do CE", "/monitoramento/buscar-estadual-ce", e);
        }
    }

    @GET
    @Path("/buscar-estadual-ma")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualMa(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    maranhaoApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do MA", "/monitoramento/buscar-estadual-ma", e);
        }
    }

    @GET
    @Path("/buscar-estadual-pi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualPi(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    piauiApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do PI", "/monitoramento/buscar-estadual-pi", e);
        }
    }

    @GET
    @Path("/buscar-estadual-rn")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualRn(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    rioGrandeNorteApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de RN", "/monitoramento/buscar-estadual-rn", e);
        }
    }

    @GET
    @Path("/buscar-estadual-to")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualTo(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    tocantinsScraper.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de TO", "/monitoramento/buscar-estadual-to", e);
        }
    }

    @GET
    @Path("/buscar-estadual-es")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualEs(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    espiritoSantoApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do ES", "/monitoramento/buscar-estadual-es", e);
        }
    }


}