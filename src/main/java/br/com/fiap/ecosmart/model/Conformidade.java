package br.com.fiap.ecosmart.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "conformidade_governanca")
public class Conformidade extends EcoDocument {
    private String unidadeId;
    private String tipo;
    private String status;

    public String getUnidadeId() { return unidadeId; }
    public void setUnidadeId(String unidadeId) { this.unidadeId = unidadeId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
