package dev.eduardodib.filter;

import dev.eduardodib.service.ratelimit.RateLimiterService;
import dev.eduardodib.util.IpUtil;
import io.quarkus.arc.Unremovable;
import io.quarkus.vertx.http.runtime.filters.Filter;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;

@ApplicationScoped
@Unremovable
public class MonitoramentoTestRateLimitFilter implements Filter {

    @Inject
    RateLimiterService rateLimiterService;

    @Override
    public Handler<RoutingContext> getHandler() {
        return context -> {
            String path = context.request().path();

            if (path.startsWith("/monitoramento/")) {
                String ip = IpUtil.extrair(context.request());

                if (!rateLimiterService.permitir("monitoramento-teste:ip:" + ip, 20, Duration.ofHours(1))) {
                    context.response()
                            .setStatusCode(429)
                            .putHeader("Content-Type", "application/json")
                            .end("{\"status\":429,\"mensagem\":\"Erro no sistema: Muitas requisições nos endpoints de teste. Tente novamente mais tarde.\",\"endpoint\":\"" + path + "\"}");
                    return;
                }
            }

            context.next();
        };
    }

    @Override
    public int getPriority() {
        return 100;
    }
}