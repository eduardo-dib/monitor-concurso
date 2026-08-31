package dev.eduardodib.client.api.estadual.ap;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AmapaApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(AmapaApiIntegration.class);
    private static final String BASE_URL = "https://diofe.portal.ap.gov.br";
    private static final int PAGE_SIZE = 10;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getEstado() {
        return "AP";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();
        String dataFim = LocalDate.now().toString();
        int offset = 0;

        try {
            while (true) {
                String termoEncoded = URLEncoder.encode("\"" + palavrasChave + "\"", StandardCharsets.UTF_8);
                String url = String.format(
                        "%s/busca/busca/buscar/query/%d/di:%s/df:%s/?1=1&q=%s",
                        BASE_URL, offset, dataInicio, dataFim, termoEncoded
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json, text/plain, */*")
                        .timeout(Duration.ofSeconds(30))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    LOG.warnf("AP retornou status %d em offset=%d", response.statusCode(), offset);
                    break;
                }

                AmapaApiResponse api = mapper.readValue(response.body(), AmapaApiResponse.class);

                if (api.hits == null || api.hits.hits == null || api.hits.hits.isEmpty()) {
                    break;
                }

                for (AmapaApiResponse.Hit hit : api.hits.hits) {
                    String trecho = (hit.highlight != null && hit.highlight.conteudo != null && !hit.highlight.conteudo.isEmpty())
                            ? hit.highlight.conteudo.get(0).replaceAll("<[^>]*>", "")
                            : "";

                    String edicao = hit.suplemento != null ? hit.suplemento.replace("Edição ", "") : "";


                    String link = String.format("%s/login#/e:%d/p:%d?find=%s",
                            BASE_URL, hit._source.diario_id, hit._source.pagina, palavrasChave);

                    resultado.add(new PublicacaoScraped(
                            "Diário Oficial do Amapá",
                            trecho,
                            link,
                            hit._source.data,
                            "AP",
                            edicao,
                            String.valueOf(hit._source.pagina),
                            "AMAPA_API"
                    ));
                }

                if (api.hits.hits.size() < PAGE_SIZE) {
                    break;
                }
                offset += PAGE_SIZE;
            }
        } catch (Exception e) {
            LOG.error("Erro ao buscar no diário do AP", e);
        }

        return resultado;
    }
}
