package com.sqlutions.api_4_semestre_backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.service.IndexService;
import com.sqlutions.api_4_semestre_backend.service.TimeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/index")
@Tag(name = "Index", description = "Endpoints for getting indexes")
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    TimeService timeService;

    @GetMapping
    @Operation(summary = "Get city index")
    @ApiResponse(responseCode = "200", description = "City index retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public Index getCityIndex(
            @Parameter(description = "Time window in minutes (from timestamp backwards)", example = "1") @RequestParam(defaultValue = "1") int minutes,
            @Parameter(description = "Timestamp for the index", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getCityIndex(minutes,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/radar")
    @Operation(summary = "Get radar indexes")
    @ApiResponse(responseCode = "200", description = "Radar indexes retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public Index getRadarIndexes(
            @Parameter(description = "Time window in minutes (from timestamp backwards)", example = "1") @RequestParam(defaultValue = "1") int minutes,
            @Parameter(description = "Radar identifiers", example = "L0649_2") @RequestParam String[] radars,
            @Parameter(description = "Timestamp for the index", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getRadarIndexes(minutes, radars,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/region")
    @Operation(summary = "Get region index")
    @ApiResponse(responseCode = "200", description = "Region index retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public Index getRegionIndex(
            @Parameter(description = "Time window in minutes (from timestamp backwards)", example = "1") @RequestParam(defaultValue = "1") int minutes,
            @Parameter(description = "Region name") @RequestParam String region,
            @Parameter(description = "Timestamp for the index", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getRegionIndex(minutes, region,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/series")
    @Operation(summary = "Get city index adaptively grouped by time series")
    @ApiResponse(responseCode = "200", description = "City index time series retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public List<Index> getCityIndexSeries(
            @Parameter(description = "Time window in minutes (from timestamp backwards)", example = "1") @RequestParam(defaultValue = "1") int minutes,
            @Parameter(description = "Timestamp for the index", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getCityIndexSeries(minutes,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/radar/series")
    @Operation(summary = "Get radar indexes adaptively grouped by time series")
    @ApiResponse(responseCode = "200", description = "Radar index time series retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public List<Index> getRadarIndexesSeries(
            @Parameter(description = "Time window in minutes (from timestamp backwards)", example = "1") @RequestParam(defaultValue = "1") int minutes,
            @Parameter(description = "Radar identifiers", example = "L0649_2") @RequestParam String[] radars,
            @Parameter(description = "Timestamp for the index", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getRadarIndexesSeries(minutes, radars,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/region/series")
    @Operation(summary = "Get region index adaptively grouped by time series")
    @ApiResponse(responseCode = "200", description = "Region index time series retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public List<Index> getRegionIndexSeries(
            @Parameter(description = "Time window in minutes (from timestamp backwards)", example = "1") @RequestParam(defaultValue = "1") int minutes,
            @Parameter(description = "Region name", example = "Centro") @RequestParam String region,
            @Parameter(description = "Timestamp for the index", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getRegionIndexSeries(minutes, region,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/address")
    @Operation(summary = "Get address indexes")
    @ApiResponse(responseCode = "200", description = "Address indexes retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No readings found for the given parameters")
    public Index getAddressIndexes(
            @Parameter(description = "Time window in minutes (from timestamp backwards)", example = "1") @RequestParam(defaultValue = "1") int minutes,
            @Parameter(description = "Address", example = "Av. Monsenhor Dutra Interseção Av. Pres. Tancredo Neves C/B") @RequestParam String address,
            @Parameter(description = "Timestamp for the index", example = "2025-07-04T12:00:00") @RequestParam(required = false) LocalDateTime timestamp) {
        return indexService.getAddressIndexes(minutes, address,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }
}
