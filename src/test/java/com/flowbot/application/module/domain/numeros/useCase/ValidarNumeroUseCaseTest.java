package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.http.dtos.VerifyNumberResponse;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ValidarNumeroUseCaseTest extends UseCaseTest {

    @InjectMocks
    private ValidarNumeroUseCase useCase;

    @Mock
    private NumeroMongoDbRepository repository;
    @Mock
    private BotBuilderApi botBuilderApi;

    @Test
    @DisplayName("Deve testar o processo completo com sucesso")
    void execute() {
        final var id = "id";

        var numero = umNumero();
        when(repository.findById(eq(id))).thenReturn(Optional.of(numero));
        when(botBuilderApi.verificarStatusDoNumero(eq(numero.getWhatsappInternalId()))).thenReturn(new VerifyNumberResponse(true));

        useCase.execute(id);

        assertEquals(StatusNumero.VALIDADO, numero.getStatusNumero());
        verify(repository, times(1)).save(any(Numero.class));
    }

    @Test
    @DisplayName("Nao deve salvar pq nao encontrou o registro")
    void naoDeveSalvar() {
        final var id = "id";
        when(repository.findById(eq(id))).thenReturn(Optional.empty());

        useCase.execute(id);

        verify(repository, never()).save(any(Numero.class));
    }

    @Test
    @DisplayName("Nao deve salvar pq o status nao permite")
    void naoDeveSalvarPorContaDoStatus() {
        final var id = "id";

        var numeroBanido = umNumero(StatusNumero.BANIDO);
        when(repository.findById(eq(id))).thenReturn(Optional.of(numeroBanido));

        assertThrows(ValidationException.class, () -> useCase.execute(id));
        verify(repository, never()).save(any(Numero.class));

        var numeroValidado = umNumero(StatusNumero.VALIDADO);
        when(repository.findById(eq(id))).thenReturn(Optional.of(numeroValidado));

        assertThrows(ValidationException.class, () -> useCase.execute(id));
        verify(repository, never()).save(any(Numero.class));
    }
}