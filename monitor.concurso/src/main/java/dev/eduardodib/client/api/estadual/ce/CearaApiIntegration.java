package dev.eduardodib.client.api.estadual.ce;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class CearaApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(CearaApiIntegration.class);

    private static final String BASE_URL = "https://www.ce.gov.br/wp-admin/admin-ajax.php";
    private static final String NONCE = "e53672b1d4"; // hardcoded
    private static final int PAGE_SIZE = 10;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_REQUISICOES_PARALELAS = 5;
    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_REQUISICOES_PARALELAS);

    @Override
    public String getEstado() {
        return "CE";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> publicacoes = new ArrayList<>();
        int page = 1;

        try {
            while (true) {
                CearaApiResponse resposta = buscarPagina(palavrasChave, dataInicio, page);

                if (resposta == null || resposta.results == null || resposta.results.isEmpty()) {
                    break;
                }

                publicacoes.addAll(buscarDetalhesEmParalelo(resposta.results));

                boolean semProximaPagina = resposta.pagination == null || resposta.pagination.nextPage == null;
                if (semProximaPagina || resposta.results.size() < PAGE_SIZE) {
                    break;
                }

                page++;
            }
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao buscar diário do CE (possível nonce expirado - conferir resposta da API)");
        }

        return publicacoes;
    }

    private CearaApiResponse buscarPagina(String palavrasChave, String dataInicio, int page) throws Exception {
        String query = URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8);
        String dataFim = LocalDate.now().toString();

        String url = BASE_URL
                + "?action=ocrdoe_proxy"
                + "&nonce=" + NONCE
                + "&path=" + URLEncoder.encode("/ocr_documents/query", StandardCharsets.UTF_8)
                + "&page=" + page
                + "&query=" + query
                + "&exact_phrase=true"
                + "&order_by=relevance"
                + "&order_dir=desc"
                + "&per_page=" + PAGE_SIZE
                + "&start_date=" + dataInicio
                + "&end_date=" + dataFim;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOG.warnf("CE retornou status %d na página %d - possível nonce expirado", response.statusCode(), page);
            return null;
        }

        return objectMapper.readValue(response.body(), CearaApiResponse.class);
    }

    private String buscarImageUrl(long id) {
        try {
            String url = BASE_URL
                    + "?action=ocrdoe_proxy"
                    + "&nonce=" + NONCE
                    + "&path=" + URLEncoder.encode("/ocr_documents/" + id, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.warnf("Falha ao buscar detalhe do documento CE id=%d, status %d", id, response.statusCode());
                return null;
            }

            CearaDetalheResponse detalhe = objectMapper.readValue(response.body(), CearaDetalheResponse.class);
            return detalhe.imageUrl;

        } catch (Exception e) {
            LOG.errorf(e, "Erro ao buscar image_url do documento CE id=%d", id);
            return null;
        }
    }

    private List<PublicacaoScraped> buscarDetalhesEmParalelo(List<CearaApiResponse.Resultado> resultados) {
        List<CompletableFuture<PublicacaoScraped>> futures = resultados.stream()
                .map(resultado -> CompletableFuture.supplyAsync(() -> montarPublicacao(resultado), executor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private PublicacaoScraped montarPublicacao(CearaApiResponse.Resultado resultado) {
        String imageUrl = buscarImageUrl(resultado.id);

        return new PublicacaoScraped(
                "Diário Oficial do Ceará - Edição " + resultado.journalNumber,
                resultado.snippets != null && !resultado.snippets.isEmpty()
                        ? String.join(" [...] ", resultado.snippets)
                        : "",
                imageUrl,
                resultado.date,
                "CE",
                String.valueOf(resultado.journalNumber),
                String.valueOf(resultado.page),
                "CEARA_API"
        );
    }


    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }
}