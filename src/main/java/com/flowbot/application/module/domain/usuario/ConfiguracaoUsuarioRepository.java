package com.flowbot.application.module.domain.usuario;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfiguracaoUsuarioRepository extends MongoRepository<ConfiguracaoUsuario, String> {
    Optional<ConfiguracaoUsuario> findFirstBy();
}

