package dev.eduardodib.service.monitoramento;



import dev.eduardodib.client.api.ApiRequest;
import dev.eduardodib.client.api.ApiResponse;
import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.publicacaoencontrada.PublicacaoEncontradaEntity;
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

    public ApiResponse buscarPublicacoes(String query, String estado) {
        return buscarPublicacoes(query, estado, 0);
    }

    private static final int PAGE_SIZE = 20;

    public ApiResponse buscarPublicacoes(String query, String estado, int offset) {
        return queridoDiarioClient.buscar(query, estado, PAGE_SIZE, offset);
    }

    @Transactional
    public void processarAlerta(AlertaMonitoramentoEntity alerta) {
        List<PublicacaoEncontradaEntity> novas = new ArrayList<>();
        int offset = 0;
        int total = Integer.MAX_VALUE;

        while (offset < total) {
            ApiResponse response = buscarPublicacoes(alerta.palavrasChave, alerta.estado, offset);

            if (response == null || response.gazettes == null || response.gazettes.isEmpty()) break;

            total = response.total;
            LOG.infof("Buscando '%s': offset %d de %d total", alerta.palavrasChave, offset, total);

            for (ApiResponse.Gazette gazette : response.gazettes) {
                boolean jaExiste = PublicacaoEncontradaEntity
                        .count("link = ?1 and alerta = ?2", gazette.url, alerta) > 0;

                if (jaExiste) {
                    LOG.infof("Publicação já registrada: %s", gazette.url);
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
}