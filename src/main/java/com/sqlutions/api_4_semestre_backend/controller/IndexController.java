package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.service.IndexService;
import com.sqlutions.api_4_semestre_backend.service.TimeService;

@RestController
@RequestMapping("/indexes")
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    TimeService timeService;

    @GetMapping
    public List<Index> getCityIndex(@RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getCityIndex(minutes,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/radar")
    public List<Index> getRadarIndexes(@RequestParam(defaultValue = "1") int minutes, @RequestBody Radar[] radars,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getRadarIndexes(minutes, radars,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/region")
    public List<Index> getRegionIndexes(@RequestParam(defaultValue = "1") int minutes, @RequestBody String region,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getRegionIndexes(minutes, region,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }
}
