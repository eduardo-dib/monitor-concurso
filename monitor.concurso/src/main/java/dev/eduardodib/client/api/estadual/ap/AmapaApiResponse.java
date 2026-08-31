package dev.eduardodib.client.api.estadual.ap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmapaApiResponse {
    public Hits hits;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hits {
        public int total;
        public List<Hit> hits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hit {
        public Source _source;
        public Highlight highlight;
        public String diario;
        public String suplemento;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {
        public String conteudo;
        public String data;
        public int pagina;
        public int paginas;
        public long pdf_id;
        public long diario_id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Highlight {
        public List<String> conteudo;
    }
}