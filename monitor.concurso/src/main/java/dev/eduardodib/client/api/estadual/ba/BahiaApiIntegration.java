package dev.eduardodib.client.api.estadual.ba;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BahiaApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(BahiaApiIntegration.class);
    private static final String BASE_URL = "https://dool.egba.ba.gov.br";
    private static final int PAGE_SIZE = 10;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = createHttpClientIgnoringSSL();

    @Override
    public String getEstado() {
        return "BA";
    }

    @Override
    public List<DiarioOficialScraper.PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<DiarioOficialScraper.PublicacaoScraped> resultado = new ArrayList<>();
        int offset = 0;
        String dataFim = LocalDate.now().toString();

        try {
            while (true) {
                String termoCodificado = URLEncoder.encode("\"" + palavrasChave + "\"", StandardCharsets.UTF_8);
                String url = BASE_URL + "/busca/busca/buscar/query/" + offset
                        + "/di:" + dataInicio + "/df:" + dataFim
                        + "/?1=1&q=" + termoCodificado;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "Mozilla/5.0")
                        .GET()
                        .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                BahiaApiResponse apiResponse = MAPPER.readValue(response.body(), BahiaApiResponse.class);

                if (apiResponse.hits == null || apiResponse.hits.hits == null || apiResponse.hits.hits.isEmpty()) break;

                LOG.infof("[BA] Offset %d — %d resultados", offset, apiResponse.hits.hits.size());

                for (BahiaApiResponse.Hit hit : apiResponse.hits.hits) {
                    String conteudo = "";
                    if (hit.highlight != null && hit.highlight.conteudo != null) {
                        conteudo = String.join(" | ", hit.highlight.conteudo)
                                .replaceAll("<[^>]*>", "");
                    }

                    String edicao = hit.suplemento != null
                            ? hit.suplemento.replace("Edição ", "")
                            : "";

                    String link = BASE_URL + "/login#/e:" + hit._source.diario_id
                            + "/p:" + hit._source.pagina
                            + "?find=" + URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8);

                    resultado.add(new DiarioOficialScraper.PublicacaoScraped(
                            "Diário Oficial BA - " + hit.suplemento + " - Pág " + hit._source.pagina,
                            conteudo,
                            link,
                            hit._source.data,
                            "BA",
                            edicao,
                            String.valueOf(hit._source.pagina),
                            "BAHIA_API"
                    ));
                }

                if (apiResponse.hits.hits.size() < PAGE_SIZE) break;
                offset += PAGE_SIZE;
            }

            LOG.infof("[BA] Total: %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[BA] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }


    private static HttpClient createHttpClientIgnoringSSL() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new java.security.SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar HttpClient sem SSL", e);
        }
    }
}
