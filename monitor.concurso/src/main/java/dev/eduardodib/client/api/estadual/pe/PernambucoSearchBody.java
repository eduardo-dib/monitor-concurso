package dev.eduardodib.client.api.estadual.pe;


import java.util.Map;

public record PernambucoSearchBody(
        int first,
        int maxResults,
        Map<String, Object> restricoes,
        Map<String, Object> order,
        String data,
        String minDate,
        String maxDate,
        String palavras,
        String dataInicial,
        String dataFinal,
        String intervaloAno,
        String codigoDiario
) {}