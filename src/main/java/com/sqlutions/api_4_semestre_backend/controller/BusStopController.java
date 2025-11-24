package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.BusStop;
import com.sqlutions.api_4_semestre_backend.service.BusStopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/bus-stop")
@Tag(name = "Bus Stop", description = "Endpoints for managing bus stops")
public class BusStopController {

    @Autowired
    private BusStopService busStopService;

    @GetMapping
    @Operation(summary = "List all bus stops")
    @ApiResponse(responseCode = "200", description = "List of bus stops retrieved successfully")
    public List<BusStop> listBusStops() {
        return busStopService.listBusStops();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bus stop by ID")
    @ApiResponse(responseCode = "200", description = "Bus stop retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Bus stop not found")
    public BusStop searchBusStop(@PathVariable Long id) {
        return busStopService.searchBusStopById(id);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a bus stop")
    @ApiResponse(responseCode = "200", description = "Bus stop deleted successfully")
    @ApiResponse(responseCode = "404", description = "Bus stop not found")
    public void deleteBusStop(@PathVariable Long id) {
        busStopService.deleteBusStop(id);
    }
}
