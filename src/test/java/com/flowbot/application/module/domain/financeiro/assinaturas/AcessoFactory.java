package com.flowbot.application.module.domain.financeiro.assinaturas;

import java.util.ArrayList;
import java.util.List;

public final class AcessoFactory {

    public static Acesso umAcessoWeb(String planoRef) {
        return new Acesso("web", "192.168.0.1", planoRef);
    }

    public static Acesso umAcessoMobile(String planoRef) {
        return new Acesso("mobile", "192.168.0.2", planoRef);
    }

    public static Acesso umAcessoDesktop(String planoRef) {
        return new Acesso("desktop", "192.168.0.3", planoRef);
    }

    public static Acesso umAcessoSemLocalizacao(String planoRef) {
        return new Acesso("web", "", planoRef);
    }

    public static List<Acesso> multiplosAcessosSuspeitos(String planoRef) {
        List<Acesso> acessos = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            acessos.add(new Acesso("web", "192.168.0." + (i + 1), planoRef));
        }

        for (int i = 0; i < 3; i++) {
            acessos.add(new Acesso("mobile", "10.0.0." + (i + 1), planoRef));
        }

        return acessos;
    }

    public static List<Acesso> acessosNormais(String planoRef) {
        List<Acesso> acessos = new ArrayList<>();
        acessos.add(new Acesso("web", "192.168.0.1", planoRef));
        acessos.add(new Acesso("web", "192.168.0.1", planoRef));
        return acessos;
    }

    public static List<Acesso> acessosComMultiplasOrigens(String planoRef) {
        List<Acesso> acessos = new ArrayList<>();
        acessos.add(new Acesso("web", "192.168.0.1", planoRef));
        acessos.add(new Acesso("mobile", "192.168.0.1", planoRef));
        acessos.add(new Acesso("desktop", "192.168.0.1", planoRef));
        acessos.add(new Acesso("tablet", "192.168.0.1", planoRef));
        return acessos;
    }
}
