package com.flowbot.application.module.domain.campanha.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.module.domain.campanha.Campanha;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.flowbot.application.module.domain.campanha.CampanhaFactory.umaCampanha;
import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IniciarDisparosUseCaseTest extends UseCaseTest {
    @InjectMocks
    private IniciarDisparosUseCase useCase;
    @Mock
    private CampanhaMongoDBRepository repository;
    @Mock
    private BotBuilderApi botBuilderApi;
    @Mock
    private NumeroMongoDbRepository numeroMongoDbRepository;

    @DisplayName("Deve executar com sucesso")
    @Test
    void execute() {
        var campanha = umaCampanha(List.of("1", "2"));
        var numero = umNumero(StatusNumero.VALIDADO);
        var response = new BatchSendResponse("execID");

        when(repository.findById(anyString())).thenReturn(Optional.of(campanha));
        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.of(numero));
        when(botBuilderApi.batchSend(anyMap())).thenReturn(response);

        BatchSendResponse executed = useCase.execute("anyID");
        assertNotNull(executed);
        assertEquals(response.executionID(), executed.executionID());
    }

    @DisplayName("Nao deve continuar pois o numero nao tem nenhum numero valido")
    @Test
    void deveOcorrerErroDeValidacao() {
        var campanha = umaCampanha();
        var numero = umNumero();

        when(repository.findById(anyString())).thenReturn(Optional.of(campanha));
        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.of(numero));

        assertThrows(ValidationException.class, () -> useCase.execute("anyID"));

        verify(botBuilderApi, never()).batchSend(anyMap());
    }

    @DisplayName("Nao deve continuar pois o numero esta com status INvalido")
    @Test
    void deveOcorrerErroDeValidacaoNoNumero() {
        var campanha = umaCampanha(List.of("1", "2"));
        var numero = umNumero(StatusNumero.BANIDO);

        when(repository.findById(anyString())).thenReturn(Optional.of(campanha));
        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.of(numero));

        assertThrows(ValidationException.class, () -> useCase.execute("anyID"));

        verify(botBuilderApi, never()).batchSend(anyMap());
    }

    @DisplayName("Deve aplicar as validacoes de mensagem")
    @Test
    void deveAplicarValidacoesDeMensagem() {
        var campanha = mock(Campanha.class);
        var numero = umNumero(StatusNumero.VALIDADO);

        when(campanha.getNumeroIdRef()).thenReturn("1");
        when(campanha.getNumerosParaDisparo()).thenReturn(List.of("1", "2"));
        when(campanha.getMessageDisparo()).thenReturn("");

        when(repository.findById(anyString())).thenReturn(Optional.of(campanha));
        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.of(numero));

        assertThrows(ValidationException.class, () -> useCase.execute("anyID"));

        verify(botBuilderApi, never()).batchSend(anyMap());
    }
}