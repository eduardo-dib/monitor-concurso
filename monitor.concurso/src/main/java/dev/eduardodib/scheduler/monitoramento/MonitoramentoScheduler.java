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

@ApplicationScoped
public class MonitoramentoScheduler {

    private static final Logger LOG = Logger.getLogger(MonitoramentoScheduler.class);

    @Inject
    MonitoramentoService monitoramentoService;

    @Inject
    Instance<DiarioOficialScraper> scrapers;

    @Inject
    Instance<DiarioOficialClient> clients;

    @@Scheduled(every = "30s")
    void executar() {
        LOG.info("Iniciando monitoramento...");

        List<AlertaMonitoramentoEntity> alertas = AlertaMonitoramentoEntity.findAtivos();
        LOG.infof("Encontrados %d alertas ativos", alertas.size());

        for (AlertaMonitoramentoEntity alerta : alertas) {
            LOG.infof("Processando alerta: %s | Fonte: %s | Estado: %s", alerta.palavrasChave, alerta.fonte, alerta.estado);

            FonteMonitoramento fonte = alerta.fonte != null ? alerta.fonte : FonteMonitoramento.TODOS;

            if (fonte == FonteMonitoramento.MUNICIPAL || fonte == FonteMonitoramento.TODOS) {
                monitoramentoService.processarAlerta(alerta);
            }

            if (fonte == FonteMonitoramento.ESTADUAL || fonte == FonteMonitoramento.TODOS) {
                // Scrapers
                for (DiarioOficialScraper scraper : scrapers) {
                    if (scraper.getEstado().equalsIgnoreCase(alerta.estado)) {
                        monitoramentoService.processarAlertaComScraper(alerta, scraper);
                    }
                }

                // Clients (APIs)
                for (DiarioOficialClient client : clients) {
                    if (client.getEstado().equalsIgnoreCase(alerta.estado)) {
                        monitoramentoService.processarAlertaComClient(alerta, client);
                    }
                }
            }
        }

        LOG.info("Monitoramento concluído.");
    }
}