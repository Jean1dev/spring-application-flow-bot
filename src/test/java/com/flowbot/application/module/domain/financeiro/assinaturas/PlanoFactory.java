package com.flowbot.application.module.domain.financeiro.assinaturas;

public final class PlanoFactory {

    public static Plano umPlanoMensal(String email) {
        return Plano.criarPlanoPadrao(email, PeriodoPlano.MENSAL);
    }
}
