package com.flowbot.application.module.domain.financeiro.assinaturas;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document
public class Plano {
    @Id
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dataCriacao;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime finalizaEm;
    private PeriodoPlano periodoPlano;
    private UsuarioDoPlano usuario;
    private Boolean ativo;
    private String informacaoPagamentoExterno;

    public static Plano criarPlanoPadrao(String email, PeriodoPlano periodoPlano) {
        var plano = new Plano();
        plano.dataCriacao = LocalDateTime.now();
        plano.periodoPlano = periodoPlano;
        plano.usuario = new UsuarioDoPlano(email, email, null);
        plano.ativo = true;
        plano.informacaoPagamentoExterno = "Hotmart";
        plano.validar();
        plano.calcularTermino();
        return plano;
    }

    private void validar() {
        if (Objects.isNull(this.usuario) || Objects.isNull(this.usuario.email()) || this.usuario.email().isEmpty()) {
            throw new IllegalArgumentException("Email do usuário não pode ser nulo ou vazio");
        }

        if (Objects.isNull(this.periodoPlano)) {
            throw new IllegalArgumentException("Período do plano não pode ser nulo");
        }
    }

    private void calcularTermino() {
        switch (periodoPlano) {
            case MENSAL -> finalizaEm = dataCriacao.plusMonths(1);
            case ANUAL -> finalizaEm = dataCriacao.plusYears(1);
        }
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getFinalizaEm() {
        return finalizaEm;
    }

    public PeriodoPlano getPeriodoPlano() {
        return periodoPlano;
    }

    public UsuarioDoPlano getUsuario() {
        return usuario;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}
