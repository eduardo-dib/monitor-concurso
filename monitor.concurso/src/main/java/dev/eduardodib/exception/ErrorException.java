package dev.eduardodib.exception;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

public record ErrorException(int status, String mensagem, String endpoint) {

    private static final Logger LOG = Logger.getLogger(ErrorException.class);

    public static Response conflict(String mensagem, String endpoint) {
        LOG.errorf("[409 CONFLICT] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorException(409, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response conflict(String mensagem, String endpoint, Throwable e) {
        LOG.errorf(e, "[409 CONFLICT] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorException(409, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response unauthorized(String mensagem, String endpoint) {
        LOG.errorf("[401 UNAUTHORIZED] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorException(401, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response unauthorized(String mensagem, String endpoint, Throwable e) {
        LOG.errorf(e, "[401 UNAUTHORIZED] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorException(401, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response badRequest(String mensagem, String endpoint) {
        LOG.errorf("[400 BAD REQUEST] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorException(400, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response badRequest(String mensagem, String endpoint, Throwable e) {
        LOG.errorf(e, "[400 BAD REQUEST] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorException(400, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response notFound(String mensagem, String endpoint) {
        LOG.errorf("[404 NOT FOUND] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorException(404, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response notFound(String mensagem, String endpoint, Throwable e) {
        LOG.errorf(e, "[404 NOT FOUND] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorException(404, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response internalError(String mensagem, String endpoint) {
        LOG.errorf("[500 INTERNAL ERROR] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorException(500, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }

    public static Response internalError(String mensagem, String endpoint, Throwable e) {
        LOG.errorf(e, "[500 INTERNAL ERROR] %s - %s", endpoint, mensagem);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorException(500, "Erro no sistema: " + mensagem, endpoint))
                .build();
    }
}