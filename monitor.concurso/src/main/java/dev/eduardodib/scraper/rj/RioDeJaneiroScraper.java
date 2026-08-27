package dev.eduardodib.scraper.rj;

import dev.eduardodib.scraper.DiarioOficialScraper;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class RioDeJaneiroScraper implements DiarioOficialScraper {

    private static final Logger LOG = Logger.getLogger(RioDeJaneiroScraper.class);

    private static final String BASE_URL = "https://www.ioerj.com.br/portal/modules/conteudoonline/";
    private static final String BUSCA_URL = BASE_URL + "busca_do.php?acao=busca";
    private static final int MAX_DIAS = 45;

    private static final Pattern PADRAO_DATA_PAGINA =
            Pattern.compile("Publicada em (\\d{2}/\\d{2}/\\d{4}) na p[áa]gina (\\d+)");

    @Override
    public String getEstado() {
        return "RJ";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> publicacoes = new ArrayList<>();
        LocalDate inicio = LocalDate.parse(dataInicio);
        LocalDate fim = LocalDate.now();

        if (inicio.isBefore(fim.minusDays(MAX_DIAS))) {
            LOG.warnf("RJ: período pedido excede %d dias (API não suporta range, uma chamada por dia) - truncando", MAX_DIAS);
            inicio = fim.minusDays(MAX_DIAS);
        }

        for (LocalDate data = inicio; !data.isAfter(fim); data = data.plusDays(1)) {
            try {
                publicacoes.addAll(buscarPorDia(palavrasChave, data));
            } catch (Exception e) {
                LOG.errorf(e, "Erro ao buscar diário do RJ (estado) para %s", data);
            }
        }

        return publicacoes;
    }

    private List<PublicacaoScraped> buscarPorDia(String palavrasChave, LocalDate data) throws Exception {
        Connection.Response response = Jsoup.connect(BUSCA_URL)
                .method(Connection.Method.POST)
                .data("textobusca", palavrasChave)
                .data("busca[jornal]", "")
                .data("datapublicacao[dia]", String.format("%02d", data.getDayOfMonth()))
                .data("datapublicacao[mes]", String.format("%02d", data.getMonthValue()))
                .data("datapublicacao[ano]", String.valueOf(data.getYear()))
                .data("tipobusca", "texto")
                .data("buscaordem", "datapublicacao desc")
                .data("buscar", "Buscar")
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() != 200) {
            LOG.warnf("RJ retornou status %d para %s", response.statusCode(), data);
            return List.of();
        }

        return parsearHtml(response.body());
    }

    private List<PublicacaoScraped> parsearHtml(String html) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        String htmlSemComentarios = html.replace("<!--", "").replace("-->", "");

        Document doc = Jsoup.parse(htmlSemComentarios);
        Elements blocos = doc.select("div.space");

        for (Element bloco : blocos) {
            try {
                PublicacaoScraped publicacao = parsearBloco(bloco);
                if (publicacao != null) {
                    resultado.add(publicacao);
                }
            } catch (Exception e) {
                LOG.warnf("Falha ao parsear um bloco do RJ, pulando: %s", e.getMessage());
            }
        }

        return resultado;
    }

    private PublicacaoScraped parsearBloco(Element bloco) {
        String textoCompleto = bloco.text();

        Matcher matcher = PADRAO_DATA_PAGINA.matcher(textoCompleto);
        String data = "";
        String pagina = "";
        if (matcher.find()) {
            data = matcher.group(1);
            pagina = matcher.group(2);
        }

        String link = "";
        Element linhaDaTabela = bloco.closest("tr");
        Element linkPdf = null;

        if (linhaDaTabela != null) {
            linkPdf = linhaDaTabela.selectFirst("a[href*='view_publicacao.php']");
        }

        if (linkPdf == null) {
            linkPdf = bloco.selectFirst("a[href*='view_publicacao.php']");
        }

        if (linkPdf != null) {
            String href = linkPdf.attr("href");
            if (href.startsWith("/")) {
                link = "https://www.ioerj.com.br" + href;
            } else {
                link = BASE_URL + href;
            }
        } else {
            Element linkVerTexto = bloco.selectFirst("a[href*='mostra_publicacao.php']");
            if (linkVerTexto != null) {
                link = BASE_URL + linkVerTexto.attr("href");
            }
        }

        String jornal = "";
        Elements tds = bloco.select("td");
        for (Element td : tds) {
            String texto = td.text();
            if (texto.startsWith("Jornal:")) {
                jornal = texto.replace("Jornal:", "").trim();
            }
        }


        String conteudo = "";
        if (linhaDaTabela != null) {

            Element tdConteudo = linhaDaTabela.selectFirst("td.style4");


            if (tdConteudo == null) {
                Element proximaLinha = linhaDaTabela.nextElementSibling();
                if (proximaLinha != null) {
                    tdConteudo = proximaLinha.selectFirst("td.style4");
                }
            }


            if (tdConteudo != null) {
                conteudo = tdConteudo.text();
            }
        }
        // ----------------------------------------

        return new PublicacaoScraped(
                "Diário Oficial do Estado do Rio de Janeiro" + (jornal.isEmpty() ? "" : " - " + jornal),
                conteudo,
                link,
                data,
                "RJ",
                "",
                pagina,
                "RIODEJANEIRO_SCRAPER"
        );
    }
}