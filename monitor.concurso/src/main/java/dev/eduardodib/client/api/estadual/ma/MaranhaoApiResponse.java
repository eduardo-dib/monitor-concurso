package dev.eduardodib.client.api.estadual.ma;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MaranhaoApiResponse {

    public Busca busca;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Busca {
        public boolean erro;

        @JsonProperty("scrollId")
        public String scrollId;

        @JsonProperty("es_html")
        public String esHtml; // fragmentos de HTML crus, um <div class="card"> por publicação
    }
}
