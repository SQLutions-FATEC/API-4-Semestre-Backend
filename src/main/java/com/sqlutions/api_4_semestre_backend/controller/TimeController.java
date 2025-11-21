package com.sqlutions.api_4_semestre_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.service.TimeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/time")
@Tag(name = "Time", description = "Endpoints for getting database time information")
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping
    @Operation(summary = "Get database time information")
    @ApiResponse(responseCode = "200", description = "Database time information: start time, end time, and current mapped time retrieved successfully")
    public String[] getDatabaseTimeStrings() {
        return new String[] { timeService.getStartTime().toString(), timeService.getEndTime().toString(),
                timeService.getCurrentTimeClampedToDatabase().toString() };
    }
}
