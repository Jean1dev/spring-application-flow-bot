package com.flowbot.application.module.domain.playground.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import com.flowbot.application.module.domain.numeros.api.dto.CriarNovoNumeroDto;
import com.flowbot.application.module.domain.numeros.useCase.CriarNumeroUseCase;
import com.flowbot.application.module.domain.numeros.useCase.ValidarNumeroUseCase;
import com.flowbot.application.module.domain.playground.api.dto.PlayGroundExecOutput;
import com.flowbot.application.shared.ApplicationScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class PlayGroundUseCaseTest extends UseCaseTest {

    @InjectMocks
    private PlayGroundUseCase useCase;
    @Mock
    private BotBuilderApi botBuilderApi;
    @Mock
    private NumeroMongoDbRepository numeroMongoDbRepository;
    @Mock
    private CriarNumeroUseCase criarNumeroUseCase;
    @Mock
    private ValidarNumeroUseCase validarNumeroUseCase;
    @Mock
    private ApplicationScheduleService scheduledExecutorService;

    @Test
    @DisplayName("Deve criar um numero novo pq o numero enviado nao existe")
    void deveCriarNumeroNovo() {
        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.empty());

        var numeroMock = umNumero();
        when(criarNumeroUseCase.execute(any(CriarNovoNumeroDto.class))).thenReturn(numeroMock);

        PlayGroundExecOutput execute = useCase.execute("", "", "");
        assertNotNull(execute);
        assertFalse(execute.success());
        assertTrue(execute.needValidadeNumber());
        assertEquals("Numero nao encontrado, vinculo o whatsapp", execute.message());
        assertNull(execute.senderId());
    }

    @Test
    @DisplayName("Nao deve continuar pq o Status do numero eh banido")
    void naoDeveContinuar() {
        var numeroMock = umNumero(StatusNumero.BANIDO);

        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.of(numeroMock));

        PlayGroundExecOutput execute = useCase.execute("", "", "");
        assertNotNull(execute);
        assertFalse(execute.success());
        assertTrue(execute.needValidadeNumber());
        assertEquals("Numero não está validado", execute.message());
        assertNull(execute.senderId());
    }

    @Test
    @DisplayName("deve processar com sucesso")
    void deveProcessar() {
        var numeroMock = umNumero(StatusNumero.VALIDADO);

        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.of(numeroMock));
        when(botBuilderApi.playground(anyMap())).thenReturn(true);

        PlayGroundExecOutput execute = useCase.execute("s", "r", "m");
        assertNotNull(execute);
        assertTrue(execute.success());
        assertFalse(execute.needValidadeNumber());
        assertEquals("Playground enviado com sucesso", execute.message());
        assertNull(execute.senderId());
    }

    @Test
    @DisplayName("deve processar com sucesso, mas api retornou false")
    void deveProcessarMasApiRetornouFalse() {
        var numeroMock = umNumero(StatusNumero.VALIDADO);

        when(numeroMongoDbRepository.findById(anyString())).thenReturn(Optional.of(numeroMock));
        when(botBuilderApi.playground(anyMap())).thenReturn(false);

        PlayGroundExecOutput execute = useCase.execute("s", "r", "m");
        assertNotNull(execute);
        assertFalse(execute.success());
        assertFalse(execute.needValidadeNumber());
        assertEquals("Falha ao enviar o playground", execute.message());
        assertNull(execute.senderId());
    }
}