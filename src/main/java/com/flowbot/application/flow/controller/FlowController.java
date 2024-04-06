package com.flowbot.application.flow.controller;

import com.flowbot.application.flow.representations.FlowDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/flow")
public class FlowController {


    @PostMapping
    public ResponseEntity<FlowDTO> insert(@Valid @RequestBody FlowDTO flow) {
        return ResponseEntity.ok().body(flow);
    }

    @PostMapping("/protegido")
    public ResponseEntity<FlowDTO> insertProtegido(@Valid @RequestBody FlowDTO flow) {
        return ResponseEntity.ok().body(flow);
    }
}
