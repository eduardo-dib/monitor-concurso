package dev.eduardodib.client.api.estadual.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdicaoDTO {
    public long id;
    public String data;
    public long numero;

    @JsonProperty("Paginas")
    public List<PaginaDTO> paginas;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaginaDTO {
        public int pagina;
        public String url;
    }
}

