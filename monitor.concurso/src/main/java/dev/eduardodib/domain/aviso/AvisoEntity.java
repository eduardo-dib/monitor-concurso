package dev.eduardodib.domain.aviso;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "aviso")
public class AvisoEntity extends PanacheEntity {

    public String titulo;

    public String mensagem;

    @Enumerated(EnumType.STRING)
    public TipoAviso tipo;

    public boolean ativo = true;

    @Column(name = "criado_em")
    public LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "expira_em")
    public LocalDateTime expiraEm;

    public static List<AvisoEntity> findAtivos() {
        return list(
                "ativo = true and (expiraEm is null or expiraEm > ?1) order by criadoEm desc",
                LocalDateTime.now()
        );
    }
}