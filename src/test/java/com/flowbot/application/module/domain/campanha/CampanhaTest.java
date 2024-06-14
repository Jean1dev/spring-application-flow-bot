package com.flowbot.application.module.domain.campanha;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Campanha Object Test")
class CampanhaTest {

    @Test
    public void deveCriarCorretamente() {
        var campanha = new Campanha(
                null,
                "Campanha Teste",
                "Campanha Teste",
                null,
                CategoriaCampanha.AVISO,
                null,
                null,
                "MEnsagem de disparo QUalqwur",
                "Flow Qualquer"
        );
        assertNotNull(campanha);
        assertEquals("MEnsagem de disparo QUalqwur", campanha.getMessageDisparo());
        assertEquals("Flow Qualquer", campanha.getFlowDisparoRef());
        assertEquals("Campanha Teste", campanha.getTitulo());
        assertEquals("Campanha Teste", campanha.getNumeroIdRef());
        assertNull(campanha.getId());
        assertEquals(CategoriaCampanha.AVISO, campanha.getCategoria());
        assertEquals(StatusCampanha.ATIVO, campanha.getStatus());
        assertNull(campanha.getArquivosUrls());
    }

}