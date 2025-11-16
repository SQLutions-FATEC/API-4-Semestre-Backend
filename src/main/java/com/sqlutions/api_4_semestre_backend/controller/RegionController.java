package com.sqlutions.api_4_semestre_backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.RegionMap;
import com.sqlutions.api_4_semestre_backend.service.IndexService;
import com.sqlutions.api_4_semestre_backend.service.TimeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/regions")
@Tag(name = "Region", description = "Endpoints for managing and getting region indexes")
public class RegionController {

    // Injeção de dependência do serviço de região
    @Autowired
    private IndexService indexService;

    @Autowired
    private TimeService timeService;

    // Métodos para manipulação de regiões
    @GetMapping
    @Operation(summary = "Get region indexes with optional filters")
    public List<RegionMap> getRegionsIndex(
            @Parameter(description = "Number of minutes to filter readings (from timestamp backwards)") @RequestParam(defaultValue = "10") int minutes,
            @Parameter(description = "Timestamp to filter readings", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getRegionsIndex(minutes,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

}
