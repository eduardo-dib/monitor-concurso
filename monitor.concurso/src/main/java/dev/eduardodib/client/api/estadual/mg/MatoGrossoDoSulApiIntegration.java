package dev.eduardodib.client.api.estadual.mg;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MatoGrossoDoSulApiIntegration implements DiarioOficialClient {

    private static final Logger LOG = Logger.getLogger(MatoGrossoDoSulApiIntegration.class);
    private static final int PAGE_SIZE = 10;

    @Inject
    @RestClient
    MatoGrossoDoSulApiRequest msApiRequest;

    @Override
    public String getEstado() {
        return "MS";
    }

    @Override
    public List<DiarioOficialScraper.PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<DiarioOficialScraper.PublicacaoScraped> resultado = new ArrayList<>();
        int pagina = 1;
        String dataFim = LocalDate.now().toString();

        try {
            while (true) {
                MatoGrossoDoSulApiResponse response = msApiRequest.buscar(
                        1, palavrasChave, dataInicio, dataFim, pagina, PAGE_SIZE
                );

                if (response == null || response.paginasDiario == null || response.paginasDiario.isEmpty()) break;

                LOG.infof("[MS] Página %d de %d — %d registros", pagina, response.totalDePaginas, response.paginasDiario.size());

                for (MatoGrossoDoSulApiResponse.PaginaDiario p : response.paginasDiario) {
                    String conteudo = "";
                    if (p.hiHighlight != null && p.hiHighlight.texto != null && !p.hiHighlight.texto.isEmpty()) {
                        conteudo = p.hiHighlight.texto.stream()
                                .map(t -> t.replaceAll("<[^>]*>", ""))
                                .reduce("", (a, b) -> a + " | " + b);
                    }

                    String data = p.dataPublicacao != null
                            ? LocalDateTime.parse(p.dataPublicacao).toLocalDate().toString()
                            : LocalDate.now().toString();

                    resultado.add(new DiarioOficialScraper.PublicacaoScraped(
                            "Diário Oficial MS - Edição " + p.numero + " - Pág " + p.pagina,
                            conteudo,
                            p.caminhoArquivo,
                            data,
                            "MS",
                            String.valueOf(p.numero),
                            String.valueOf(p.pagina),
                            "MATOGROSSODOSUL_API"
                    ));
                }

                if (pagina >= response.totalDePaginas) break;
                pagina++;
            }

            LOG.infof("[MS] Total: %d publicações para '%s'", resultado.size(), palavrasChave);

        } catch (Exception e) {
            LOG.errorf(e, "[MS] Erro ao buscar publicações para '%s'", palavrasChave);
        }

        return resultado;
    }
}
