package dev.eduardodib.client.api.estadual.ce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CearaApiResponse {

    public Pagination pagination;

    @JsonProperty("terms_used")
    public List<String> termosUsados;

    public List<Resultado> results;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pagination {
        @JsonProperty("limit_value")
        public int limitValue;

        @JsonProperty("total_pages")
        public int totalPages;

        @JsonProperty("current_page")
        public int currentPage;

        @JsonProperty("next_page")
        public Integer nextPage; // Integer (não int) — vem null na última página

        @JsonProperty("total_count")
        public int totalCount;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resultado {
        public long id;
        public int year;
        public String date;

        @JsonProperty("journal_number")
        public int journalNumber;

        @JsonProperty("original_filename")
        public String originalFilename;

        public int page;
        public List<String> snippets;
        public int occurrences;

        @JsonProperty("created_at")
        public String createdAt;
    }
}