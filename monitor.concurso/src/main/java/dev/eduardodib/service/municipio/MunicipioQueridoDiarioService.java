package dev.eduardodib.service.municipio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ApplicationScoped
public class MunicipioQueridoDiarioService {

    private static final Logger LOG = Logger.getLogger(MunicipioQueridoDiarioService.class);
    private static final String CITIES_URL = "https://api.queridodiario.org.br/cities?levels=3";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicReference<List<Municipio>> cache = new AtomicReference<>(Collections.emptyList());

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Municipio(String territory_id, String territory_name, String state_code) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CitiesResponse(List<Municipio> cities) {}

    public List<Municipio> listarPorEstado(String estado) {
        return cache.get().stream()
                .filter(m -> m.state_code().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    public List<Municipio> listarTodos() {
        return cache.get();
    }

    // única fonte da primeira carga — remove o onStart manual pra não duplicar
    // com a execução imediata que o Quarkus já dispara por padrão
    @Scheduled(every = "24h")
    void atualizarCache() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CITIES_URL))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (compatible; VigiaConcursosBot/1.0)")
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.warnf("[QueridoDiario] Status inesperado (%d) ao buscar municípios", response.statusCode());
                return;
            }

            CitiesResponse parsed = objectMapper.readValue(response.body(), CitiesResponse.class);
            if (parsed.cities() != null && !parsed.cities().isEmpty()) {
                cache.set(parsed.cities());
                LOG.infof("[QueridoDiario] Cache atualizado: %d municípios", parsed.cities().size());
            } else {
                LOG.warn("[QueridoDiario] Resposta vazia — mantendo cache anterior");
            }
        } catch (Exception e) {
            LOG.errorf(e, "[QueridoDiario] Erro ao atualizar cache — mantendo cache anterior");
        }
    }
}