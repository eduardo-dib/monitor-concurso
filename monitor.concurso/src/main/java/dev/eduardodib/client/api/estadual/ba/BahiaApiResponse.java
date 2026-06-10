package dev.eduardodib.client.api.estadual.ba;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BahiaApiResponse {

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
        public String suplemento;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {
        public String data;
        public int pagina;
        public long diario_id;
        public long pdf_id;
        public String year;
        public String month;
        public String day;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Highlight {
        public List<String> conteudo;
    }
}
