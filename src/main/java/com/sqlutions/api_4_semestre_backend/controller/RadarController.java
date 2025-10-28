package com.sqlutions.api_4_semestre_backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.service.RadarService;

@RestController
@CrossOrigin
@RequestMapping("/radars")
public class RadarController { // não faz muito sentido pra API ter essas rotas de criação e remoção de
                               // radares, mas ok
    @Autowired
    private RadarService radarService;

    @GetMapping
    public ResponseEntity<List<Radar>> getRadars() {
        return ResponseEntity.ok(radarService.getAllRadars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Radar> getRadarById(@PathVariable String id) {
        Radar radar = radarService.getRadarById(id);
        if (radar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(radar);
    }

    @PostMapping
    public ResponseEntity<Radar> postRadar(@RequestBody Radar radar) {
        return ResponseEntity.created(URI.create("/radars/" + radar.getId()))
                .body(radarService.createRadar(radar));
    }

    @PutMapping
    public ResponseEntity<Radar> putRadar(@RequestBody Radar radar) {
        return ResponseEntity.ok()
                .body(radarService.updateRadar(radar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRadar(@PathVariable String id) {
        return ResponseEntity.accepted()
                .body(radarService.deleteRadar(id));
    }
}
