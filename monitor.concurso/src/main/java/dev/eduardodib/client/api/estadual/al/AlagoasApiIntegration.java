package dev.eduardodib.client.api.estadual.al;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AlagoasApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(AlagoasApiIntegration.class);
    private static final String BASE_URL = "https://diario.imprensaoficial.al.gov.br";
    private static final int PAGE_SIZE = 10;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Override
    public String getEstado() {
        return "AL";
    }

    @Override
    public List<DiarioOficialScraper.PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<DiarioOficialScraper.PublicacaoScraped> resultado = new ArrayList<>();
        int pagina = 1;
        String dataFim = LocalDate.now().toString();

        try {
            while (true) {
                String body = String.format(
                        "{\"keywords\":\"%s\",\"range\":[\"%s\",\"%s\"],\"edition_number\":\"\",\"searchType\":\"toda_palavra\",\"order\":\"novo\"}",
                        palavrasChave, dataInicio, dataFim
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/apinova/api/editions/searchES?page=" + pagina + "&bucket_size=" + PAGE_SIZE))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                AlagoasApiResponse apiResponse = MAPPER.readValue(response.body(), AlagoasApiResponse.class);

                if (apiResponse.result == null || apiResponse.result.items == null || apiResponse.result.items.isEmpty()) break;

                LOG.infof("[AL] Página %d — %d resultados", pagina, apiResponse.result.items.size());

                for (AlagoasApiResponse.Item item : apiResponse.result.items) {
                    String conteudo = item.highlight != null
                            ? String.join(" | ", item.highlight).replaceAll("<[^>]*>", "").replaceAll("<\\\\/em>", "")
                            : "";

                    String link = BASE_URL + "/ver-edicao?edition=" + item.editionId
                            + "&page=" + item.pageNumber
                            + "&searchTerm=" + palavrasChave.replace(" ", "%20");

                    resultado.add(new DiarioOficialScraper.PublicacaoScraped(
                            "Diário Oficial AL - Edição " + item.editionNumber + " - Pág " + item.pageNumber,
                            conteudo,
                            link,
                            item.publicationDate,
                            "AL",
                            String.valueOf(item.editionNumber),
                            String.valueOf(item.pageNumber),
                            "ALAGOAS_API"
                    ));
                }

                if (apiResponse.result.items.size() < PAGE_SIZE) break;
                pagina++;
            }

            LOG.infof("[AL] Total: %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[AL] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }
}
