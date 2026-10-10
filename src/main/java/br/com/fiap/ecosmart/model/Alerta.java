package br.com.fiap.ecosmart.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "alertas_automaticos")
public class Alerta extends EcoDocument {
    private String sensorId;
    private String unidadeId;
    private String tipoAlerta;
    private String severidade;
    private String status;

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getUnidadeId() { return unidadeId; }
    public void setUnidadeId(String unidadeId) { this.unidadeId = unidadeId; }
    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }
    public String getSeveridade() { return severidade; }
    public void setSeveridade(String severidade) { this.severidade = severidade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
