package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.api.dto.CriarNovoNumeroDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("Criar numero use case test")
class CriarNumeroUseCaseTest extends UseCaseTest {

    @InjectMocks
    private CriarNumeroUseCase useCase;

    @Mock
    private NumeroMongoDbRepository repository;

    @DisplayName("Deve executar e salvar um numero com sucesso")
    @Test
    void execute() {
        Numero numero = umNumero();
        Mockito.when(repository.save(any(Numero.class))).thenReturn(numero);

        final var requestDto = new CriarNovoNumeroDto(numero.getNick(), numero.getNumero());
        final var result = useCase.execute(requestDto);
        assertNotNull(result);
        assertEquals(numero.getNick(), result.getNick());
        assertEquals(numero.getNumero(), result.getNumero());
    }
}