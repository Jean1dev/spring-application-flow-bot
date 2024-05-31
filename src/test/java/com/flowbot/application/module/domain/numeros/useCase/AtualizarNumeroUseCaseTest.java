package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.api.dto.AtualizarNumeroInput;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AtualizarNumeroUseCaseTest extends UseCaseTest {

    @InjectMocks
    private AtualizarNumeroUseCase useCase;

    @Mock
    private NumeroMongoDbRepository repository;

    @Test
    @DisplayName("Deve atualizar o numero com sucesso")
    void execute() {
        var numero = umNumero();
        var numeroId = "numeroId";

        when(repository.findById(numeroId)).thenReturn(Optional.of(numero));

        var input = new AtualizarNumeroInput("486688932", "Novo Apelido");
        useCase.execute(numeroId, input);

        verify(repository, times(1)).save(numero);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o numero não for encontrado")
    void execute_ThrowException() {
        var numeroId = "numeroId";

        when(repository.findById(numeroId)).thenReturn(Optional.empty());

        var input = new AtualizarNumeroInput("486688932", "Novo Apelido");
        assertThrows(NoSuchElementException.class, () -> useCase.execute(numeroId, input));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aplicar as validacoes")
    void execute_ApplyValidations() {
        var numeroId = "numeroId";

        var input = new AtualizarNumeroInput("", "Novo Apelido");
        assertThrows(ValidationException.class, () -> useCase.execute(numeroId, input));

        var input2 = new AtualizarNumeroInput("486688932", "");
        assertThrows(ValidationException.class, () -> useCase.execute(numeroId, input2));

        verify(repository, never()).save(any());
    }
}