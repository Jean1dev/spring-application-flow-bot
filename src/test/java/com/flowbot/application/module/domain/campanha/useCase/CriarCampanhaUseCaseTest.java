package com.flowbot.application.module.domain.campanha.useCase;

import com.flowbot.application.module.domain.UseCaseTest;
import com.flowbot.application.module.domain.campanha.Campanha;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.campanha.apis.dto.CriarCampanhaRequest;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;

import static com.flowbot.application.module.domain.campanha.CampanhaFactory.umaCampanha;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

class CriarCampanhaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private CriarCampanhaUseCase criarCampanhaUseCase;

    @Mock
    private CampanhaMongoDBRepository campanhaMongoDBRepository;

    @Test
    void criarNovaCampanhaComSucesso() {
        var request = new CriarCampanhaRequest(
                "titulo",
                "numeroIdRef",
                Arrays.asList("numerosParaDisparo"),
                "AVISO",
                Arrays.asList("arquivo1", "arquivo2")
        );

        final var campanha = umaCampanha();
        Mockito.when(campanhaMongoDBRepository.save(any(Campanha.class))).thenReturn(campanha);

        var response = criarCampanhaUseCase.execute(request);
        assertNotNull(response);
    }

    @Test
    void naoDeveCriarComEnumInvalido() {
        var request = new CriarCampanhaRequest(
                "titulo",
                "numeroIdRef",
                Arrays.asList("numerosParaDisparo"),
                "Enum Nao valido",
                Arrays.asList("arquivo1", "arquivo2")
        );

        assertThrows(IllegalArgumentException.class, () -> criarCampanhaUseCase.execute(request));
    }

    @Test
    void naoDeveCriarPorFalhaDeValidacao() {
        final var request = new CriarCampanhaRequest(
                "",
                "numeroIdRef",
                Arrays.asList("numerosParaDisparo"),
                "OUTROS",
                Arrays.asList("arquivo1", "arquivo2")
        );

        assertThrows(ValidationException.class, () -> criarCampanhaUseCase.execute(request));

        final var request2 = new CriarCampanhaRequest(
                null,
                "numeroIdRef",
                Arrays.asList("numerosParaDisparo"),
                "OUTROS",
                Arrays.asList("arquivo1", "arquivo2")
        );

        assertThrows(ValidationException.class, () -> criarCampanhaUseCase.execute(request2));
    }
}