package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.module.domain.financeiro.assinaturas.Acesso;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.AnomaliaAcessoDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.DetalhesAcessoDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DetectorAnomaliaAcessoUseCase {

    private static final int LIMITE_ACESSOS_SUSPEITOS = 5;
    private static final int LIMITE_DIAS_DIFERENTES = 15;
    private final MongoTemplate mongoTemplate;

    public DetectorAnomaliaAcessoUseCase(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<AnomaliaAcessoDto> detectarAnomalias() {
        var todosAcessos = mongoTemplate.findAll(Acesso.class);
        var todosPlanos = mongoTemplate.findAll(Plano.class);

        Map<String, Plano> planosMap = todosPlanos.stream()
                .collect(Collectors.toMap(Plano::getId, plano -> plano));

        return analisarAcessosPorPlanoEMes(todosAcessos, planosMap);
    }

    public List<AnomaliaAcessoDto> detectarAnomaliasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        var query = new Query()
                .addCriteria(Criteria.where("dataAcesso").gte(inicio).lte(fim));

        var acessosNoPeriodo = mongoTemplate.find(query, Acesso.class);
        var todosPlanos = mongoTemplate.findAll(Plano.class);

        Map<String, Plano> planosMap = todosPlanos.stream()
                .collect(Collectors.toMap(Plano::getId, plano -> plano));

        return analisarAcessosPorPlanoEMes(acessosNoPeriodo, planosMap);
    }

    private List<AnomaliaAcessoDto> analisarAcessosPorPlanoEMes(List<Acesso> acessos, Map<String, Plano> planosMap) {
        Map<String, Map<YearMonth, List<Acesso>>> acessosPorPlanoEMes = agruparAcessosPorPlanoEMes(acessos);
        List<AnomaliaAcessoDto> anomalias = new ArrayList<>();

        acessosPorPlanoEMes.forEach((planoRef, acessosPorMes) -> {
            var plano = planosMap.get(planoRef);
            if (plano == null) return;

            acessosPorMes.forEach((mes, acessosDoMes) -> {
                var anomalia = analisarAcessosDoMes(plano, mes, acessosDoMes);
                if (anomalia != null) {
                    anomalias.add(anomalia);
                }
            });
        });

        return anomalias.stream()
                .sorted((a, b) -> b.nivelSuspeita().compareTo(a.nivelSuspeita()))
                .collect(Collectors.toList());
    }

    private Map<String, Map<YearMonth, List<Acesso>>> agruparAcessosPorPlanoEMes(List<Acesso> acessos) {
        return acessos.stream()
                .filter(acesso -> acesso.getPlanoRef() != null)
                .collect(Collectors.groupingBy(
                        Acesso::getPlanoRef,
                        Collectors.groupingBy(acesso -> YearMonth.from(acesso.getDataAcesso()))
                ));
    }

    private AnomaliaAcessoDto analisarAcessosDoMes(Plano plano, YearMonth mes, List<Acesso> acessos) {
        if (acessos.size() < 2) return null;

        var detalhesAcessos = acessos.stream()
                .map(acesso -> new DetalhesAcessoDto(
                        acesso.getDataAcesso(),
                        acesso.getOrigem(),
                        acesso.getLocalizacao() != null ? acesso.getLocalizacao() : "Não informada"
                ))
                .collect(Collectors.toList());

        var diasUnicos = acessos.stream()
                .map(acesso -> acesso.getDataAcesso().toLocalDate())
                .collect(Collectors.toSet())
                .size();

        var origensUnicas = acessos.stream()
                .map(Acesso::getOrigem)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                .size();

        var localizacoesUnicas = acessos.stream()
                .map(Acesso::getLocalizacao)
                .filter(Objects::nonNull)
                .filter(loc -> !loc.isEmpty())
                .collect(Collectors.toSet())
                .size();

        var nivelSuspeita = calcularNivelSuspeita(acessos.size(), diasUnicos, origensUnicas, localizacoesUnicas);

        if (nivelSuspeita.ordinal() == 0) return null;

        return new AnomaliaAcessoDto(
                plano.getId(),
                plano.getUsuario().email(),
                mes.getYear(),
                mes.getMonthValue(),
                acessos.size(),
                diasUnicos,
                origensUnicas,
                localizacoesUnicas,
                nivelSuspeita,
                gerarMotivosDeteccao(acessos.size(), diasUnicos, origensUnicas, localizacoesUnicas),
                detalhesAcessos
        );
    }

    private AnomaliaAcessoDto.NivelSuspeita calcularNivelSuspeita(int totalAcessos, int diasUnicos, int origensUnicas, int localizacoesUnicas) {
        int pontuacao = 0;

        if (totalAcessos >= LIMITE_ACESSOS_SUSPEITOS * 2) pontuacao += 3;
        else if (totalAcessos >= LIMITE_ACESSOS_SUSPEITOS) pontuacao += 2;
        else if (totalAcessos >= 3) pontuacao += 1;

        if (diasUnicos >= LIMITE_DIAS_DIFERENTES) pontuacao += 3;
        else if (diasUnicos >= 10) pontuacao += 2;
        else if (diasUnicos >= 5) pontuacao += 1;

        if (origensUnicas >= 3) pontuacao += 2;
        else if (origensUnicas >= 2) pontuacao += 1;

        if (localizacoesUnicas >= 3) pontuacao += 3;
        else if (localizacoesUnicas >= 2) pontuacao += 2;

        if (pontuacao >= 7) return AnomaliaAcessoDto.NivelSuspeita.CRITICO;
        if (pontuacao >= 4) return AnomaliaAcessoDto.NivelSuspeita.ALTO;
        if (pontuacao >= 2) return AnomaliaAcessoDto.NivelSuspeita.MEDIO;

        return AnomaliaAcessoDto.NivelSuspeita.BAIXO;
    }

    private List<String> gerarMotivosDeteccao(int totalAcessos, int diasUnicos, int origensUnicas, int localizacoesUnicas) {
        List<String> motivos = new ArrayList<>();

        if (totalAcessos >= LIMITE_ACESSOS_SUSPEITOS * 2) {
            motivos.add("Volume muito alto de acessos no mês (" + totalAcessos + " acessos)");
        } else if (totalAcessos >= LIMITE_ACESSOS_SUSPEITOS) {
            motivos.add("Volume alto de acessos no mês (" + totalAcessos + " acessos)");
        }

        if (diasUnicos >= LIMITE_DIAS_DIFERENTES) {
            motivos.add("Acessos distribuídos em muitos dias diferentes (" + diasUnicos + " dias)");
        } else if (diasUnicos >= 10) {
            motivos.add("Acessos em vários dias do mês (" + diasUnicos + " dias)");
        }

        if (origensUnicas >= 3) {
            motivos.add("Múltiplas origens de acesso detectadas (" + origensUnicas + " origens)");
        } else if (origensUnicas >= 2) {
            motivos.add("Diferentes origens de acesso (" + origensUnicas + " origens)");
        }

        if (localizacoesUnicas >= 3) {
            motivos.add("Múltiplas localizações detectadas (" + localizacoesUnicas + " localizações)");
        } else if (localizacoesUnicas >= 2) {
            motivos.add("Diferentes localizações de acesso (" + localizacoesUnicas + " localizações)");
        }

        if (motivos.isEmpty()) {
            motivos.add("Padrão de uso atípico detectado");
        }

        return motivos;
    }
}
