package com.sqlutions.api_4_semestre_backend.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.event.HighIndexEvent;

@RestController
@RequestMapping("/simulate")
public class SimulationController {

    private final ApplicationEventPublisher eventPublisher;

    public SimulationController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public String simulate(
            @RequestParam(defaultValue = "0") int trafficIndex,
            @RequestParam(defaultValue = "0") int securityIndex
    ) {
        Index index = new Index(trafficIndex, securityIndex);

        eventPublisher.publishEvent(new HighIndexEvent(this, index));

        return String.format(
            " Simulação enviada! (Tráfego: %d, Segurança: %d)",
            trafficIndex, securityIndex
        );
    }

    @GetMapping("/all")
    public String simulateAll() {
        Index index = new Index(5, 5);
        eventPublisher.publishEvent(new HighIndexEvent(this, index));
        return " Simulação completa enviada (tráfego e segurança críticos)!";
    }
}