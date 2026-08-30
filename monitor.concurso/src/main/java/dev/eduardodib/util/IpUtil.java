package dev.eduardodib.util;

import io.vertx.core.http.HttpServerRequest;

public class IpUtil {
    private IpUtil() {}

    public static String extrair(HttpServerRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.remoteAddress().host();
    }
}
