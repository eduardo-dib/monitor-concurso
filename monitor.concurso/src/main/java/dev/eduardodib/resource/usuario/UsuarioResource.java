package dev.eduardodib.resource.usuario;


import dev.eduardodib.domain.usuario.CadastroUsuarioResponseDTO;
import dev.eduardodib.domain.usuario.LoginResponseDTO;
import dev.eduardodib.domain.usuario.UsuarioEntity;
import dev.eduardodib.service.token.TokenService;
import dev.eduardodib.service.usuario.UsuarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    @POST
    @Path("/cadastrar")
    public Response cadastrar(CadastroRequest request) {
        try {
            UsuarioEntity usuario = usuarioService.cadastrar(request.nome(), request.email(), request.senha());
            return Response.status(Response.Status.CREATED)
                    .entity(CadastroUsuarioResponseDTO.fromEntity(usuario))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        UsuarioEntity usuario = UsuarioEntity.findByEmail(request.email());

        if (usuario == null || !usuarioService.verificarSenha(request.senha(), usuario.senhaHash)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("E-mail ou senha inválidos")
                    .build();
        }

        return Response.ok(new LoginResponseDTO(tokenService.gerarToken(usuario))).build();
    }
}
