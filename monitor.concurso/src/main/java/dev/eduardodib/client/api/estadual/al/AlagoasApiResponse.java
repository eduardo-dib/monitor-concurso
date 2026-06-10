package dev.eduardodib.client.api.estadual.al;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AlagoasApiResponse {

    public String status;
    public Result result;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        public List<Item> items;
        public int total;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("edition_id")
        public long editionId;

        @JsonProperty("publication_date")
        public String publicationDate;

        @JsonProperty("page_number")
        public int pageNumber;

        public long id;

        @JsonProperty("edition_number")
        public int editionNumber;

        @JsonProperty("edition_type_name")
        public String editionTypeName;

        @JsonProperty("is_suplement")
        public boolean isSuplement;

        public List<String> highlight;
    }
}
