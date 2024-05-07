package com.flowbot.application.module.domain.numeros;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static com.flowbot.application.utils.Utils.nullOrToday;
import static com.flowbot.application.utils.Utils.nullOrValue;

@Document
public class Numero {
    @Id
    private String id;
    private String nick;
    private String numero;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dataCriacao;
    private StatusNumero statusNumero;
    private String whatsappInternalId;

    public Numero(String id,
                  String nick,
                  LocalDateTime dataCriacao,
                  StatusNumero statusNumero,
                  String numero,
                  String whatsappInternalId) {
        this.id = id;
        this.nick = nick;
        this.dataCriacao = nullOrToday(dataCriacao);
        this.statusNumero = (StatusNumero) nullOrValue(statusNumero, StatusNumero.CRIADO);
        this.whatsappInternalId = whatsappInternalId;
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }

    public String getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public StatusNumero getStatusNumero() {
        return statusNumero;
    }

    public String getWhatsappInternalId() {
        return whatsappInternalId;
    }

    public void atualizarStatus(final StatusNumero novoStatus) {
        statusNumero = novoStatus;
    }

    public void atualizarWhatsappInternalId(final String whatsappInternalId) {
        this.whatsappInternalId = whatsappInternalId;
    }
}
