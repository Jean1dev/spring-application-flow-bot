package com.flowbot.application.module.domain.numeros.service;

import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class VerificarTodosNumerosService {
    private final NumeroMongoDbRepository repository;
    private final BotBuilderApi botBuilderApi;

    public VerificarTodosNumerosService(NumeroMongoDbRepository repository, BotBuilderApi botBuilderApi) {
        this.repository = repository;
        this.botBuilderApi = botBuilderApi;
    }

    @Async
    public void verificar() {
        repository.findAll()
                .stream()
                .filter(numero -> StatusNumero.VALIDADO.equals(numero.getStatusNumero()))
                .parallel()
                .forEach(this::asyncProcess);
    }

    private void asyncProcess(Numero numero) {
        boolean result = botBuilderApi.verificarStatusDoNumero(numero.getWhatsappInternalId())
                .connected();

        if (!result) {
            numero.atualizarStatus(StatusNumero.PENDENTE);
            repository.save(numero);
        }
    }
}
