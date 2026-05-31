package dev.eduardodib.service.monitoramento;



import dev.eduardodib.client.api.ApiRequest;
import dev.eduardodib.client.api.ApiResponse;
import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.publicacaoencontrada.PublicacaoEncontradaEntity;
import dev.eduardodib.scraper.DiarioOficialScraper;
import dev.eduardodib.scraper.parana.DioeParanaScraper;
import dev.eduardodib.service.notificacao.NotificacaoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MonitoramentoService {

    private static final Logger LOG = Logger.getLogger(MonitoramentoService.class);

    @Inject
    @RestClient
    ApiRequest queridoDiarioClient;

    @Inject
    NotificacaoService notificacaoService;

    @Inject
    DioeParanaScraper dioeParanaScraper;

    public ApiResponse buscarPublicacoes(String query, String estado) {
        return buscarPublicacoes(query, estado, 0, LocalDate.now().minusDays(30).toString());
    }

    private static final int PAGE_SIZE = 20;

    private ApiResponse buscarPublicacoes(String query, String estado, int offset, String publishedSince) {
        return queridoDiarioClient.buscar(query, estado, PAGE_SIZE, offset, publishedSince);
    }

    @Transactional
    public void processarAlerta(AlertaMonitoramentoEntity alerta) {
        List<PublicacaoEncontradaEntity> novas = new ArrayList<>();
        int offset = 0;
        int total = Integer.MAX_VALUE;
        String publishedSince = alerta.criadoEm != null
                ? alerta.criadoEm.toLocalDate().toString()
                : LocalDate.now().minusDays(30).toString();

        while (offset < total) {



            ApiResponse response = buscarPublicacoes(alerta.palavrasChave, alerta.estado, offset, publishedSince);



            if (response == null || response.gazettes == null || response.gazettes.isEmpty()) break;

            total = response.total;
            LOG.infof("Buscando '%s': offset %d de %d total", alerta.palavrasChave, offset, total);

            for (ApiResponse.Gazette gazette : response.gazettes) {
                if (alerta.estado != null && !alerta.estado.isEmpty()
                        && !gazette.estado.equalsIgnoreCase(alerta.estado)) {
                    continue;
                }
                boolean jaExiste = PublicacaoEncontradaEntity
                        .count("link = ?1 and alerta = ?2", gazette.url, alerta) > 0;

                if (jaExiste) {
                    LOG.infof("Publicação já registrada: %s", gazette.url);
                    continue;
                }

                if (alerta.estado != null && !alerta.estado.isEmpty()
                        && !gazette.estado.equalsIgnoreCase(alerta.estado)) {
                    continue;
                }

                PublicacaoEncontradaEntity publicacao = new PublicacaoEncontradaEntity();
                publicacao.alerta = alerta;
                publicacao.link = gazette.url;
                publicacao.dataPublicacao = LocalDate.parse(gazette.date);
                publicacao.territorio = gazette.territorioId;

                if (gazette.trechosDestacados != null && !gazette.trechosDestacados.isEmpty()) {
                    publicacao.conteudo = gazette.trechosDestacados.getFirst();
                }

                publicacao.persist();
                novas.add(publicacao);
                LOG.infof("Nova publicação salva: %s", gazette.url);
            }

            offset += PAGE_SIZE;
        }

        if (!novas.isEmpty()) {
            notificacaoService.notificarMatches(alerta, novas);
        }
    }

    @Transactional
    public void processarAlertaEstadual(AlertaMonitoramentoEntity alerta) {
        String dataInicio = alerta.criadoEm != null
                ? alerta.criadoEm.toLocalDate().toString()
                : LocalDate.now().minusDays(30).toString();

        List<DiarioOficialScraper.PublicacaoScraped> publicacoes = dioeParanaScraper.buscar(alerta.palavrasChave, dataInicio);
        List<PublicacaoEncontradaEntity> novas = new ArrayList<>();

        for (DiarioOficialScraper.PublicacaoScraped scraped : publicacoes) {
            boolean jaExiste = PublicacaoEncontradaEntity
                    .count("link = ?1 and alerta = ?2", scraped.link(), alerta) > 0;

            if (jaExiste) {
                LOG.infof("Publicação estadual já registrada: %s", scraped.link());
                continue;
            }

            PublicacaoEncontradaEntity publicacao = new PublicacaoEncontradaEntity();
            publicacao.alerta = alerta;
            publicacao.link = scraped.link();
            publicacao.conteudo = scraped.conteudo();
            publicacao.territorio = "PR";

            try {
                publicacao.dataPublicacao = LocalDate.parse(scraped.data(),
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                publicacao.dataPublicacao = LocalDate.now();
            }

            publicacao.persist();
            novas.add(publicacao);
            LOG.infof("Nova publicação estadual salva: %s", scraped.link());
        }

        if (!novas.isEmpty()) {
            notificacaoService.notificarMatches(alerta, novas);
        }
    }
}