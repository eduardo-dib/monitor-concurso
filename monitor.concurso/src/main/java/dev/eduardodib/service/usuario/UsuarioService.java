package dev.eduardodib.service.usuario;



import dev.eduardodib.domain.usuario.UsuarioEntity;
import dev.eduardodib.service.email.RecuperacaoSenhaService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import dev.eduardodib.domain.usuario.RecuperacaoSenhaEntity;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class UsuarioService {

    @Inject
    RecuperacaoSenhaService emailService;

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

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

        if (usuario == null) {
            return; // Retorno silencioso para evitar user enumeration
        }

        String token = UUID.randomUUID().toString();

        RecuperacaoSenhaEntity recuperacao = new RecuperacaoSenhaEntity();
        recuperacao.usuario = usuario;
        recuperacao.token = token;
        recuperacao.expiracao = LocalDateTime.now().plusHours(1);
        recuperacao.persist();

        emailService.enviarEmailRecuperacao(usuario.email, token);
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        RecuperacaoSenhaEntity recuperacao = RecuperacaoSenhaEntity.findByToken(token);

        if (recuperacao == null || recuperacao.usado) {
            throw new IllegalArgumentException("Token inválido ou já utilizado.");
        }
        if (recuperacao.expiracao.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado.");
        }

        UsuarioEntity usuario = recuperacao.usuario;
        usuario.senhaHash = BcryptUtil.bcryptHash(novaSenha);
        usuario.persist();

        recuperacao.usado = true;
        recuperacao.persist();
    }
}