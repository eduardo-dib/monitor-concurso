package dev.eduardodib.client.api.estadual.mg;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MinasGeraisApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(MinasGeraisApiIntegration.class);
    private static final int PAGE_SIZE = 20;

    @Inject
    @RestClient
    MinasGeraisApiRequest apiRequest;

    @Inject
    @RestClient
    MinasGeraisAutenticacaoRequest autenticacaoRequest;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getEstado() {
        return "MG";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> publicacoes = new ArrayList<>();

        String authorization = autenticar();
        if (authorization == null) {
            return publicacoes;
        }

        int pagina = 1;
        String dataFim = LocalDate.now().toString();

        try {
            while (true) {
                MinasGeraisApiResponse resposta = apiRequest.buscar(
                        authorization,
                        dataInicio, dataFim, palavrasChave,
                        true, false, false, false,
                        pagina, PAGE_SIZE
                );

                if (resposta == null || resposta.dados == null || resposta.dados.isEmpty()) {
                    break;
                }

                for (MinasGeraisApiResponse.Resultado resultado : resposta.dados) {
                    publicacoes.add(montarPublicacao(resultado, palavrasChave));
                }

                if (resposta.dados.size() < PAGE_SIZE) {
                    break;
                }

                pagina++;
            }
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao buscar diário do MG");
        }

        return publicacoes;
    }

    private String autenticar() {
        try {
            MinasGeraisAutenticacaoResponse resposta = autenticacaoRequest.autenticar(Collections.emptyMap());

            if (resposta == null || resposta.dados == null || resposta.dados.isBlank()) {
                LOG.warn("MG: autenticação não retornou token");
                return null;
            }

            if (resposta.erros != null && !resposta.erros.isEmpty()) {
                LOG.warnf("MG: autenticação retornou erros: %s", resposta.erros);
                return null;
            }

            return "Bearer " + resposta.dados;
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao autenticar no MG");
            return null;
        }
    }

    private PublicacaoScraped montarPublicacao(MinasGeraisApiResponse.Resultado resultado, String termo) {
        return new PublicacaoScraped(
                "Diário Oficial de Minas Gerais - " + resultado.tipoCaderno,
                resultado.textoResultado,
                montarLink(resultado, termo),
                resultado.dataPublicacao,
                "MG",
                String.valueOf(resultado.idJornal),
                String.valueOf(resultado.pagina),
                "Diário Oficial de Minas Gerais"
        );
    }

    private String montarLink(MinasGeraisApiResponse.Resultado resultado, String termo) {
        try {
            Map<String, Object> dados = new LinkedHashMap<>();
            dados.put("dataPublicacaoSelecionada", resultado.dataPublicacao);
            dados.put("idCadernoEdicaoSelecionado", resultado.idJornal);
            dados.put("paginaSelecionada", resultado.pagina);
            dados.put("textoPesquisa", termo);

            String json = objectMapper.writeValueAsString(dados);
            String jsonEncoded = URLEncoder.encode(json, StandardCharsets.UTF_8);

            return "https://www.jornalminasgerais.mg.gov.br/edicao-do-dia?dados=" + jsonEncoded;
        } catch (Exception e) {
            LOG.warnf("Falha ao montar link do MG para idJornal=%d: %s", resultado.idJornal, e.getMessage());
            return "";
        }
    }
}