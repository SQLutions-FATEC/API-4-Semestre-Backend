package com.sqlutions.api_4_semestre_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.service.TimeService;

@RestController
@RequestMapping("/time")
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping
    public String[] getDatabaseTimeStrings() {
        return new String[] { timeService.getStartTime().toString(), timeService.getEndTime().toString(),
                timeService.getCurrentTimeClampedToDatabase().toString() };
    }
}
