package dev.eduardodib.client.api;

import dev.eduardodib.scraper.DiarioOficialScraper;

import java.util.List;

public interface DiarioOficialClient {

    String getEstado();

    List<DiarioOficialScraper.PublicacaoScraped> buscar(String palavrasChave, String dataInicio);
}