package dev.eduardodib.domain.alertamonitoramento;

import dev.eduardodib.domain.usuario.UsuarioEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "alerta_monitoramento")
public class AlertaMonitoramentoEntity extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    public UsuarioEntity usuario;

    public String palavrasChave;
    public String estado;
    public String municipio;
    public String orgao;
    public boolean ativo = true;

    public static List<AlertaMonitoramentoEntity> findAtivos() {
        return list("ativo", true);
    }
}
