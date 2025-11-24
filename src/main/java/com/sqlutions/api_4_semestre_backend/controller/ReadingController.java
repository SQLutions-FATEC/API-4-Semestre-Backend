package com.sqlutions.api_4_semestre_backend.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;
import com.sqlutions.api_4_semestre_backend.service.ReadingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reading")
@Tag(name = "Reading", description = "Endpoints for managing and getting readings and reading information")
public class ReadingController {

    @Autowired
    private ReadingService readingService;

    @GetMapping("/all")
    @Operation(summary = "Get all readings")
    public ResponseEntity<List<ReadingInformation>> getAllReadings() {
        return ResponseEntity.ok(readingService.getAllReadings()); // péssima ideia.
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reading by ID")
    public ResponseEntity<Reading> getReadingById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(readingService.getReadingById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new reading")
    @ApiResponse(responseCode = "201", description = "Reading created successfully")
    public ResponseEntity<Reading> postReading(@RequestBody Reading reading) {
        return ResponseEntity.created(URI.create("/reading/" + reading.getId()))
                .body(readingService.createReading(reading));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reading")
    @ApiResponse(responseCode = "200", description = "Reading updated successfully")
    public ResponseEntity<Reading> putReading(@PathVariable Integer id, @RequestBody Reading reading) {
        reading.setId(id);
        return ResponseEntity.ok(readingService.updateReading(reading));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reading")
    @ApiResponse(responseCode = "202", description = "Reading deletion accepted")
    public ResponseEntity<Void> deleteReading(@PathVariable Integer id) {
        return ResponseEntity.accepted().body(readingService.deleteReading(id));
    }

    @GetMapping()
    @Operation(summary = "Get readings information with optional filters")
    @ApiResponse(responseCode = "200", description = "Readings retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public ResponseEntity<ReadingGroupAggregate> getReadings(
            @Parameter(description = "Number of minutes to filter readings (from timestamp backwards)") @RequestParam(required = false, defaultValue = "5") int minutes,
            @Parameter(description = "Timestamp to filter readings", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp,
            @Parameter(description = "List of radars to filter readings", example = "L0649_2,L0649_1") @RequestParam(required = false) List<String> radars,
            @Parameter(description = "List of addresses to filter readings", example = "Av. Monsenhor Dutra Interseção Av. Pres. Tancredo Neves C/B") @RequestParam(required = false) List<String> addresses,
            @Parameter(description = "List of regions to filter readings", example = "Norte,Centro,Leste") @RequestParam(required = false) List<String> regions) {
        return ResponseEntity.ok(readingService.getReadings(minutes, timestamp, radars, addresses, regions));
    }

    @GetMapping("/series")
    @Operation(summary = "Get readings information adaptively grouped by time series with optional filters", description = "Get readings information with the filters applied, and grouped adaptively with the followign rules: \n"
            + "- If period is less than 1 hour, group by 10 minute intervals\n"
            + "- If period is less than 3 days, group by hour\n"
            + "- If period is more than 3 days, group by day")
    @ApiResponse(responseCode = "200", description = "Readings retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters", content = {})
    public ResponseEntity<List<ReadingGroupAggregate>> getReadingSeries(
            @Parameter(description = "Number of minutes to filter readings (from timestamp backwards)") @RequestParam(required = false, defaultValue = "5") int minutes,
            @Parameter(description = "Timestamp to filter readings", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp,
            @Parameter(description = "List of radars to filter readings", example = "L0649_2,L0649_1") @RequestParam(required = false) List<String> radars,
            @Parameter(description = "List of addresses to filter readings", example = "Av. Monsenhor Dutra Interseção Av. Pres. Tancredo Neves C/B") @RequestParam(required = false) List<String> addresses,
            @Parameter(description = "List of regions to filter readings", example = "Norte,Centro,Leste") @RequestParam(required = false) List<String> regions) {
        return ResponseEntity.ok(readingService.getReadingSeries(minutes, timestamp, radars, addresses, regions));
    }
}
