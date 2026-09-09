package dev.eduardodib.scraper.parana;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DioeParanaScraper implements DiarioOficialScraper {

    private static final Logger LOG = Logger.getLogger(DioeParanaScraper.class);


    private static final String BASE_URL = "https://dioe.pr.gov.br//busca/busca/buscar/query";



    private static final int PAGE_SIZE = 10;
    private static final int MAX_PAGINAS_SEGURANCA = 50;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String getEstado() {
        return "PR";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        try {
            LocalDate dataInicial = (dataInicio != null && !dataInicio.isEmpty())
                    ? LocalDate.parse(dataInicio)
                    : LocalDate.now().minusDays(30);
            LocalDate dataFinal = LocalDate.now();


            String termoEncoded = URLEncoder.encode("\"" + palavrasChave + "\"", StandardCharsets.UTF_8);

            int offset = 0;
            int paginasLidas = 0;

            while (paginasLidas < MAX_PAGINAS_SEGURANCA) {
                String url = String.format("%s/%d/di:%s/df:%s/?1=1&q=%s",
                        BASE_URL, offset, dataInicial, dataFinal, termoEncoded);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json, text/plain, */*")
                        .header("Referer", "https://www.dioe.pr.gov.br/")
                        .header("Origin", "https://www.dioe.pr.gov.br")
                        .header("User-Agent", "Mozilla/5.0 (compatible; VigiaConcursosBot/1.0)")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    LOG.warnf("[DIOE-PR] Status inesperado (%d) ao buscar '%s'", response.statusCode(), palavrasChave);
                    break;
                }

                DioeSearchResponse parsed = objectMapper.readValue(response.body(), DioeSearchResponse.class);

                if (parsed.hits() == null || parsed.hits().hits() == null || parsed.hits().hits().isEmpty()) {
                    break;
                }

                for (DioeSearchResponse.Hit hit : parsed.hits().hits()) {
                    DioeSearchResponse.Source source = hit._source();
                    if (source == null) continue;

                    String link = String.format(
                            "https://dioe.pr.gov.br/portal/visualizacoes/pdf/%d#/p:%d/e:%d?find=%s",
                            source.diario_id(), source.pagina(), source.diario_id(),
                            URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8));

                    String titulo = "Diário Oficial Executivo PR - Edição " + source.diario_id()
                            + " - Pág " + source.pagina();

                    resultado.add(new PublicacaoScraped(
                            titulo,
                            source.conteudo(),
                            link,
                            source.data(),
                            "PR",
                            String.valueOf(source.diario_id()),
                            String.valueOf(source.pagina()),
                            "PARANA_API"
                    ));
                }

                paginasLidas++;
                if (parsed.hits().hits().size() < PAGE_SIZE) break;
                offset += PAGE_SIZE;
            }

            LOG.infof("[DIOE-PR] Encontradas %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[DIOE-PR] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }

    private record DioeSearchResponse(Hits hits) {
        private record Hits(List<Hit> hits) {}
        private record Hit(Source _source) {}
        private record Source(String conteudo, String data, int pagina, long diario_id) {}
    }
}
