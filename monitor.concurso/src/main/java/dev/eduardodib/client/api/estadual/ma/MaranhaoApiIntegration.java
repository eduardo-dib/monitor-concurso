package dev.eduardodib.client.api.estadual.ma;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class MaranhaoApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(MaranhaoApiIntegration.class);

    private static final String BASE_URL = "https://diariooficial.ma.gov.br/ajax.busca.php";
    private static final int MAX_PAGINAS = 100;

    private static final Pattern SET_MODAL_PATTERN =
            Pattern.compile("setModal\\('[^']*','[^']*','([^']*)'");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getEstado() {
        return "MA";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> publicacoes = new ArrayList<>();
        String scrollId = "";
        int paginasProcessadas = 0;

        try {
            while (paginasProcessadas < MAX_PAGINAS) {
                MaranhaoApiResponse resposta = buscarPagina(palavrasChave, dataInicio, scrollId);
                paginasProcessadas++;

                if (resposta == null || resposta.busca == null || resposta.busca.erro) {
                    LOG.warnf("MA retornou erro na busca (pode ser elasticsearch), página %d", paginasProcessadas);
                    break;
                }

                List<PublicacaoScraped> daPagina = parsearEsHtml(resposta.busca.esHtml);

                if (daPagina.isEmpty()) {
                    break;
                }

                publicacoes.addAll(daPagina);

                scrollId = resposta.busca.scrollId;
                if (scrollId == null || scrollId.isBlank()) {
                    break;
                }
            }

            if (paginasProcessadas >= MAX_PAGINAS) {
                LOG.warnf("MA atingiu o limite de %d páginas", MAX_PAGINAS);
            }
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao buscar diário do MA");
        }

        return publicacoes;
    }

    private MaranhaoApiResponse buscarPagina(String palavrasChave, String dataInicio, String scrollId) throws Exception {
        String termo = URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8);
        String dataFim = LocalDate.now().toString();
        String scrollIdEncoded = scrollId.isBlank() ? "" : URLEncoder.encode(scrollId, StandardCharsets.UTF_8);

        String url = BASE_URL
                + "?&termo=" + termo
                + "&sigla="
                + "&datai=" + dataInicio
                + "&dataf=" + dataFim
                + "&scrollId=" + scrollIdEncoded;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOG.warnf("MA retornou status %d", response.statusCode());
            return null;
        }

        return objectMapper.readValue(response.body(), MaranhaoApiResponse.class);
    }

    private List<PublicacaoScraped> parsearEsHtml(String esHtml) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        if (esHtml == null || esHtml.isBlank()) {
            return resultado;
        }

        Document doc = Jsoup.parse(esHtml);
        Elements cards = doc.select("div.card");

        for (Element card : cards) {
            try {
                resultado.add(parsearCard(card));
            } catch (Exception e) {
                LOG.warnf("Falha ao parsear um card do MA, pulando: %s", e.getMessage());
            }
        }

        return resultado;
    }

    private PublicacaoScraped parsearCard(Element card) {
        String categoria = card.selectFirst("strong.text-primary").text().trim();

        String dataTexto = card.selectFirst("div#dataPub").text();
        String data = dataTexto.replaceAll("(?i)Publicado em\\s*", "").trim();

        Elements paragrafos = card.select("p.card-text.mb-auto");
        String conteudo = paragrafos.size() > 0
                ? paragrafos.get(0).text().replaceAll("\\s+", " ").trim()
                : "";

        String pagina = "";
        if (paragrafos.size() > 1) {
            Matcher paginaMatcher = Pattern.compile("\\d+").matcher(paragrafos.get(1).text());
            if (paginaMatcher.find()) {
                pagina = paginaMatcher.group();
            }
        }

        String codigoEdicao = "";
        Element botao = card.selectFirst("a.btnVermais");
        if (botao != null) {
            String onclick = botao.attr("onclick");
            Matcher m = SET_MODAL_PATTERN.matcher(onclick);
            if (m.find()) {
                codigoEdicao = m.group(1);
            }
        }

        String link = codigoEdicao.isEmpty()
                ? ""
                : "https://diariooficial.ma.gov.br/download.php?arq=" + codigoEdicao;

        return new PublicacaoScraped(
                "Diário Oficial do Maranhão - " + categoria,
                conteudo,
                link,
                data,
                "MA",
                codigoEdicao,
                pagina,
                "MARANHAO_API"
        );
    }
}
