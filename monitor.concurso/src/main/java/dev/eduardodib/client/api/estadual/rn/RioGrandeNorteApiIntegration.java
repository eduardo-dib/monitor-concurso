package dev.eduardodib.client.api.estadual.rn;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class RioGrandeNorteApiIntegration implements DiarioOficialClient {

    private static final String BASE_URL = "https://deirn.sdoe.com.br/diariooficial/public/search";
    private static final String CODIGO_DIARIO = "121";
    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getEstado() {
        return "RN";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultados = new ArrayList<>();

        String dataInicial = LocalDate.parse(dataInicio).format(FORMATO_BR);
        String dataFinal = LocalDate.now().format(FORMATO_BR);

        int first = 0;
        int total = Integer.MAX_VALUE;

        try {
            while (first < total) {
                String body = montarBody(palavrasChave, dataInicial, dataFinal, first);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                RioGrandeNorteApiResponse resposta = mapper.readValue(response.body(), RioGrandeNorteApiResponse.class);

                total = resposta.rowCount;

                if (resposta.list == null || resposta.list.isEmpty()) {
                    break;
                }

                String link = montarLinkBusca(palavrasChave, dataInicial, dataFinal);

                for (RioGrandeNorteApiResponse.Item item : resposta.list) {
                    resultados.add(new PublicacaoScraped(
                            item.titulo != null ? item.titulo : item.nomeCategoria,
                            item.resumo,
                            link,
                            item.dataPublicacao,
                            "RN",
                            null, // edição não disponível nessa fonte
                            null, // página não disponível nessa fonte
                            "RIOGRANDENORTE_API"
                    ));
                }

                first += PAGE_SIZE;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar no diário oficial do RN", e);
        }

        return resultados;
    }

    private String montarBody(String palavrasChave, String dataInicial, String dataFinal, int first) throws Exception {
        var node = mapper.createObjectNode();
        node.put("first", first);
        node.put("maxResults", PAGE_SIZE);
        node.putObject("restricoes");
        node.putObject("order");
        node.put("data", java.time.Instant.now().toString());
        node.put("minDate", "2020-01-01T03:00:00.000Z");
        node.put("maxDate", "2029-01-01T03:00:00.000Z");
        node.put("palavras", palavrasChave);
        node.put("dataInicial", dataInicial);
        node.put("dataFinal", dataFinal);
        node.put("intervaloAno", dataInicial + "-" + dataFinal);
        node.put("codigoDiario", CODIGO_DIARIO);
        return mapper.writeValueAsString(node);
    }

    private String montarLinkBusca(String palavrasChave, String dataInicial, String dataFinal) {
        String diarioBase64 = Base64.getEncoder().encodeToString(CODIGO_DIARIO.getBytes(StandardCharsets.UTF_8));
        String inicioEscapado = dataInicial.replace("/", "~2F");
        String fimEscapado = dataFinal.replace("/", "~2F");

        return "https://deirn.sdoe.com.br/diariooficialweb/#/busca-avancada?diario=" + diarioBase64
                + "&inicio=" + inicioEscapado
                + "&fim=" + fimEscapado
                + "&palavra=" + palavrasChave
                + "&consultar=true";
    }
}
