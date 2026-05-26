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
        return queridoDiarioClient.buscar(query, estado, 5);
    }

    @Transactional
    public void processarAlerta(AlertaMonitoramentoEntity alerta) {
        ApiResponse response = buscarPublicacoes(alerta.palavrasChave, alerta.estado);
        LOG.infof("Resposta da API para '%s': %d publicações", alerta.palavrasChave, response != null && response.gazettes != null ? response.gazettes.size() : 0);

        if (response == null || response.gazettes == null) return;

        List<PublicacaoEncontradaEntity> novas = new ArrayList<>();

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
                publicacao.conteudo = gazette.trechosDestacados.get(0);
            }

            publicacao.persist();
            novas.add(publicacao);
            LOG.infof("Nova publicação salva: %s", gazette.url);
        }

        if (!novas.isEmpty()) {
            notificacaoService.notificarMatches(alerta, novas);
        }
    }
}