package dev.eduardodib.exception;

import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
public class SegurancaLog implements ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(SegurancaLog.class);

    @Inject
    HttpServerRequest request;

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {

        int status = responseContext.getStatus();

        if (status != 401 && status != 403) {
            return;
        }

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        if (ip == null || ip.isBlank()) {
            ip = request.remoteAddress() != null
                    ? request.remoteAddress().host()
                    : "IP Desconhecido";
        }

        LOG.warnf(
                "[SEGURANÇA] %d | MÉT: %s | ENDPOINT: %s | IP: %s",
                status,
                method,
                path,
                ip
        );
    }
}