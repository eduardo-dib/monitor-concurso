package dev.eduardodib.client.api.municipal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    @JsonProperty("total_gazettes")
    public int total;

    public List<Gazette> gazettes;

    @JsonIgnoreProperties(ignoreUnknown = true)
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

        @JsonProperty("excerpts")
        public List<String> trechosDestacados;

        @JsonProperty("txt_url")
        public String txtUrl;
    }
}