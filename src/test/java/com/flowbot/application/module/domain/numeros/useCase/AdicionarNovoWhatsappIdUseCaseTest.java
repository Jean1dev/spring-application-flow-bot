package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdicionarNovoWhatsappIdUseCaseTest extends UseCaseTest {
    @InjectMocks
    private AdicionarNovoWhatsappIdUseCase useCase;
    @Mock
    private ScheduledExecutorService executorService;
    @Mock
    private ValidarNumeroUseCase validarNumeroUseCase;
    @Mock
    private NumeroMongoDbRepository repository;

    @Test
    @DisplayName("Deve atualizar o codigo do whatsapp")
    void execute() {
        var numero = umNumero();

        when(repository.findById(anyString())).thenReturn(Optional.of(numero));

        useCase.execute("numero.getId()", "123");

        assertEquals("123", numero.getWhatsappInternalId());
        verify(repository, times(1)).save(numero);
        verify(executorService, times(1)).schedule(any(Runnable.class), anyLong(), any());
    }

    @Test
    @DisplayName("Nao deve fazer nada pq o id do numero nao existe")
    void naoDeveFazerNadaPqIdNumeroNaoExiste() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.execute("numero.getId()", "123"));

        verify(repository, never()).save(any());
        verify(executorService, never()).schedule(any(Runnable.class), anyLong(), any());
    }

    @Test
    @DisplayName("deve aplicar todas as regras de validacao")
    void deveAplicarTodasAsRegrasDeValidacao() {
        var numero = umNumero(StatusNumero.BANIDO);

        when(repository.findById(anyString())).thenReturn(Optional.of(numero));

        assertThrows(ValidationException.class,
                () -> useCase.execute("numero.getId()", "123"),
                "Numero banido não pode ser alterado");

        verify(repository, never()).save(any());
        verify(executorService, never()).schedule(any(Runnable.class), anyLong(), any());

        var numero2 = umNumero();

        when(repository.findById(anyString())).thenReturn(Optional.of(numero2));

        assertThrows(ValidationException.class,
                () -> useCase.execute("numero.getId()", ""),
                "Whatsapp id não pode ser nulo");

        verify(repository, never()).save(any());
        verify(executorService, never()).schedule(any(Runnable.class), anyLong(), any());
    }
}