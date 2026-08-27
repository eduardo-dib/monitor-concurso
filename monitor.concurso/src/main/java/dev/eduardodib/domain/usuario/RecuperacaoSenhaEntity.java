package dev.eduardodib.domain.usuario;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "recuperacao_senha")
public class RecuperacaoSenhaEntity extends PanacheEntity {

    public String token;

    @ManyToOne
    public UsuarioEntity usuario;

    public LocalDateTime criadoEm = LocalDateTime.now();
    public LocalDateTime expiracao;
    public boolean usado = false;

    public static RecuperacaoSenhaEntity findByToken(String token) {
        return find("token", token).firstResult();
    }
}
