package dev.eduardodib.domain.publicacaoencontrada;

import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "publicacao_encontrada")
public class PublicacaoEncontradaEntity extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "alerta_id")
    public AlertaMonitoramentoEntity alerta;

    @Column(columnDefinition = "TEXT")
    public String conteudo;

    public LocalDate dataPublicacao;
    public String link;
    public String territorio;

    public static List<PublicacaoEncontradaEntity> findByAlerta(AlertaMonitoramentoEntity alerta) {
        return list("alerta", alerta);
    }
}
