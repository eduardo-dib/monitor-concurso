package dev.eduardodib.resource.usuario;


import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.publicacaoencontrada.PublicacaoEncontradaEntity;
import dev.eduardodib.domain.usuario.CadastroUsuarioResponseDTO;
import dev.eduardodib.domain.usuario.LoginResponseDTO;
import dev.eduardodib.domain.usuario.RecuperacaoSenhaEntity;
import dev.eduardodib.domain.usuario.UsuarioEntity;
import dev.eduardodib.service.ratelimit.RateLimiterService;
import dev.eduardodib.service.token.TokenService;
import dev.eduardodib.service.usuario.UsuarioService;
import dev.eduardodib.exception.ErrorException;
import dev.eduardodib.util.IpUtil;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.Authenticated;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.ws.rs.*;

import java.time.Duration;
import java.util.List;


@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    TokenService tokenService;

    @Inject
    RateLimiterService rateLimiterService;

    @Context
    HttpServerRequest httpRequest;


    public record CadastroRequest(String nome, String email, String senha) {}
    public record LoginRequest(String email, String senha) {}
    public record SolicitarRecuperacaoRequest(String email) {}
    public record RedefinirSenhaRequest(String token, String novaSenha) {}
    public record ExclusaoContaRequest(String senha) {}
    public record VerificarEmailRequest(String email, String codigo) {}
    public record ReenviarCodigoRequest(String email) {}

    @POST
    @Path("/cadastrar")
    public Response cadastrar(CadastroRequest request) {

        String usuarioCadastrarUrl = "/usuarios/cadastrar";
        try {
            UsuarioEntity usuario = usuarioService.cadastrar(request.nome(), request.email(), request.senha());
            return Response.status(Response.Status.CREATED)
                    .entity(CadastroUsuarioResponseDTO.fromEntity(usuario))
                    .build();
        } catch (IllegalArgumentException e) {
            return ErrorException.conflict(e.getMessage(), usuarioCadastrarUrl, e);
        }
    }

    @ConfigProperty(name = "app.jwt.cookie.secure")
    boolean cookieSecure;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        UsuarioEntity usuario = UsuarioEntity.findByEmail(request.email());
        if (usuario == null || !BcryptUtil.matches(request.senha(), usuario.senhaHash)) {
            return ErrorException.unauthorized("Credenciais inválidas", "/usuarios/login");
        }
        if (!usuario.emailVerificado) {
            return ErrorException.unauthorized("E-mail ainda não verificado. Confira sua caixa de entrada.", "/usuarios/login");
        }

        String token = tokenService.gerarToken(usuario);

        NewCookie cookie = new NewCookie.Builder("token")
                .value(token)
                .path("/")
                .maxAge(8 * 60 * 60)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        return Response.ok(new LoginSucesso(usuario.nome)).cookie(cookie).build();
    }

    public record LoginSucesso(String nome) {}

    @POST
    @Path("/logout")
    public Response logout() {
        NewCookie cookie = new NewCookie.Builder("token")
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        return Response.ok().cookie(cookie).build();
    }

    @GET
    @Path("/me")
    @Authenticated
    public Response me(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

        if (usuario == null) {
            return ErrorException.unauthorized("Usuário não encontrado", "/usuarios/me");
        }

        return Response.ok(CadastroUsuarioResponseDTO.fromEntity(usuario)).build();
    }

    @POST
    @Path("/esqueci-senha")
    public Response esqueciSenha(SolicitarRecuperacaoRequest request) {
        String ip = IpUtil.extrair(httpRequest);

        if (!rateLimiterService.permitir("esqueci-senha:ip:" + ip, 10, Duration.ofHours(1))) {
            return ErrorException.tooManyRequests("Muitas tentativas. Tente novamente mais tarde.", "/usuarios/esqueci-senha");
        }

        String chaveEmail = "esqueci-senha:email:" + request.email().toLowerCase();
        if (!rateLimiterService.permitir(chaveEmail, 3, Duration.ofHours(1))) {

            return Response.ok().entity("Se o e-mail existir, um link de recuperação foi enviado.").build();
        }

        usuarioService.solicitarRecuperacaoSenha(request.email());
        return Response.ok().entity("Se o e-mail existir, um link de recuperação foi enviado.").build();
    }

    @POST
    @Path("/redefinir-senha")
    public Response redefinirSenha(RedefinirSenhaRequest request) {
        try {
            usuarioService.redefinirSenha(request.token(), request.novaSenha());
            return Response.ok().entity("Senha redefinida com sucesso.").build();
        } catch (IllegalArgumentException e) {
            return ErrorException.badRequest(e.getMessage(), "/usuarios/redefinir-senha");
        }
    }

    @DELETE
    @Path("/me")
    @Authenticated
    @Transactional
    public Response excluirConta(ExclusaoContaRequest request, @Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

        if (usuario == null) {
            return ErrorException.notFound("Usuário não encontrado", "/usuarios/me");
        }

        if (request == null || request.senha() == null || request.senha().isBlank()) {
            return ErrorException.badRequest("Senha é obrigatória para confirmar a exclusão", "/usuarios/me");
        }

        if (!BcryptUtil.matches(request.senha(), usuario.senhaHash)) {
            return ErrorException.unauthorized("Senha incorreta", "/usuarios/me");
        }

        try {
            List<AlertaMonitoramentoEntity> alertas =
                    AlertaMonitoramentoEntity.list("usuario", usuario);

            if (!alertas.isEmpty()) {
                PublicacaoEncontradaEntity.delete("alerta in ?1", alertas);
            }

            AlertaMonitoramentoEntity.delete("usuario", usuario);

            String emailExcluido = usuario.email;
            RecuperacaoSenhaEntity.delete("usuario", usuario);
            UsuarioEntity.delete("id", usuario.id);

            //LOG.infof("Conta excluída via LGPD: %s", emailExcluido);

            NewCookie cookie = new NewCookie.Builder("token")
                    .value("")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite(NewCookie.SameSite.LAX)
                    .build();

            return Response.noContent().cookie(cookie).build();

        } catch (Exception e) {
            return ErrorException.internalError("Erro ao excluir conta", "/usuarios/me", e);
        }
    }

    @POST
    @Path("/verificar-email")
    public Response verificarEmail(VerificarEmailRequest request) {
        String url = "/usuarios/verificar-email";
        if (request == null
                || request.email() == null || request.email().isBlank()
                || request.codigo() == null || request.codigo().isBlank()) {
            return ErrorException.badRequest("E-mail e código são obrigatórios", url);
        }

        String ip = IpUtil.extrair(httpRequest);
        if (!rateLimiterService.permitir("verificar-email:" + request.email().toLowerCase(), 10, Duration.ofMinutes(15))) {
            return ErrorException.tooManyRequests("Muitas tentativas. Solicite um novo código.", url);
        }

        try {
            usuarioService.verificarEmail(request.email(), request.codigo());
            return Response.ok().entity("E-mail verificado com sucesso.").build();
        } catch (IllegalArgumentException e) {
            return ErrorException.badRequest(e.getMessage(), url);
        }
    }

    @POST
    @Path("/reenviar-codigo")
    public Response reenviarCodigo(ReenviarCodigoRequest request) {
        String url = "/usuarios/reenviar-codigo";
        if (request == null || request.email.isBlank() || request.email().isBlank()) {
            return ErrorException.badRequest("E-mail é obrigatório", url);
        }

        String ip = IpUtil.extrair(httpRequest);
        if (!rateLimiterService.permitir("reenviar-codigo:ip:" + ip, 10, Duration.ofHours(1))) {
            return ErrorException.tooManyRequests("Muitas tentativas. Tente novamente mais tarde.", url);
        }

        String chaveEmail = "reenviar-codigo:email:" + request.email().toLowerCase();
        if (rateLimiterService.permitir(chaveEmail, 3, Duration.ofHours(1))) {
            usuarioService.reenviarCodigoVerificacao(request.email());
        }

        return Response.ok().entity("Se o e-mail existir e ainda não estiver verificado, um novo código foi enviado.").build();
    }
}
