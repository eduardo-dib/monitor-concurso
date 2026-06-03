package dev.eduardodib.client.api.estadual.mt;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.client.api.estadual.go.GoiasApiResponse;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MatoGrossoApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(MatoGrossoApiIntegration.class);
    private static final int PAGE_SIZE = 20;
    private static final String BASE_URL = "https://api.iomat.mt.gov.br";

    @Inject
    @RestClient
    MatoGrossoApiRequest matoGrossoApiRequest;

    @Override
    public String getEstado() {
        return "MT";
    }

    @Override
    public List<DiarioOficialScraper.PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<DiarioOficialScraper.PublicacaoScraped> resultado = new ArrayList<>();
        int pagina = 1;
        String dataFim = LocalDate.now().toString();

        try {
            while (true) {
                GoiasApiResponse response = matoGrossoApiRequest.buscar(
                        palavrasChave, dataInicio, dataFim, pagina, PAGE_SIZE
                );

                if (response == null || response.resultados == null || response.resultados.isEmpty()) break;

                LOG.infof("[MT] Página %d — %d resultados", pagina, response.resultados.size());

                for (GoiasApiResponse.Resultado r : response.resultados) {
                    String link = BASE_URL + "/transparencia/v1/diarios/1/edicoes/" + r.edicaoNumero + "/materias/" + r.protocolo;
                    String conteudo = r.highlight != null ? r.highlight.replaceAll("<[^>]*>", "") : "";

                    resultado.add(new DiarioOficialScraper.PublicacaoScraped(
                            "Diário Oficial MT - Edição " + r.edicaoNumero + " - Pág " + r.pagina,
                            conteudo,
                            link,
                            r.data,
                            "MT",
                            String.valueOf(r.edicaoNumero),
                            String.valueOf(r.pagina),
                            "MATOGROSSO_API"
                    ));
                }

                if (response.resultados.size() < PAGE_SIZE) break;
                pagina++;
            }

            LOG.infof("[MT] Total: %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[MT] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }
}
