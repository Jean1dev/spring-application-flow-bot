package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.financeiro.assinaturas.PeriodoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoMongoDbRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CriarPlanoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private CriarPlanoUseCase useCase;

    @Mock
    private PlanoMongoDbRepository repository;

    @Test
    void criarPlanoSimples() {
        final var email = "xxxxx";
        final var periodo = PeriodoPlano.ANUAL;

        when(repository.save(any(Plano.class))).thenReturn(new Plano());

        final var id = useCase.criarPlanoSimples(email, periodo);
        assertNull(id);

        verify(repository, times(1)).save(any(Plano.class));
    }
}