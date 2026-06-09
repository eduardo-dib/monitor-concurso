package dev.eduardodib.client.api.estadual.df;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class DistritoFederalApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(DistritoFederalApiIntegration.class);
    private static final String BASE_URL = "https://dodf.df.gov.br";
    private static final int PAGE_SIZE = 10;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String getEstado() {
        return "DF";
    }

    @Override
    public List<DiarioOficialScraper.PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<DiarioOficialScraper.PublicacaoScraped> resultado = new ArrayList<>();
        int pagina = 1;
        String dataFim = LocalDate.now().toString();

        try {
            while (true) {
                String json = Jsoup.connect(BASE_URL + "/dodf/materia/listar")
                        .userAgent("Mozilla/5.0")
                        .method(Connection.Method.POST)
                        .data("termo", palavrasChave)
                        .data("dtInicial", dataInicio)
                        .data("dtFinal", dataFim)
                        .data("tpLocalBusca", "tudo")
                        .data("tpJornal", "Todos")
                        .data("tpSecao", "Todas")
                        .data("tpBusca", "contexto")
                        .data("pagina", String.valueOf(pagina))
                        .data("listarFiltrosMaterias", "true")
                        .data("tpPlatform", "desktop")
                        .ignoreContentType(true)
                        .execute()
                        .body();

                JsonNode root = MAPPER.readTree(json);
                JsonNode materias = root.get("listaMaterias");
                int totalPaginas = root.path("totalPaginas").asInt(1);

                if (materias == null || !materias.isArray() || materias.isEmpty()) break;

                LOG.infof("[DF] Página %d de %d", pagina, totalPaginas);

                for (JsonNode materia : materias) {
                    String coMateria = materia.path("co_materia").asText();
                    String titulo = materia.path("ds_titulo").asText();
                    String resumo = materia.path("tx_resumo").asText().replaceAll("<[^>]*>", "");
                    String edicao = materia.path("nu_jornal").asText();
                    String dtPublicacao = materia.path("dt_previsao_publicacao").asText();
                    String slug = materia.path("slug").asText();
                    String link = BASE_URL + "/dodf/materia/visualizar?co_data=" + coMateria
                            + "&p=" + slug
                            + "&busca=contexto#termo=" + palavrasChave;

                    String data = LocalDate.now().toString();
                    try {
                        data = LocalDateTime.parse(dtPublicacao, DateTimeFormatter.ISO_DATE_TIME)
                                .toLocalDate().toString();
                    } catch (Exception ignored) {}

                    resultado.add(new DiarioOficialScraper.PublicacaoScraped(
                            titulo,
                            resumo,
                            link,
                            data,
                            "DF",
                            edicao,
                            "",
                            "DISTRITOFEDERAL_API"
                    ));
                }

                if (pagina >= totalPaginas) break;
                pagina++;
            }

            LOG.infof("[DF] Total: %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[DF] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }
}
