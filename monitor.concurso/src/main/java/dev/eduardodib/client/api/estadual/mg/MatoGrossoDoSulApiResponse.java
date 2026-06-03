package dev.eduardodib.client.api.estadual.mg;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MatoGrossoDoSulApiResponse {

    public int paginaAtual;
    public int totalDePaginas;
    public int totalDeRegistros;
    public List<PaginaDiario> paginasDiario;

    public static class PaginaDiario {
        public int numero;
        public String descricao;
        public int pagina;
        public String caminhoArquivo;
        public String nomeArquivo;
        public String dataPublicacao;
        public HiHighlight hiHighlight;
        public int ocorrenciasPorPagina;
    }

    public static class HiHighlight {
        public List<String> texto;
    }
}
