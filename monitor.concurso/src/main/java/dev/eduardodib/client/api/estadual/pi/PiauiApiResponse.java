package dev.eduardodib.client.api.estadual.pi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PiauiApiResponse {

    public List<Resultado> resposta;

    @JsonProperty("palavraschave")
    public List<String> palavrasChave;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resultado {
        @JsonProperty("dadosDiario")
        public String dadosDiario;

        public String nota;

        @JsonProperty("anexodiario")
        public String anexoDiario;

        public String acertos;
    }
}
