package dev.eduardodib.scraper.federal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DouScraper implements DiarioOficialScraper {

    private static final Logger LOG = Logger.getLogger(DouScraper.class);
    private static final String BASE_URL = "https://www.in.gov.br/consulta/-/buscar/dou";
    private static final DateTimeFormatter FORMATO_DATA_URL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int DELTA = 75;

    @Override
    public String getEstado() {
        return "BR";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        try {
            String publishFrom = LocalDate.parse(dataInicio).format(FORMATO_DATA_URL);
            String publishTo = LocalDate.now().format(FORMATO_DATA_URL);
            String termoEncoded = URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8);

            String url = String.format(
                    "%s?q=%s&s=todos&exactDate=personalizado&sortType=0&publishFrom=%s&publishTo=%s&delta=%d",
                    BASE_URL, termoEncoded, publishFrom, publishTo, DELTA
            );

            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(30_000)
                    .execute();

            Document doc = response.parse();
            Element scriptParams = doc.getElementById("_br_com_seatecnologia_in_buscadou_BuscaDouPortlet_params");

            if (scriptParams == null) {
                LOG.warn("Bloco de resultados do DOU não encontrado — layout pode ter mudado");
                return resultado;
            }

            JsonNode hits = mapper.readTree(scriptParams.data()).get("jsonArray");
            if (hits == null || !hits.isArray()) {
                return resultado;
            }

            for (JsonNode hit : hits) {
                String conteudo = hit.path("content").asText("").replaceAll("<[^>]*>", "");
                String link = "https://www.in.gov.br/web/dou/-/" + hit.path("urlTitle").asText("");

                resultado.add(new PublicacaoScraped(
                        hit.path("title").asText(""),
                        conteudo,
                        link,
                        hit.path("pubDate").asText(""),
                        "BR",
                        hit.path("editionNumber").asText(""),
                        hit.path("numberPage").asText(""),
                        "DOU"
                ));
            }

        } catch (Exception e) {
            LOG.error("Erro ao buscar no DOU", e);
        }

        return resultado;
    }
}