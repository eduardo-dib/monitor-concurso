package dev.eduardodib.client.api.estadual.go;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GoiasApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(GoiasApiIntegration.class);
    private static final int PAGE_SIZE = 20;
    private static final String BASE_URL = "https://api.diariooficial.abc.go.gov.br";

    @Inject
    @RestClient
    GoiasApiRequest goiasApiRequest;

    @Override
    public String getEstado() {
        return "GO";
    }

    @Override
    public List<DiarioOficialScraper.PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<DiarioOficialScraper.PublicacaoScraped> resultado = new ArrayList<>();
        int pagina = 1;
        String dataFim = LocalDate.now().toString();

        try {
            LOG.infof("[GOIAS] Buscando termo='%s', dataInicio='%s', dataFim='%s'", palavrasChave, dataInicio, dataFim);
            while (true) {
                GoiasApiResponse response = goiasApiRequest.buscar(
                        palavrasChave, dataInicio, dataFim, pagina, PAGE_SIZE
                );

                if (response == null || response.resultados == null || response.resultados.isEmpty()) break;

                LOG.infof("[GOIAS] Página %d — %d resultados", pagina, response.resultados.size());

                for (GoiasApiResponse.Resultado r : response.resultados) {
                    String link = BASE_URL + "/transparencia/v1/diarios/1/edicoes/" + r.edicaoNumero + "/materias/" + r.protocolo;
                    String conteudo = r.highlight != null ? r.highlight.replaceAll("<[^>]*>", "") : "";

                    resultado.add(new DiarioOficialScraper.PublicacaoScraped(
                            "Diário Oficial GO - Edição " + r.edicaoNumero + " - Pág " + r.pagina,
                            conteudo,
                            link,
                            r.data,
                            "GO",
                            String.valueOf(r.edicaoNumero),
                            String.valueOf(r.pagina),
                            "GOIAS_API"
                    ));
                }

                if (response.resultados.size() < PAGE_SIZE) break;
                pagina++;
            }

            LOG.infof("[GOIAS] Total: %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[GOIAS] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }
}
