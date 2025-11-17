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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/radars")
@Tag(name = "Radar", description = "Endpoints for managing radars")
public class RadarController { // não faz muito sentido pra API ter essas rotas de criação e remoção de
                               // radares, mas ok
    @Autowired
    private RadarService radarService;

    @GetMapping
    @Operation(summary = "Get all radars")
    @ApiResponse(responseCode = "200", description = "Radars retrieved successfully")
    public ResponseEntity<List<Radar>> getRadars() {
        return ResponseEntity.ok(radarService.getAllRadars());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get radar by ID")
    @ApiResponse(responseCode = "200", description = "Radar retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Radar not found")
    public ResponseEntity<Radar> getRadarById(@PathVariable String id) {
        Radar radar = radarService.getRadarById(id);
        if (radar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(radar);
    }

    @PostMapping
    @Operation(summary = "Create a new radar")
    @ApiResponse(responseCode = "201", description = "Radar created successfully")
    public ResponseEntity<Radar> postRadar(@RequestBody Radar radar) {
        return ResponseEntity.created(URI.create("/radars/" + radar.getId()))
                .body(radarService.createRadar(radar));
    }

    @PutMapping
    @Operation(summary = "Update an existing radar")
    @ApiResponse(responseCode = "200", description = "Radar updated successfully")
    public ResponseEntity<Radar> putRadar(
        @RequestBody Radar radar) {
        return ResponseEntity.ok()
                .body(radarService.updateRadar(radar));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a radar")
    @ApiResponse(responseCode = "202", description = "Radar deletion accepted")
    @ApiResponse(responseCode = "404", description = "Radar not found")
    public ResponseEntity<Void> deleteRadar(@PathVariable String id) {
        return ResponseEntity.accepted()
                .body(radarService.deleteRadar(id));
    }
}
