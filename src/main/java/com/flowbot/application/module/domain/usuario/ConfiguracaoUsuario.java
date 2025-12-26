package com.flowbot.application.module.domain.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static com.flowbot.application.utils.Utils.nullOrToday;

@Document
public class ConfiguracaoUsuario {
    @Id
    private String id;
    private String logoUrl;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dataCriacao;

    public ConfiguracaoUsuario(String id, String logoUrl, String name, LocalDateTime dataCriacao) {
        this.id = id;
        this.logoUrl = logoUrl;
        this.name = name;
        this.dataCriacao = nullOrToday(dataCriacao);
    }

    public String getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void atualizar(String logoUrl, String name) {
        this.logoUrl = logoUrl;
        this.name = name;
    }
}

