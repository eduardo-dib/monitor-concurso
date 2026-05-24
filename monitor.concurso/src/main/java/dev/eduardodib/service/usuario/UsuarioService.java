package dev.eduardodib.service.usuario;



import dev.eduardodib.domain.usuario.UsuarioEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UsuarioService {

    @Transactional
    public UsuarioEntity cadastrar(String nome, String email, String senha) {
        if (UsuarioEntity.findByEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.nome = nome;
        usuario.email = email;
        usuario.senhaHash = BcryptUtil.bcryptHash(senha);
        usuario.persist();

        return usuario;
    }

    public boolean verificarSenha(String senha, String senhaHash) {
        return BcryptUtil.matches(senha, senhaHash);
    }
}