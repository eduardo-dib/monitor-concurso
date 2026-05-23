package dev.eduardodib.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ApiResponse {

    public int total;
    public List<Gazette> gazettes;

    public static class Gazette {

        @JsonProperty("territory_id")
        public String territorioId;

        @JsonProperty("state_code")
        public String estado;

        public String date;
        public String url;
        public String edition;

        @JsonProperty("is_extra_edition")
        public boolean edicaoExtra;

        public String power;

        @JsonProperty("highlight_texts")
        public List<String> trechosDestacados;

        @JsonProperty("txt_url")
        public String txtUrl;
    }
}
