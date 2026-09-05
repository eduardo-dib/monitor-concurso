package dev.eduardodib.resource.monitoramento;


import dev.eduardodib.client.api.estadual.al.AlagoasApiIntegration;
import dev.eduardodib.client.api.estadual.am.AmazonasApiIntegration;
import dev.eduardodib.client.api.estadual.ap.AmapaApiIntegration;
import dev.eduardodib.client.api.estadual.ba.BahiaApiIntegration;
import dev.eduardodib.client.api.estadual.ce.CearaApiIntegration;
import dev.eduardodib.client.api.estadual.df.DistritoFederalApiIntegration;
import dev.eduardodib.client.api.estadual.es.EspiritoSantoApiIntegration;
import dev.eduardodib.client.api.estadual.go.GoiasApiIntegration;
import dev.eduardodib.client.api.estadual.ma.MaranhaoApiIntegration;
import dev.eduardodib.client.api.estadual.mg.MinasGeraisApiIntegration;
import dev.eduardodib.client.api.estadual.ms.MatoGrossoDoSulApiIntegration;
import dev.eduardodib.client.api.estadual.mt.MatoGrossoApiIntegration;
import dev.eduardodib.client.api.estadual.pe.PernambucoApiIntegration;
import dev.eduardodib.client.api.estadual.pi.PiauiApiIntegration;
import dev.eduardodib.client.api.estadual.rn.RioGrandeNorteApiIntegration;
import dev.eduardodib.client.api.estadual.sc.SantaCatarinaApiIntegration;
import dev.eduardodib.client.api.estadual.sp.SaoPauloApiIntegration;
import dev.eduardodib.client.api.municipal.ApiResponse;
import dev.eduardodib.exception.ErrorException;
import dev.eduardodib.scraper.DiarioOficialScraper;
import dev.eduardodib.scraper.ac.AcreScraper;
import dev.eduardodib.scraper.ce.CearaScraper;
import dev.eduardodib.scraper.pa.ParaScraper;
import dev.eduardodib.scraper.parana.DioeParanaScraper;
import dev.eduardodib.scraper.rj.RioDeJaneiroScraper;
import dev.eduardodib.scraper.to.TocantinsScraper;
import dev.eduardodib.service.monitoramento.MonitoramentoService;
import io.quarkus.security.Authenticated;
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
@Authenticated
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

    @Inject
    MinasGeraisApiIntegration minasGeraisApiIntegration;

    @Inject
    RioDeJaneiroScraper rioDeJaneiroScraper;

    @Inject
    SaoPauloApiIntegration saoPauloApiIntegration;

    @Inject
    SantaCatarinaApiIntegration santaCatarinaApiIntegration;

    @Inject
    AcreScraper acreScraper;

    @Inject
    AmapaApiIntegration amapaApiIntegration;

    @Inject
    AmazonasApiIntegration amazonasApiIntegration;

    @Inject
    ParaScraper paraScraper;

    @Inject
    PernambucoApiIntegration pernambucoApiIntegration;

    @Inject
    MatoGrossoDoSulApiIntegration matoGrossoDoSulApiIntegration;

    @Inject
    MatoGrossoApiIntegration matoGrossoApiIntegration;


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

    @GET
    @Path("/buscar-estadual-mg")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualMg(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    minasGeraisApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do MG", "/monitoramento/buscar-estadual-mg", e);
        }
    }

    @GET
    @Path("/buscar-estadual-rj")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualRj(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    rioDeJaneiroScraper.buscar(query, LocalDate.now().minusDays(3).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do RJ", "/monitoramento/buscar-estadual-rj", e);
        }
    }

    @GET
    @Path("/buscar-estadual-sp")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualSp(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    saoPauloApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de SP", "/monitoramento/buscar-estadual-sp", e);
        }
    }

    @GET
    @Path("/buscar-estadual-sc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualSc(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    santaCatarinaApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de SC", "/monitoramento/buscar-estadual-sc", e);
        }
    }

    @GET
    @Path("/buscar-estadual-ac")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualAc(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    acreScraper.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do AC", "/monitoramento/buscar-estadual-ac", e);
        }
    }

    @GET
    @Path("/buscar-estadual-ap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualAp(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    amapaApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do AP", "/monitoramento/buscar-estadual-ap", e);
        }
    }

    @GET
    @Path("/buscar-estadual-am")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualAm(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    amazonasApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do AM", "/monitoramento/buscar-estadual-am", e);
        }
    }

    @GET
    @Path("/buscar-estadual-pa")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualPa(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    paraScraper.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário do PA", "/monitoramento/buscar-estadual-pa", e);
        }
    }

    @GET
    @Path("/buscar-estadual-pe")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualPe(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    pernambucoApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de PE", "/monitoramento/buscar-estadual-pe", e);
        }
    }

    @GET
    @Path("/buscar-estadual-ms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualMs(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    matoGrossoDoSulApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de MS", "/monitoramento/buscar-estadual-ms", e);
        }
    }

    @GET
    @Path("/buscar-estadual-mt")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarEstadualMt(@QueryParam("query") String query) {
        try {
            List<DiarioOficialScraper.PublicacaoScraped> resultado =
                    matoGrossoApiIntegration.buscar(query, LocalDate.now().minusDays(30).toString());
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return ErrorException.internalError("Erro ao buscar no diário de MT", "/monitoramento/buscar-estadual-mt", e);
        }
    }


}