package com.flowbot.application.module.domain.numeros.api.dto;

import com.flowbot.application.module.domain.numeros.Numero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.flowbot.application.utils.Utils.calculateElapsedTime;

public final class DtoUtils {

    public static Page<NumeroOutput> toDto(Page<Numero> page) {
        Pageable pageable = page.getPageable();
        var content = listToDto(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public static List<NumeroSimplificadoOutput> listToDtoSimplificado(List<Numero> content) {
        return content.stream().map(DtoUtils::simplificadoToObject).toList();
    }

    private static NumeroSimplificadoOutput simplificadoToObject(Numero numero) {
        var novaDescricao = numero.getNick().substring(0, 5) + "-" + numero.getNumero();
        return new NumeroSimplificadoOutput(
                numero.getId(),
                novaDescricao
        );
    }

    private static List<NumeroOutput> listToDto(List<Numero> content) {
        return content.stream().map(DtoUtils::toObject).toList();
    }

    private static NumeroOutput toObject(Numero numero) {
        return new NumeroOutput(
                numero.getId(),
                numero.getNick(),
                numero.getNumero(),
                numero.getStatusNumero().toString(),
                calculateElapsedTime(numero.getDataCriacao())
        );
    }
}
