package dev.eduardodib.scheduler.monitoramento;




import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
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
        LOG.info("Iniciando monitoramento.");

        List<AlertaMonitoramentoEntity> alertas = AlertaMonitoramentoEntity.findAtivos();
        LOG.infof("Encontrados %d alertas ativos", alertas.size());

        for (AlertaMonitoramentoEntity alerta : alertas) {
            LOG.infof("Processando alerta: %s", alerta.palavrasChave);
            monitoramentoService.processarAlerta(alerta);
        }

        LOG.info("Monitoramento concluído.");
    }
}