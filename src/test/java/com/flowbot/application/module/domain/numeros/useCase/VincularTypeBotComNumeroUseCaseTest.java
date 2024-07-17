package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.http.dtos.TypeBotAddInput;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import com.flowbot.application.module.domain.numeros.api.dto.VincularTypeBotInput;
import com.flowbot.application.module.domain.numeros.service.RelacionamentoNumeroTypeBotService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VincularTypeBotComNumeroUseCaseTest extends UseCaseTest {
    @InjectMocks
    private VincularTypeBotComNumeroUseCase useCase;
    @Mock
    private BotBuilderApi botBuilderApi;
    @Mock
    private NumeroMongoDbRepository repository;
    @Mock
    private RelacionamentoNumeroTypeBotService relacionamentoNumeroTypeBotService;

    @Test
    @DisplayName("Deve executar com sucesso")
    void execute() {
        var input = new VincularTypeBotInput("123", "apiHost", "typebotName");
        Numero numero = umNumero(StatusNumero.VALIDADO);

        when(repository.findById(input.numeroId())).thenReturn(Optional.of(numero));

        when(botBuilderApi.addTypeBot(any(TypeBotAddInput.class))).thenReturn(true);

        useCase.execute(input);

        verify(relacionamentoNumeroTypeBotService).relacionar(input.numeroId(), input.typebotName(), input.apiHost());
    }

    @Test
    @DisplayName("Deve falhar pq a requisicao para a api retornou falha")
    void deveFalhar() {
        var input = new VincularTypeBotInput("123", "apiHost", "typebotName");
        Numero numero = umNumero(StatusNumero.VALIDADO);

        when(repository.findById(input.numeroId())).thenReturn(Optional.of(numero));

        when(botBuilderApi.addTypeBot(any(TypeBotAddInput.class))).thenReturn(false);

        assertThrows(ValidationException.class, () -> useCase.execute(input));
        verify(relacionamentoNumeroTypeBotService, never()).relacionar(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve falhar pq o numero nao foi encontrado")
    void deveFalharQuandoNumeroNaoEncontrado() {
        var input = new VincularTypeBotInput("123", "apiHost", "typebotName");

        when(repository.findById(input.numeroId())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> useCase.execute(input));
        verify(relacionamentoNumeroTypeBotService, never()).relacionar(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve validar os dados")
    void validar() {
        var numero = umNumero();
        when(repository.findById(anyString())).thenReturn(Optional.of(numero));

        var input = new VincularTypeBotInput("123", "apiHost", "typebotName");

        assertThrows(ValidationException.class, () -> useCase.execute(input));
        verify(relacionamentoNumeroTypeBotService, never()).relacionar(anyString(), anyString(), anyString());

        var input1 = new VincularTypeBotInput("123", "apiHost", null);

        assertThrows(ValidationException.class, () -> useCase.execute(input1));
        verify(relacionamentoNumeroTypeBotService, never()).relacionar(anyString(), anyString(), anyString());
    }
}