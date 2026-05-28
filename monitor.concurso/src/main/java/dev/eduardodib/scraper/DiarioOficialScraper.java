package dev.eduardodib.scraper;

import java.util.List;

public interface DiarioOficialScraper {

    String getEstado();

    List<PublicacaoScraped> buscar(String palavrasChave, String dataInicio);

    record PublicacaoScraped(
            String titulo,
            String conteudo,
            String link,
            String data,
            String estado
    ) {}
}