package dev.eduardodib.scraper.parana;

import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jboss.logging.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DioeParanaScraper implements DiarioOficialScraper {

    private static final Logger LOG = Logger.getLogger(DioeParanaScraper.class);
    private static final String BASE_URL = "https://www.documentos.dioe.pr.gov.br/dioe/consultaPublicaPDF.do";

    @Override
    public String getEstado() {
        return "PR";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        try {

            Document paginaInicial = Jsoup.connect(BASE_URL + "?action=pgLocalizar")
                    .userAgent("Mozilla/5.0")
                    .get();

            Element tokenInput = paginaInicial.selectFirst("input[name=org.apache.struts.taglib.html.TOKEN]");
            if (tokenInput == null) {
                LOG.warn("[DIOE-PR] Token CSRF não encontrado");
                return resultado;
            }
            String token = tokenInput.val();
            LOG.infof("[DIOE-PR] Token extraído: %s", token);


            String dataFormatada = "";
            String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            if (dataInicio != null && !dataInicio.isEmpty()) {
                LocalDate data = LocalDate.parse(dataInicio);
                dataFormatada = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }


            String url = BASE_URL
                    + "?org.apache.struts.taglib.html.TOKEN=" + token
                    + "&action=pgLocalizar"
                    + "&enviado=true"
                    + "&numero="
                    + "&dataInicialEntrada=" + URLEncoder.encode(dataFormatada, StandardCharsets.UTF_8)
                    + "&dataFinalEntrada=" + URLEncoder.encode(dataHoje, StandardCharsets.UTF_8)
                    + "&search=" + URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8)
                    + "&diarioCodigo=3"
                    + "&localizador=";

            LOG.infof("[DIOE-PR] URL da busca: %s", url);

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            LOG.infof("[DIOE-PR] Blocos encontrados: %d", doc.select("table[id^=dv_pagina_]").size());


            Elements blocos = doc.select("table[id^=dv_pagina_]");

            for (Element bloco : blocos) {
                try {

                    String data = "";
                    Element labelData = bloco.select("td.labelpqn10:containsOwn(Data da Publicação)").first();
                    if (labelData != null && labelData.nextElementSibling() != null) {
                        data = labelData.nextElementSibling().text().trim();
                    }


                    String edicao = "";
                    Element labelEdicao = bloco.select("td.labelpqn10:containsOwn(Nº da Edição)").first();
                    if (labelEdicao != null && labelEdicao.nextElementSibling() != null) {
                        edicao = labelEdicao.nextElementSibling().text().trim();
                    }


                    String pagina = "";
                    Element labelPagina = bloco.select("span.label10").first();
                    if (labelPagina != null) {
                        pagina = labelPagina.text().replace("Pág", "").trim();
                    }


                    Element linkAmpliar = bloco.selectFirst("a[href^=javascript:ampliar]");
                    String link = "";
                    if (linkAmpliar != null) {
                        String href = linkAmpliar.attr("href");
                        String[] partes = href.replace("javascript:ampliar(", "")
                                .replace(");", "")
                                .replace("'", "")
                                .split(",");
                        if (partes.length >= 2) {
                            String ec = partes[0].trim();
                            String pg = partes[1].trim();
                            link = "https://www.documentos.dioe.pr.gov.br/dioe/consultaPublicaPDF.do"
                                    + "?action=pgLocalizar"
                                    + "&search=" + URLEncoder.encode(palavrasChave, StandardCharsets.UTF_8)
                                    + "&ec=" + ec
                                    + "&pg=" + pg;
                        }
                    }

                    if (!link.isEmpty()) {
                        String titulo = "Diário Oficial Executivo PR - Edição " + edicao + " - Pág " + pagina;
                        resultado.add(new PublicacaoScraped(titulo, "", link, data, "PR", edicao, pagina, "DIOE_PR"));
                    }

                } catch (Exception e) {
                    LOG.warnf(e, "[DIOE-PR] Erro ao parsear bloco");
                }
            }

            LOG.infof("[DIOE-PR] Encontradas %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[DIOE-PR] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }
}
