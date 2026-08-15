package dev.eduardodib.client.api.estadual.es;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.client.api.estadual.go.GoiasApiResponse;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EspiritoSantoApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(EspiritoSantoApiIntegration.class);
    private static final int PAGE_SIZE = 20;
    private static final String BASE_URL = "https://api.ioes.dio.es.gov.br";

    @Inject
    @RestClient
    EspiritoSantoApiRequest apiRequest;

    @Override
    public String getEstado() {
        return "ES";
    }

    private static final long DIARIO_ID = 1; // ainda não confirmado para o ES - ver observação abaixo

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> publicacoes = new ArrayList<>();
        int pagina = 1;
        String dataFim = LocalDate.now().toString();

        try {
            while (true) {
                GoiasApiResponse resposta = apiRequest.buscar(palavrasChave, pagina, PAGE_SIZE, dataInicio, dataFim);

                if (resposta == null || resposta.resultados == null || resposta.resultados.isEmpty()) {
                    break;
                }

                for (GoiasApiResponse.Resultado resultado : resposta.resultados) {
                    PublicacaoScraped publicacao = montarPublicacao(resultado, palavrasChave);
                    if (publicacao != null) {
                        publicacoes.add(publicacao);
                    }
                }

                if (resposta.resultados.size() < PAGE_SIZE) {
                    break;
                }

                pagina++;
            }
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao buscar diário do ES");
        }

        return publicacoes;
    }

    private PublicacaoScraped montarPublicacao(GoiasApiResponse.Resultado resultado, String termo) {
        EdicaoDTO edicao = resolverEdicao(resultado.data, resultado.edicaoNumero);

        String link = "";
        if (edicao != null && edicao.paginas != null) {
            link = edicao.paginas.stream()
                    .filter(p -> p.pagina == resultado.pagina)
                    .map(p -> p.url)
                    .findFirst()
                    .orElse("");
        }

        return new PublicacaoScraped(
                "Diário Oficial do Espírito Santo - Edição " + resultado.edicaoNumero,
                resultado.highlight,
                link,
                resultado.data,
                "ES",
                String.valueOf(resultado.edicaoNumero),
                String.valueOf(resultado.pagina),
                "ESPIRITOSANTO_API"
        );
    }

    private EdicaoDTO resolverEdicao(String data, long edicaoNumero) {
        try {
            List<EdicaoDTO> resposta = apiRequest.buscarEdicoes(DIARIO_ID, data, String.valueOf(edicaoNumero));
            if (resposta != null && !resposta.isEmpty()) {
                return resposta.get(0);
            }
        } catch (Exception e) {
            LOG.warnf("Não foi possível resolver a edição %d (%s) do ES: %s", edicaoNumero, data, e.getMessage());
        }
        return null;
    }
}
