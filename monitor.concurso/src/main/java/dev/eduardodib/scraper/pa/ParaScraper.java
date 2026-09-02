package dev.eduardodib.scraper.pa;

import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class ParaScraper implements DiarioOficialScraper {

    private static final String BASE_URL = "https://www.ioepa.com.br/pesquisa/";
    private static final DateTimeFormatter FORMATO_URL = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Pattern PADRAO_TOTAL = Pattern.compile("(\\d+)\\s+resultado");
    private static final Pattern PADRAO_PAGINA = Pattern.compile("(\\d+)");
    private static final int MAX_PAGINAS = 50;

    @Override
    public String getEstado() {
        return "PA";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        String di = LocalDate.parse(dataInicio).format(FORMATO_URL);
        String df = LocalDate.now().format(FORMATO_URL);

        Integer totalEsperado = null;
        int pagina = 1;

        while (pagina <= MAX_PAGINAS) {
            try {
                String url = BASE_URL + "?q=" + palavrasChave + "&di=" + di + "&df=" + df + "&p=" + pagina;
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(30_000)
                        .get();

                if (totalEsperado == null) {
                    totalEsperado = extrairTotal(doc);
                }

                Elements itens = doc.select("div.box.box-widget:has(h3.box-title)");
                if (itens.isEmpty()) {
                    break;
                }

                for (Element item : itens) {
                    resultado.add(parseItem(item));
                }

                if (totalEsperado != null && resultado.size() >= totalEsperado) {
                    break;
                }
                pagina++;

            } catch (Exception e) {
                break;
            }
        }

        return resultado;
    }

    private Integer extrairTotal(Document doc) {
        String textoAlerta = doc.select(".alert-info").text();
        Matcher m = PADRAO_TOTAL.matcher(textoAlerta);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    private PublicacaoScraped parseItem(Element item) {
        String tituloBox = item.select("h3.box-title").text();
        String data = tituloBox.replace("Diário publicado em:", "").trim();

        String linkPagina = null;
        String linkCompleto = null;
        for (Element a : item.select("ul.dropdown-menu li a")) {
            String texto = a.text().trim();
            if (texto.equalsIgnoreCase("Página")) {
                linkPagina = a.attr("href");
            } else if (texto.equalsIgnoreCase("Diário Completo")) {
                linkCompleto = a.attr("href");
            }
        }
        String link = linkPagina != null ? linkPagina : linkCompleto;

        String conteudo = item.select("dl dd").text();

        String paginaTexto = item.select("dl dt").text();
        Matcher mPagina = PADRAO_PAGINA.matcher(paginaTexto);
        String pagina = mPagina.find() ? mPagina.group(1) : null;

        return new PublicacaoScraped(
                "Diário Oficial do Pará – Página " + pagina,
                conteudo,
                link,
                data,
                "PA",
                null,
                pagina,
                "PARA_SCRAPER"
        );
    }
}
