package dev.eduardodib.client.api.estadual.am;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AmazonasApiIntegration implements DiarioOficialClient {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Inject
    @RestClient
    AmazonasApiRequest amazonasApiRequest;

    @Override
    public String getEstado() {
        return "AM";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        String dataInit = LocalDate.parse(dataInicio).format(FORMATO_DATA);
        String dataEnd = LocalDate.now().format(FORMATO_DATA);

        int pagina = 1;
        int totalAcumulado = 0;

        while (true) {
            AmazonasApiResponse response = amazonasApiRequest.buscar(pagina, palavrasChave, 1, dataInit, dataEnd);

            if (response == null || response.data() == null
                    || response.data().resultados() == null || response.data().resultados().isEmpty()) {
                break;
            }

            for (AmazonasApiResponse.Item item : response.data().resultados()) {
                resultado.add(new PublicacaoScraped(
                        item.materiaTitulo(),
                        limparHighlight(item.highlight()),
                        montarLink(item.edicaoId(), item.edicaoPagina()),
                        item.publicadoEm(),
                        "AM",
                        item.edicaoNumero(),
                        String.valueOf(item.edicaoPagina()),
                        "AMAZONAS_API"
                ));
            }

            totalAcumulado += response.data().resultados().size();
            if (totalAcumulado >= response.data().total()) {
                break;
            }
            pagina++;
        }

        return resultado;
    }

    private String limparHighlight(String highlight) {
        return highlight == null ? null : highlight.replaceAll("<[^>]*>", "");
    }

    private String montarLink(long edicaoId, int pagina) {
        return "https://diario.imprensaoficial.am.gov.br/portal/visualizacoes/pdf/" + edicaoId
                + "#/p:" + pagina + "/e:" + edicaoId + "?find=";
    }
}
