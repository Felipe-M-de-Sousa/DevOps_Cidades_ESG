package br.com.fiap.ecosmart.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leituras_consumo")
public class Leitura extends EcoDocument {
    private String sensorId;
    private String unidadeId;
    private String tipoMedida;
    private Double valor;
    private java.time.Instant timestamp;

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getUnidadeId() { return unidadeId; }
    public void setUnidadeId(String unidadeId) { this.unidadeId = unidadeId; }
    public String getTipoMedida() { return tipoMedida; }
    public void setTipoMedida(String tipoMedida) { this.tipoMedida = tipoMedida; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public java.time.Instant getTimestamp() { return timestamp; }
    public void setTimestamp(java.time.Instant timestamp) { this.timestamp = timestamp; }
}
