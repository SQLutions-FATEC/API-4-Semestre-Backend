package com.sqlutions.api_4_semestre_backend.controller;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.service.RadarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/radars")
public class RadarController {

    private final RadarService radarService;

    public RadarController(RadarService radarService) {
        this.radarService = radarService;
    }

    @PostMapping
    public Radar createRadar(@RequestBody Radar radar) {
        return radarService.createRadar(radar);
    }

    @GetMapping
    public List<Radar> getAllRadars() {
        return radarService.getAllRadars();
    }

    @GetMapping("/{id}")
    public Radar getRadarById(@PathVariable String id) {
        return radarService.getRadarById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possível encontrar o radar"));
    }

    @PutMapping("/{id}")
    public Radar updateRadar(@PathVariable String id, @RequestBody Radar radar) {
        return radarService.updateRadar(id, radar);
    }

    @DeleteMapping("/{id}")
    public void deleteRadar(@PathVariable String id) {
        radarService.deleteRadar(id);
    }

    @GetMapping("/address/{addressId}")
    public List<Radar> getRadarsByAddress(@PathVariable Long addressId) {
        return radarService.getRadarsByAddress(addressId);
    }
}
