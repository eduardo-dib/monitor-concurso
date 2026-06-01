package dev.eduardodib.client.api.estadual.go;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GoiasApiResponse {

    public int total;
    public int pagina;
    public List<Resultado> resultados;

    public static class Resultado {
        public String highlight;
        public long protocolo;

        @JsonProperty("edicao_numero")
        public int edicaoNumero;

        public String data;
        public int pagina;
        public String diario;
        public String suplemento;
    }
}
