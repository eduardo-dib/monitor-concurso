package dev.eduardodib.scheduler.monitoramento;




import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.alertamonitoramento.FonteMonitoramento;
import dev.eduardodib.service.monitoramento.MonitoramentoService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.util.List;

@ApplicationScoped
public class MonitoramentoScheduler {

    private static final Logger LOG = Logger.getLogger(MonitoramentoScheduler.class);

    @Inject
    MonitoramentoService monitoramentoService;

    @Scheduled(every = "1h")
    void executar() {
        LOG.info("Iniciando monitoramento...");

        List<AlertaMonitoramentoEntity> alertas = AlertaMonitoramentoEntity.findAtivos();
        LOG.infof("Encontrados %d alertas ativos", alertas.size());

        for (AlertaMonitoramentoEntity alerta : alertas) {
            LOG.infof("Processando alerta: %s | Fonte: %s", alerta.palavrasChave, alerta.fonte);

            String fonte = alerta.fonte != null ? alerta.fonte : "TODOS";
            if (fonte.equals(FonteMonitoramento.MUNICIPAL) || fonte.equals(FonteMonitoramento.TODOS)) {
                monitoramentoService.processarAlerta(alerta);
            }

            if (fonte.equals(FonteMonitoramento.ESTADUAL) || fonte.equals(FonteMonitoramento.TODOS)) {
                monitoramentoService.processarAlertaEstadual(alerta);
            }
        }

        LOG.info("Monitoramento concluído.");
    }
}