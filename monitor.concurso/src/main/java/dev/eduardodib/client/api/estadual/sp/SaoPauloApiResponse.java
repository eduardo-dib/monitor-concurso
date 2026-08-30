package dev.eduardodib.client.api.estadual.sp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SaoPauloApiResponse(
        List<Item> items,
        int currentPage,
        int totalPages,
        boolean hasNextPage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String id,
            String date,
            String title,
            String slug,
            String excerpt,
            String hierarchy
    ) {}
}
