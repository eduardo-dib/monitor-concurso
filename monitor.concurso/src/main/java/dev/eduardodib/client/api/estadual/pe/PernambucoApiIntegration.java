package dev.eduardodib.client.api.estadual.pe;

import dev.eduardodib.client.api.DiarioOficialClient;
import dev.eduardodib.scraper.DiarioOficialScraper.PublicacaoScraped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PernambucoApiIntegration implements DiarioOficialClient {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int PAGE_SIZE = 10;

    @Inject
    @RestClient
    PernambucoApiRequest pernambucoApiRequest;

    @Override
    public String getEstado() {
        return "PE";
    }

    @Override
    public List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio) {
        List<PublicacaoScraped> resultado = new ArrayList<>();

        String dataInicial = LocalDate.parse(dataInicio).format(FORMATO_DATA);
        String dataFinal = LocalDate.now().format(FORMATO_DATA);
        String intervalo = dataInicial + "-" + dataFinal;

        int first = 0;
        int total = Integer.MAX_VALUE;

        while (first < total) {
            PernambucoSearchBody body = new PernambucoSearchBody(
                    first,
                    PAGE_SIZE,
                    Map.of(),
                    Map.of(),
                    java.time.Instant.now().toString(),
                    "2020-01-01T03:00:00.000Z",
                    "2029-01-01T03:00:00.000Z",
                    palavrasChave,
                    dataInicial,
                    dataFinal,
                    intervalo,
                    "1"
            );

            PernambucoApiResponse response = pernambucoApiRequest.buscar(body);

            if (response == null || response.list() == null || response.list().isEmpty()) {
                break;
            }

            total = response.rowCount();

            for (PernambucoApiResponse.Item item : response.list()) {
                resultado.add(new PublicacaoScraped(
                        item.titulo(),
                        limparHighlight(item.texto()),
                        montarLink(item.codigo()),
                        item.dataPublicacao(),
                        "PE",
                        null,
                        null,
                        "CEPE"
                ));
            }

            first += PAGE_SIZE;
        }

        return resultado;
    }

    private String limparHighlight(String texto) {
        return texto == null ? null : texto.replaceAll("<[^>]*>", "");
    }


    private String montarLink(long codigo) {
        return "https://diariooficial.cepe.com.br/diariooficialweb/#/publicacao/" + codigo;
    }
}
