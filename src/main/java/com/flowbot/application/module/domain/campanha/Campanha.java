package com.flowbot.application.module.domain.campanha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.flowbot.application.utils.Utils.nullOrValue;

@Document
public class Campanha {
    @Id
    private String id;
    @NotBlank(message = "Titulo não pode ser vazio ou nulo")
    private String titulo;
    @NotBlank(message = "Numero não pode ser vazio ou nulo")
    private String numeroIdRef;
    private List<String> numerosParaDisparo;
    @NotNull(message = "Categoria não pode ser vazia ou nula")
    private CategoriaCampanha categoria;
    private StatusCampanha status;
    private List<String> arquivosUrls;
    @Transient
    private List<String> validateMessages;

    public Campanha(String id,
                    String titulo,
                    String numeroIdRef,
                    List<String> numerosParaDisparo,
                    CategoriaCampanha categoria,
                    StatusCampanha status,
                    List<String> arquivosUrls) {
        this.id = id;
        this.titulo = titulo;
        this.numeroIdRef = numeroIdRef;
        this.numerosParaDisparo = numerosParaDisparo;
        this.categoria = categoria;
        this.status = (StatusCampanha) nullOrValue(status, StatusCampanha.ATIVO);
        this.arquivosUrls = arquivosUrls;
        validateMessages = new ArrayList<>();
    }

    public boolean validate() {
        var isValid = true;
        if (Objects.isNull(titulo) || titulo.isBlank()) {
            validateMessages.add("Titulo não pode ser vazio ou nulo");
            isValid = false;
        }

        if (Objects.isNull(categoria)) {
            validateMessages.add("Categoria não pode ser vazia ou nula");
            isValid = false;
        }

        return isValid;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getNumeroIdRef() {
        return numeroIdRef;
    }

    public List<String> getNumerosParaDisparo() {
        return numerosParaDisparo;
    }

    public List<String> getValidateMessages() {
        return validateMessages;
    }

    public CategoriaCampanha getCategoria() {
        return categoria;
    }

    public StatusCampanha getStatus() {
        return status;
    }

    public List<String> getArquivosUrls() {
        return arquivosUrls;
    }
}
