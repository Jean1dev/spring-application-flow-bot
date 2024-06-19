package com.flowbot.application.module.domain.telemetria.api;

import com.flowbot.application.module.domain.telemetria.TelemetriaOutput;
import com.flowbot.application.module.domain.telemetria.TelemetriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/telemetria")
public class TelemetriaController {

    private final TelemetriaRepository telemetriaRepository;

    public TelemetriaController(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;
    }

    @GetMapping
    public List<TelemetriaOutput> findAll() {
        return telemetriaRepository.get();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        telemetriaRepository.removeById(id);
    }

    @DeleteMapping("/numero/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteByNumber(@PathVariable String id) {
        telemetriaRepository.removeAllByNumber(id);
    }
}
