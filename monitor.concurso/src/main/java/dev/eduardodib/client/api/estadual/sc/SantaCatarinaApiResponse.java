package dev.eduardodib.client.api.estadual.sc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SantaCatarinaApiResponse {

    public int total;
    public List<Materia> materias;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Materia {
        public long id;
        public String nrJornal;
        public long cdJornal;
        public String publicacao;
        public String categoria;
        public String assunto;
        public String extrato;
        public String preview;
        public String resumo;
    }
}