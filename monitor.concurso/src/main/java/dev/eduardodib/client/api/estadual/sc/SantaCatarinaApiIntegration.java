package dev.eduardodib.client.api.estadual.sc;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SantaCatarinaApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(SantaCatarinaApiIntegration.class);
    private static final String BASE_URL = "https://portal.doe.sea.sc.gov.br/apis/busca-materia";
    private static final int PAGE_SIZE = 10;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getEstado() {
        return "SC";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();
        String dtFim = LocalDate.now().toString();
        int from = 0;

        try {
            while (true) {
                String body = String.format(
                        "{\"texto\":\"%s\",\"dtIni\":\"%s\",\"dtFim\":\"%s\",\"from\":%d,\"size\":%d}",
                        palavrasChave, dataInicio, dtFim, from, PAGE_SIZE
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(30))
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    LOG.warnf("SC retornou status %d em from=%d", response.statusCode(), from);
                    break;
                }

                SantaCatarinaApiResponse api = mapper.readValue(response.body(), SantaCatarinaApiResponse.class);

                if (api.materias == null || api.materias.isEmpty()) {
                    break;
                }

                for (SantaCatarinaApiResponse.Materia m : api.materias) {
                    String data = (m.publicacao != null && m.publicacao.length() >= 10)
                            ? m.publicacao.substring(0, 10)
                            : LocalDate.now().toString();

                    resultado.add(new PublicacaoScraped(
                            (m.categoria != null ? m.categoria + " - " : "") + m.assunto,
                            m.resumo,
                            m.extrato,
                            data,
                            "SC",
                            m.nrJornal,
                            "",
                            "SANTACATARINA_API"
                    ));
                }

                if (api.materias.size() < PAGE_SIZE) {
                    break;
                }
                from += PAGE_SIZE;
            }
        } catch (Exception e) {
            LOG.error("Erro ao buscar no diário de SC", e);
        }

        return resultado;
    }
}
