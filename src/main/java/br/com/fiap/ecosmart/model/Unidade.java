package br.com.fiap.ecosmart.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "unidades_organizacionais")
public class Unidade extends EcoDocument {
    private String nome;
    private String tipo;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
