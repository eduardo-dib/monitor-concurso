package dev.eduardodib.scraper.ce;

import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CearaScraper implements DiarioOficialScraper {

    private static final Logger LOG = Logger.getLogger(CearaScraper.class);
    private static final String BASE_URL = "http://pesquisa.doe.seplag.ce.gov.br/doepesquisa";
    private static final DateTimeFormatter FORMATO_ENTRADA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_SAIDA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getEstado() {
        return "CE";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        try {
            String dataInicioFormatada = LocalDate.parse(dataInicio, FORMATO_ENTRADA).format(FORMATO_SAIDA);
            String dataFimFormatada = LocalDate.now().format(FORMATO_SAIDA);

            String url = BASE_URL + "/sead.to"
                    + "?page=pesquisaTextual&action=PesquisarTextual&cmd=11&flag=1"
                    + "&dataini=" + URLEncoder.encode(dataInicioFormatada, StandardCharsets.UTF_8)
                    + "&datafim=" + URLEncoder.encode(dataFimFormatada, StandardCharsets.UTF_8)
                    + "&numDiario=&numCaderno=&numPagina="
                    + "&RadioGroup1=radio1"
                    + "&pesqAnd=" + URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8)
                    + "&consultar=";

            // Busca inicial e captura cookies de sessão
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .method(Connection.Method.GET)
                    .execute();

            Map<String, String> cookies = response.cookies();
            Document doc = response.parse();

            boolean temMaisPaginas = true;
            int pagina = 1;

            LOG.infof("[CE] HTML recebido (500 chars): %s", doc.html().substring(0, Math.min(500, doc.html().length())));
            LOG.infof("[CE] Linhas encontradas com seletor atual: %d", doc.select("table.Formulario tr").size());
            LOG.infof("[CE] TDs bgcolor branco: %d", doc.select("td[bgcolor=#FFFFFF]").size());

            Elements tds = doc.select("td[bgcolor=#FFFFFF]");
            for (int i = 0; i < Math.min(8, tds.size()); i++) {
                LOG.infof("[CE] TD[%d]: '%s' | link: '%s'", i, tds.get(i).text(), tds.get(i).selectFirst("a") != null ? tds.get(i).selectFirst("a").attr("href") : "sem link");
            }

            while (temMaisPaginas) {
                List<PublicacaoScraped> paginaAtual = parsearResultados(doc, palavrasChave);
                resultado.addAll(paginaAtual);

                LOG.infof("[CE] Página %d — %d resultados", pagina, paginaAtual.size());

                // Verifica se tem próxima página
                String textoPaginacao = doc.select("td.SubTituloTabelaResutados div").text();
                if (textoPaginacao.contains("Pagina")) {
                    String[] partes = textoPaginacao.split("de");
                    if (partes.length == 2) {
                        int paginaAtualNum = Integer.parseInt(partes[0].replaceAll("[^0-9]", "").trim());
                        int totalPaginas = Integer.parseInt(partes[1].replaceAll("[^0-9]", "").trim());
                        if (paginaAtualNum >= totalPaginas) {
                            temMaisPaginas = false;
                            break;
                        }
                    }
                } else {
                    break;
                }

                // Navega para próxima página usando sessão
                response = Jsoup.connect(BASE_URL + "/sead.do")
                        .userAgent("Mozilla/5.0")
                        .data("page", "pesquisaTextual")
                        .data("cmd", "proximo")
                        .data("action", "NavegarBasico")
                        .data("flag", "1")
                        .cookies(cookies)
                        .method(Connection.Method.GET)
                        .execute();

                cookies = response.cookies();
                doc = response.parse();
                pagina++;
            }

            LOG.infof("[CE] Total: %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[CE] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }

    private List<PublicacaoScraped> parsearResultados(Document doc, String palavrasChave) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        Elements tds = doc.select("td[bgcolor=#FFFFFF]");

        int i = 0;
        while (i + 3 < tds.size()) {
            Element tdData = tds.get(i);
            Element tdDiario = tds.get(i + 1);
            Element tdCaderno = tds.get(i + 2);
            Element tdPagina = tds.get(i + 3);

            Element linkElement = tdData.selectFirst("a");
            if (linkElement == null || linkElement.attr("href").startsWith("javascript")) {
                i++;
                continue;
            }

            String link = linkElement.attr("href");
            String dataTexto = tdData.text().trim();
            String diario = tdDiario.text().trim();
            String pagina = tdPagina.text().trim();

            String data = LocalDate.now().toString();
            try {
                data = LocalDate.parse(dataTexto,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString();
            } catch (Exception ignored) {}

            resultado.add(new PublicacaoScraped(
                    "Diário Oficial CE - Nº " + diario + " - Pág " + pagina,
                    "",
                    link,
                    data,
                    "CE",
                    diario,
                    pagina,
                    "CEARA_SCRAPER"
            ));

            i += 4;
        }

        return resultado;
    }




}