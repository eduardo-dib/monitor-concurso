package dev.eduardodib.resource.usuario;


import dev.eduardodib.domain.usuario.CadastroUsuarioResponseDTO;
import dev.eduardodib.domain.usuario.LoginResponseDTO;
import dev.eduardodib.domain.usuario.UsuarioEntity;
import dev.eduardodib.service.token.TokenService;
import dev.eduardodib.service.usuario.UsuarioService;
import dev.eduardodib.exception.ErrorException;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.ws.rs.*;


@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    TokenService tokenService;


    public record CadastroRequest(String nome, String email, String senha) {}
    public record LoginRequest(String email, String senha) {}
    public record SolicitarRecuperacaoRequest(String email) {}
    public record RedefinirSenhaRequest(String token, String novaSenha) {}

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
}
