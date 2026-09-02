package dev.eduardodib.client.api.estadual.am;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmazonasApiResponse(Data data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(List<Item> resultados, int total) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String highlight,
            @JsonProperty("publicado_em") String publicadoEm,
            @JsonProperty("edicao_id") long edicaoId,
            @JsonProperty("edicao_numero") String edicaoNumero,
            @JsonProperty("edicao_pagina") int edicaoPagina,
            @JsonProperty("materia_titulo") String materiaTitulo
    ) {}
}
