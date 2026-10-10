package br.com.fiap.ecosmart.model;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;

/**
 * Base de todos os documentos. O mapa "atributos" guarda os campos que mudam
 * de documento para documento (schema flexivel do MongoDB).
 */
public abstract class EcoDocument {

    @Id
    private String id;

    private Map<String, Object> atributos = new HashMap<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Map<String, Object> getAtributos() { return atributos; }
    public void setAtributos(Map<String, Object> atributos) { this.atributos = atributos; }
}
