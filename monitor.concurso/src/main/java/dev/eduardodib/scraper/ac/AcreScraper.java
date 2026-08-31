package dev.eduardodib.scraper.ac;

import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AcreScraper implements DiarioOficialScraper {

    private static final Logger LOG = Logger.getLogger(AcreScraper.class);
    private static final String BASE_URL = "https://diario.ac.gov.br/";
    private static final DateTimeFormatter FORMATO_DATA_SITE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int MAX_PAGINAS_POR_ANO = 50; // trava de segurança

    @Override
    public String getEstado() {
        return "AC";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();
        LocalDate dataLimite = LocalDate.parse(dataInicio);
        int anoAtual = LocalDate.now().getYear();
        int anoInicial = dataLimite.getYear();

        try {
            for (int ano = anoInicial; ano <= anoAtual; ano++) {
                buscarNoAno(palavrasChave, ano, dataLimite, resultado);
            }
        } catch (Exception e) {
            LOG.error("Erro ao buscar no diário do AC", e);
        }

        return resultado;
    }

    private void buscarNoAno(String palavrasChave, int ano, LocalDate dataLimite,
                             List<PublicacaoScraped> resultado) throws Exception {
        int pagina = 0;

        while (pagina < MAX_PAGINAS_POR_ANO) {
            Connection.Response response = Jsoup.connect(BASE_URL)
                    .data("paginaIni", String.valueOf(pagina))
                    .data("palavraTipo", "")
                    .data("ano_palavra", String.valueOf(ano))
                    .data("palavra", palavrasChave)
                    .postDataCharset("ISO-8859-1")
                    .method(Connection.Method.POST)
                    .timeout(30_000)
                    .execute();

            Document doc = response.parse();
            Elements linhasPrincipais = doc.select("tr[id^=trId1_]");

            if (linhasPrincipais.isEmpty()) {
                break;
            }

            boolean atingiuLimite = false;

            for (Element linha : linhasPrincipais) {
                String idSufixo = linha.id().substring("trId1_".length());

                String dataTexto = linha.select("td").get(0).text().trim();
                LocalDate dataPublicacao;
                try {
                    dataPublicacao = LocalDate.parse(dataTexto, FORMATO_DATA_SITE);
                } catch (Exception e) {
                    dataPublicacao = LocalDate.now();
                }

                if (dataPublicacao.isBefore(dataLimite)) {
                    atingiuLimite = true;
                    break;
                }

                Element linkEl = linha.select("a[href*=download.php]").first();
                String link = linkEl != null ? linkEl.attr("abs:href") : "";
                String titulo = linkEl != null ? linkEl.text().trim() : "Diário Oficial do Acre";

                Element linhaTrecho = doc.getElementById("trId2_" + idSufixo);
                String trecho = linhaTrecho != null ? linhaTrecho.text().trim() : "";

                resultado.add(new PublicacaoScraped(
                        titulo,
                        trecho,
                        link,
                        dataPublicacao.toString(),
                        "AC",
                        "",
                        "",
                        "ACRE-SCRAPER"
                ));
            }

            if (atingiuLimite) {
                break;
            }

            pagina++;
        }
    }
}
