package dev.eduardodib.resource.contato;

import dev.eduardodib.domain.contato.CategoriaContato;
import dev.eduardodib.exception.ErrorException;
import dev.eduardodib.service.contato.ContatoService;
import dev.eduardodib.service.ratelimit.RateLimiterService;
import dev.eduardodib.util.IpUtil;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Duration;

@Path("/contato")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContatoResource {

    @Inject
    ContatoService contatoService;

    @Inject
    RateLimiterService rateLimiterService;

    @Context
    HttpServerRequest httpRequest;

    public record ContatoRequest(String nome, String email, CategoriaContato categoria, String mensagem) {}

    @POST
    public Response enviarContato(ContatoRequest request) {
        String contatoUrl = "/contato";

        if (request == null || isBlank(request.nome()) || isBlank(request.email()) || isBlank(request.mensagem())) {
            return ErrorException.badRequest("Nome, e-mail e mensagem são obrigatórios", contatoUrl);
        }

        if (!request.email().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return ErrorException.badRequest("E-mail inválido", contatoUrl);
        }

        if (request.mensagem().length() > 2000) {
            return ErrorException.badRequest("Mensagem muito longa (máximo 2000 caracteres)", contatoUrl);
        }

        String ip = IpUtil.extrair(httpRequest);
        if (!rateLimiterService.permitir("contato:ip:" + ip, 5, Duration.ofHours(1))) {
            return ErrorException.tooManyRequests("Muitas mensagens enviadas. Tente novamente mais tarde.", contatoUrl);
        }

        CategoriaContato categoria = request.categoria() != null ? request.categoria() : CategoriaContato.OUTRO;
        contatoService.enviarContato(request.nome(), request.email(), categoria, request.mensagem());

        return Response.ok().entity("Mensagem enviada com sucesso! Retornaremos em breve.").build();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
