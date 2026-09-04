package dev.eduardodib.client.api.estadual.pe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PernambucoApiResponse(List<Item> list, int rowCount) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            long codigo,
            String titulo,
            String texto,
            String nomeCategoria,
            String dataPublicacao
    ) {}
}