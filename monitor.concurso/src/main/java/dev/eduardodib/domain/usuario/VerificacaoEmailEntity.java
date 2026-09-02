package dev.eduardodib.domain.usuario;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "verificacao_email")
public class VerificacaoEmailEntity extends PanacheEntity {
    public String codigo;
    @ManyToOne
    public UsuarioEntity usuario;

    @Column(name = "criado_em")
    public LocalDateTime criadoEm = LocalDateTime.now();
    public LocalDateTime expiracao;
    public boolean usado = false;

    public static VerificacaoEmailEntity findValido(UsuarioEntity usuario, String codigoHash) {
        return find("usuario = ?1 and codigo = ?2 and usado = false", usuario, codigoHash).firstResult();
    }

    public static void invalidarCodigosAtivos(UsuarioEntity usuario) {
        update("usado = true where usuario = ?1 and usado = false", usuario);
    }
}