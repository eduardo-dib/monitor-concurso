package dev.eduardodib.client.api.estadual.rn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RioGrandeNorteApiResponse {
    public List<Item> list;
    public int rowCount;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        public long codigo;
        public String titulo;      // pode vir nulo em alguns itens (ex: "Poder Executivo > Atos")
        public String texto;
        public String resumo;
        public String nomeCategoria;
        public String dataPublicacao;  // formato "yyyy-MM-dd"
    }
}
