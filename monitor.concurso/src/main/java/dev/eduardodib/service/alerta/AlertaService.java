package dev.eduardodib.service.alerta;



import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.alertamonitoramento.FonteMonitoramento;
import dev.eduardodib.domain.usuario.UsuarioEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AlertaService {

    @Transactional
    public AlertaMonitoramentoEntity criar(UsuarioEntity usuario, String palavrasChave, String estado, String municipio, String orgao,  FonteMonitoramento fonte) {
        AlertaMonitoramentoEntity alerta = new AlertaMonitoramentoEntity();
        alerta.usuario = usuario;
        alerta.palavrasChave = palavrasChave;
        alerta.estado = estado;
        alerta.municipio = municipio;
        alerta.orgao = orgao;
        alerta.fonte = alerta.fonte != null ? alerta.fonte : FonteMonitoramento.TODOS;
        alerta.persist();
        return alerta;
    }

    public List<AlertaMonitoramentoEntity> listarPorUsuario(UsuarioEntity usuario) {
        return AlertaMonitoramentoEntity.list("usuario", usuario);
    }

    @Transactional
    public boolean deletar(Long id, UsuarioEntity usuario) {
        AlertaMonitoramentoEntity alerta = AlertaMonitoramentoEntity.findById(id);

        if (alerta == null || !alerta.usuario.id.equals(usuario.id)) {
            return false;
        }

        alerta.delete();
        return true;
    }
}