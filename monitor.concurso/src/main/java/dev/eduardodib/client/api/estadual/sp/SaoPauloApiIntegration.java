package dev.eduardodib.client.api.estadual.sp;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SaoPauloApiIntegration implements DiarioOficialClient {

    private static final int PAGE_SIZE = 20;

    @Inject
    @RestClient
    SaoPauloApiRequest saoPauloApiRequest;

    @Override
    public String getEstado() {
        return "SP";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        String fromDate = formatarData(LocalDate.parse(dataInicio));
        String toDate = formatarData(LocalDate.now());

        int pagina = 1;
        while (true) {
            SaoPauloApiResponse response = saoPauloApiRequest.buscar(
                    "personalized",
                    pagina,
                    palavrasChave,
                    fromDate,
                    toDate,
                    PAGE_SIZE,
                    "Date"
            );

            if (response == null || response.items() == null || response.items().isEmpty()) {
                break;
            }

            for (SaoPauloApiResponse.Item item : response.items()) {
                resultado.add(new PublicacaoScraped(
                        item.title(),
                        item.excerpt(),
                        montarLink(item.slug()),
                        extrairData(item.date()),
                        "SP",
                        null,
                        null,
                        "SAOPAULO_API"
                ));
            }

            if (!response.hasNextPage()) {
                break;
            }
            pagina++;
        }

        return resultado;
    }


    private String formatarData(LocalDate data) {
        return data.getYear() + "-" + data.getMonthValue() + "-" + data.getDayOfMonth();
    }


    private String extrairData(String dataHora) {
        return dataHora != null && dataHora.length() >= 10 ? dataHora.substring(0, 10) : null;
    }


    private String montarLink(String slug) {
        return "https://doe.sp.gov.br/" + slug;
    }
}
