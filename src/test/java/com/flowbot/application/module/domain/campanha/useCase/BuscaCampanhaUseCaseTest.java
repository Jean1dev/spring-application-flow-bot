package com.flowbot.application.module.domain.campanha.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.flowbot.application.module.domain.campanha.CampanhaFactory.umaCampanha;
import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class BuscaCampanhaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private BuscaCampanhaUseCase useCase;
    @Mock
    private CampanhaMongoDBRepository repository;
    @Mock
    private NumeroMongoDbRepository numeroMongoDbRepository;

    @Test
    void findAll() {
        Mockito.when(repository.findAll(Mockito.any(PageRequest.class))).thenReturn(Page.empty());
        var result = useCase.findAll(0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToOutput() {
        var list = List.of(umaCampanha(), umaCampanha("numero"));
        var numero = umNumero();

        Mockito.when(numeroMongoDbRepository.findById(eq("numero")))
                .thenReturn(Optional.of(numero));

        Mockito.when(numeroMongoDbRepository.findById(eq("Campanha Teste")))
                .thenReturn(Optional.empty());

        var campanhaOutputs = useCase.convertToOutput(list);
        assertNotNull(campanhaOutputs);
        assertEquals(2, campanhaOutputs.size());
        assertEquals("sem numero", campanhaOutputs.get(0).numero());
        assertEquals("numero", campanhaOutputs.get(1).numero());
    }
}