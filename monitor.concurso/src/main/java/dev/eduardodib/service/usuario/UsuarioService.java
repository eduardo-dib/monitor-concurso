package dev.eduardodib.service.usuario;



import dev.eduardodib.domain.usuario.UsuarioEntity;
import dev.eduardodib.domain.usuario.VerificacaoEmailEntity;
import dev.eduardodib.exception.BetaLotadoException;
import dev.eduardodib.service.email.RecuperacaoSenhaService;
import dev.eduardodib.service.email.VerificacaoEmailService;
import dev.eduardodib.util.HashUtil;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import dev.eduardodib.domain.usuario.RecuperacaoSenhaEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class UsuarioService {

    @Inject
    RecuperacaoSenhaService emailService;

    @Inject
    VerificacaoEmailService verificacaoEmailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @ConfigProperty(name = "app.beta.max-usuarios")
    int betaMaxUsuarios;

   @Transactional
   public UsuarioEntity cadastrar(String nome, String email, String senha) {
       UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

       if (usuario != null && usuario.emailVerificado) {
           throw new IllegalArgumentException("E-mail já cadastrado");
       }

       if (usuario == null) {
           if (betaMaxUsuarios >= 0 && UsuarioEntity.count() >= betaMaxUsuarios) {
               throw new BetaLotadoException("As vagas do período de testes estão esgotadas no momento.");
           }
           usuario = new UsuarioEntity();
           usuario.email = email;
       }

       usuario.nome = nome;
       usuario.senhaHash = BcryptUtil.bcryptHash(senha);
       usuario.persist();

       enviarCodigoVerificacao(usuario);

       return usuario;
   }

    @Transactional
    public void enviarCodigoVerificacao(UsuarioEntity usuario) {
        VerificacaoEmailEntity.invalidarCodigosAtivos(usuario);

        String codigoBruto = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        String codigoHash = HashUtil.sha256(codigoBruto);

        VerificacaoEmailEntity verificacao = new VerificacaoEmailEntity();
        verificacao.usuario = usuario;
        verificacao.codigo = codigoHash;
        verificacao.expiracao = LocalDateTime.now().plusMinutes(15);
        verificacao.persist();

        verificacaoEmailService.enviarCodigoVerificacao(usuario.email, codigoBruto);
    }

    @Transactional
    public void verificarEmail(String email, String codigo) {
        UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

        if (usuario == null || usuario.emailVerificado) {
            throw new IllegalArgumentException("Código inválido ou expirado.");
        }

        String codigoHash = HashUtil.sha256(codigo);
        VerificacaoEmailEntity verificacao = VerificacaoEmailEntity.findValido(usuario, codigoHash);

        if (verificacao == null || verificacao.expiracao.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Código inválido ou expirado.");
        }

        usuario.emailVerificado = true;
        usuario.persist();

        verificacao.usado = true;
        verificacao.persist();
    }

    @Transactional
    public void reenviarCodigoVerificacao(String email) {
        UsuarioEntity usuario = UsuarioEntity.findByEmail(email);
        if (usuario == null || usuario.emailVerificado) {
            return;
        }
        enviarCodigoVerificacao(usuario);
    }

    public boolean verificarSenha(String senha, String senhaHash) {
        return BcryptUtil.matches(senha, senhaHash);
    }

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        UsuarioEntity usuario = UsuarioEntity.findByEmail(email);
        if (usuario == null) {
            return;
        }

        RecuperacaoSenhaEntity.invalidarTokensAtivos(usuario);

        String tokenBruto = UUID.randomUUID().toString();
        String tokenHash = HashUtil.sha256(tokenBruto);

        RecuperacaoSenhaEntity recuperacao = new RecuperacaoSenhaEntity();
        recuperacao.usuario = usuario;
        recuperacao.token = tokenHash;
        recuperacao.expiracao = LocalDateTime.now().plusHours(1);
        recuperacao.persist();

        emailService.enviarEmailRecuperacao(usuario.email, tokenBruto);
    }

    @Transactional
    public void redefinirSenha(String tokenBruto, String novaSenha) {
        String tokenHash = HashUtil.sha256(tokenBruto);
        RecuperacaoSenhaEntity recuperacao = RecuperacaoSenhaEntity.findByToken(tokenHash);

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