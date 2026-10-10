package br.com.fiap.ecosmart.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Mostra em qual ambiente a aplicacao esta rodando (local, staging, production). */
@RestController
@RequestMapping("/api/ambiente")
public class AmbienteController {

    private final String ambiente;

    public AmbienteController(@Value("${ecosmart.ambiente}") String ambiente) {
        this.ambiente = ambiente;
    }

    @GetMapping
    public Map<String, String> info() {
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("aplicacao", "EcoSmart Porto Verde - Cidades ESG Inteligentes");
        resposta.put("ambiente", ambiente);
        resposta.put("versao", "1.0.0");
        return resposta;
    }
}
