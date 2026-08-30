package dev.eduardodib.scheduler.monitoramento;




import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.alertamonitoramento.FonteMonitoramento;
import dev.eduardodib.scraper.DiarioOficialScraper;
import dev.eduardodib.service.monitoramento.MonitoramentoService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MonitoramentoScheduler {

    private static final Logger LOG = Logger.getLogger(MonitoramentoScheduler.class);

    private volatile boolean queridoDiarioFalhando = false;

    @Inject
    MonitoramentoService monitoramentoService;

    @Inject
    Instance<DiarioOficialScraper> scrapers;

    @Inject
    Instance<DiarioOficialClient> clients;


    @Scheduled(every = "{monitoramento.intervalo-busca}")
    void executar() {
        List<AlertaMonitoramentoEntity> alertas = AlertaMonitoramentoEntity.findAtivos();

        for (AlertaMonitoramentoEntity alerta : alertas) {
            FonteMonitoramento fonte = alerta.fonte != null ? alerta.fonte : FonteMonitoramento.TODOS;

            if (fonte == FonteMonitoramento.MUNICIPAL || fonte == FonteMonitoramento.TODOS) {
                try {
                    monitoramentoService.processarAlerta(alerta); // Querido Diário
                    if (queridoDiarioFalhando) {
                        LOG.info("Querido Diário voltou a responder normalmente");
                        queridoDiarioFalhando = false;
                    }
                } catch (Exception e) {
                    if (!queridoDiarioFalhando) {
                        LOG.errorf(e, "Erro ao processar alerta %d via Querido Diário (municipal) — suprimindo logs repetidos até normalizar", alerta.id);
                        queridoDiarioFalhando = true;
                    }
                }
            }

            if (fonte == FonteMonitoramento.ESTADUAL || fonte == FonteMonitoramento.TODOS) {
                for (DiarioOficialScraper scraper : scrapers) {
                    if (scraper.getEstado().equalsIgnoreCase(alerta.estado)) {
                        try {
                            monitoramentoService.processarAlertaComScraper(alerta, scraper);
                        } catch (Exception e) {
                            LOG.errorf(e, "Erro ao processar alerta %d via scraper %s", alerta.id, scraper.getEstado());
                        }
                    }
                }
                for (DiarioOficialClient client : clients) {
                    if (client.getEstado().equalsIgnoreCase(alerta.estado)) {
                        try {
                            monitoramentoService.processarAlertaComClient(alerta, client);
                        } catch (Exception e) {
                            LOG.errorf(e, "Erro ao processar alerta %d via client %s", alerta.id, client.getEstado());
                        }
                    }
                }
            }
        }
    }
}