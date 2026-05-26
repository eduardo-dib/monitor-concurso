package dev.eduardodib.resource.alerta;


import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.alertamonitoramento.AlertaResponseDTO;
import dev.eduardodib.domain.usuario.UsuarioEntity;
import dev.eduardodib.exception.ErrorException;
import dev.eduardodib.service.alerta.AlertaService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.ArrayList;
import java.util.List;

@Path("/alertas")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlertaResource {

    @Inject
    AlertaService alertaService;

    public record AlertaRequest(String palavrasChave, String estado, String municipio, String orgao) {}

    @POST
    public Response criar(AlertaRequest request, @Context SecurityContext securityContext) {
        String alertasUrl = "/alertas";
        try {
            String email = securityContext.getUserPrincipal().getName();
            UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

            if (usuario == null) {
                return ErrorException.unauthorized("Usuário não encontrado", alertasUrl);
            }

            AlertaMonitoramentoEntity alerta = alertaService.criar(
                    usuario, request.palavrasChave(), request.estado(), request.municipio(), request.orgao()
            );
            AlertaResponseDTO retorno = AlertaResponseDTO.fromEntity(alerta);

            return Response.status(Response.Status.CREATED).entity(retorno).build();

        } catch (Exception e) {
            return ErrorException.internalError("Erro ao criar alerta", alertasUrl, e);
        }
    }

    @GET
    public Response listar(@Context SecurityContext securityContext) {
        String alertasUrl = "/alertas";
        try {
            String email = securityContext.getUserPrincipal().getName();
            UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

            if (usuario == null) {
                return ErrorException.unauthorized("Usuário não encontrado", alertasUrl);
            }

            List<AlertaMonitoramentoEntity> alertas = alertaService.listarPorUsuario(usuario);
            List<AlertaResponseDTO> retorno = new ArrayList<>();
            for (AlertaMonitoramentoEntity alerta : alertas) {
                retorno.add(AlertaResponseDTO.fromEntity(alerta));
            }
            return Response.ok(retorno).build();

        } catch (Exception e) {
            return ErrorException.internalError("Erro ao listar alertas", alertasUrl, e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        String alertasUrl = "/alertas/" + id;
        try {
            String email = securityContext.getUserPrincipal().getName();
            UsuarioEntity usuario = UsuarioEntity.findByEmail(email);

            if (usuario == null) {
                return ErrorException.unauthorized("Usuário não encontrado", alertasUrl);
            }

            boolean deletado = alertaService.deletar(id, usuario);

            if (!deletado) {
                return ErrorException.notFound("Alerta não encontrado", alertasUrl);
            }

            return Response.noContent().build();

        } catch (Exception e) {
            return ErrorException.internalError("Erro ao deletar alerta", alertasUrl, e);
        }
    }
}