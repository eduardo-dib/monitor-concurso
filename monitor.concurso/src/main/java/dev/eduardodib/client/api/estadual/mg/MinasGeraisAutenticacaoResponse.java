package dev.eduardodib.client.api.estadual.mg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinasGeraisAutenticacaoResponse {
    public String dados;
    public List<String> erros;
}