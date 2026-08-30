package dev.eduardodib.service.ratelimit;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.scheduler.Scheduled;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class RateLimiterService {

    private static class Contador {
        final AtomicInteger tentativas = new AtomicInteger(0);
        volatile Instant inicioJanela = Instant.now();
    }

    private final ConcurrentHashMap<String, Contador> contadores = new ConcurrentHashMap<>();


    public boolean permitir(String chave, int maxTentativas, Duration janela) {
        Contador contador = contadores.computeIfAbsent(chave, k -> new Contador());

        synchronized (contador) {
            Instant agora = Instant.now();
            if (Duration.between(contador.inicioJanela, agora).compareTo(janela) > 0) {
                contador.inicioJanela = agora;
                contador.tentativas.set(0);
            }
            return contador.tentativas.incrementAndGet() <= maxTentativas;
        }
    }

    @Scheduled(every = "1h")
    void limparContadoresAntigos() {
        Instant limite = Instant.now().minus(Duration.ofHours(2));
        contadores.entrySet().removeIf(e -> e.getValue().inicioJanela.isBefore(limite));
    }
}