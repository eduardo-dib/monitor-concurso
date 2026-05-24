package dev.eduardodib.service.token;


import dev.eduardodib.domain.usuario.UsuarioEntity;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;

@ApplicationScoped
public class TokenService {

    public String gerarToken(UsuarioEntity usuario) {
        return Jwt.issuer("concurso-monitor")
                .subject(usuario.email)
                .claim("id", usuario.id)
                .claim("nome", usuario.nome)
                .expiresIn(Duration.ofHours(8))
                .sign();
    }
}