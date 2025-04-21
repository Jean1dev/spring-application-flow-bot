package com.flowbot.application.module.domain.financeiro.assinaturas;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class Acesso {
    @Id
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dataAcesso;
    private String origem;
    private String localizacao;
    private String planoRef;

    public Acesso(String origem, String localizacao, String planoRef) {
        this.origem = origem;
        this.localizacao = localizacao;
        this.planoRef = planoRef;
        this.dataAcesso = LocalDateTime.now();
    }

    public String getPlanoRef() {
        return planoRef;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDataAcesso() {
        return dataAcesso;
    }

    public String getOrigem() {
        return origem;
    }

    public String getLocalizacao() {
        return localizacao;
    }
}
