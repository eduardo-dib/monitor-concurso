package dev.eduardodib.domain.usuario;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class UsuarioEntity extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String senhaHash;

    @Column(nullable = false)
    public String nome;

    @Column(name = "email_verificado", nullable = false)
    public boolean emailVerificado = false;

    public LocalDateTime criadoEm = LocalDateTime.now();

    public static UsuarioEntity findByEmail(String email) {
        return find("email", email).firstResult();
    }
}