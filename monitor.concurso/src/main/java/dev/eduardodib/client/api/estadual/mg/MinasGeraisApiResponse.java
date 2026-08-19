package dev.eduardodib.client.api.estadual.mg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinasGeraisApiResponse {

    public List<Resultado> dados;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resultado {
        @JsonProperty("idJornal")
        public long idJornal;

        @JsonProperty("dataPublicacao")
        public String dataPublicacao;

        @JsonProperty("tipoCaderno")
        public String tipoCaderno;

        @JsonProperty("textoResultado")
        public String textoResultado;

        public int pagina;
    }
}
