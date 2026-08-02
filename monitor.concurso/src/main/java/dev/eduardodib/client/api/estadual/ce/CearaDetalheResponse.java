package dev.eduardodib.client.api.estadual.ce;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CearaDetalheResponse {

    @JsonProperty("image_url")
    public String imageUrl;

    @JsonProperty("download_url")
    public String downloadUrl;

    // No resumo ficou registrado como "content/raw_content" — nome exato incerto,
    // provavelmente um dos dois. Mapeando os dois pra garantir que um deles pega:
    public String content;

    @JsonProperty("raw_content")
    public String rawContent;
}
