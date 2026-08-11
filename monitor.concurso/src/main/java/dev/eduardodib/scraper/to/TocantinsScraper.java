package dev.eduardodib.scraper.to;

import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TocantinsScraper implements DiarioOficialScraper {

    private static final String BASE_URL = "https://diariooficial.to.gov.br/busca";

    @Override
    public String getEstado() {
        return "TO";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultados = new ArrayList<>();

        try {
            String dataFinal = LocalDate.now().toString(); // yyyy-MM-dd
            String termoEncoded = URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8);

            String url = BASE_URL
                    + "?por=texto"
                    + "&texto=" + termoEncoded
                    + "&data-inicial=" + dataInicio
                    + "&data-final=" + dataFinal;

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(15000)
                    .get();

            Elements linhas = doc.select("table.responsive-table tbody tr");

            for (Element linha : linhas) {
                Elements colunas = linha.select("td");
                if (colunas.size() < 5) continue;

                String edicao = colunas.get(0).text().replace("Nº", "").trim();
                String dataPublicacao = colunas.get(1).text().trim(); // dd/MM/yyyy
                String paginas = colunas.get(2).text().trim();

                Element linkEl = linha.selectFirst("a[href]");
                String link = linkEl != null ? linkEl.attr("href") : null;

                if (link == null) continue;

                resultados.add(new PublicacaoScraped(
                        "Diário Oficial do Tocantins - Edição " + edicao,
                        "Termo \"" + palavrasChave + "\" encontrado na edição " + edicao
                                + " (" + paginas + "). O diário completo precisa ser baixado para localizar a ocorrência exata.",
                        link,
                        dataPublicacao,
                        "TO",
                        edicao,
                        paginas,
                        "TOCANTINS_SCRAPER"
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar no diário oficial do TO", e);
        }

        return resultados;
    }
}
