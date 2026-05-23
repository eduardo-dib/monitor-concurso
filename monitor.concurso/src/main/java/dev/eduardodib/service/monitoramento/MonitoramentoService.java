package dev.eduardodib.service.monitoramento;



import dev.eduardodib.client.api.ApiRequest;
import dev.eduardodib.client.api.ApiResponse;
import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.publicacaoencontrada.PublicacaoEncontradaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import java.time.LocalDate;

@ApplicationScoped
public class MonitoramentoService {

    private static final Logger LOG = Logger.getLogger(MonitoramentoService.class);

    @Inject
    @RestClient
    ApiRequest queridoDiarioClient;

    public ApiResponse buscarPublicacoes(String query, String estado) {
        return queridoDiarioClient.buscar(query, estado, 5);
    }

    @Transactional
    public void processarAlerta(AlertaMonitoramentoEntity alerta) {
        ApiResponse response = buscarPublicacoes(alerta.palavrasChave, alerta.estado);

        if (response == null || response.gazettes == null) return;

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
            LOG.infof("Nova publicação salva: %s", gazette.url);
        }
    }
}