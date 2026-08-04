package dev.eduardodib.client.api.estadual.pi;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class PiauiApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(PiauiApiIntegration.class);

    private static final String BUSCA_URL = "https://www.diario.pi.gov.br/doe/Api/buscaavancada.json";
    private static final String ANEXO_BASE_URL = "https://www.diario.pi.gov.br/doe/files/diarios/anexo/";

    private static final Pattern DADOS_DIARIO_PATTERN =
            Pattern.compile("(\\d+)\\s+de\\s+(\\d{2}/\\d{2}/\\d{4})(?:,\\s*em\\s*<i>\"([^\"]*)\"</i>)?");

    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getEstado() {
        return "PI";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> publicacoes = new ArrayList<>();
        LocalDate dataLimite = LocalDate.parse(dataInicio);

        try {
            Connection.Response response = Jsoup.connect(BUSCA_URL)
                    .method(Connection.Method.POST)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("filter_texto", palavrasChave)
                    .timeout(60_000)
                    .ignoreContentType(true)
                    .execute();

            if (response.statusCode() != 200) {
                LOG.warnf("PI retornou status %d", response.statusCode());
                return publicacoes;
            }

            PiauiApiResponse resposta = objectMapper.readValue(response.body(), PiauiApiResponse.class);

            if (resposta == null || resposta.resposta == null) {
                return publicacoes;
            }

            for (PiauiApiResponse.Resultado resultado : resposta.resposta) {
                PublicacaoScraped publicacao = parsearResultado(resultado, dataLimite);
                if (publicacao != null) {
                    publicacoes.add(publicacao);
                }
            }

        } catch (Exception e) {
            LOG.errorf(e, "Erro ao buscar diário do PI");
        }

        return publicacoes;
    }

    private PublicacaoScraped parsearResultado(PiauiApiResponse.Resultado resultado, LocalDate dataLimite) {
        if (resultado.dadosDiario == null || resultado.anexoDiario == null) {
            return null;
        }

        Matcher matcher = DADOS_DIARIO_PATTERN.matcher(resultado.dadosDiario);
        if (!matcher.find()) {
            LOG.warnf("Não foi possível parsear dadosDiario do PI: %s", resultado.dadosDiario);
            return null;
        }

        String numeroEdicao = matcher.group(1);
        String dataTexto = matcher.group(2);
        String categoria = matcher.group(3) != null ? matcher.group(3) : "";

        LocalDate dataPublicacao;
        try {
            dataPublicacao = LocalDate.parse(dataTexto, FORMATO_DATA_BR);
        } catch (Exception e) {
            LOG.warnf("Data inválida no resultado do PI: %s", dataTexto);
            return null;
        }


        if (dataPublicacao.isBefore(dataLimite)) {
            return null;
        }

        String link = ANEXO_BASE_URL + resultado.anexoDiario;

        return new PublicacaoScraped(
                "Diário Oficial do Piauí - Edição " + numeroEdicao + (categoria.isEmpty() ? "" : " (" + categoria + ")"),
                "",
                link,
                dataTexto,
                "PI",
                numeroEdicao,
                "",
                "PIAUI_API"
        );
    }
}
