package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.service.IndexService;
import com.sqlutions.api_4_semestre_backend.service.TimeService;

@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    TimeService timeService;

    @GetMapping
    public Index getCityIndex(@RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getCityIndex(minutes,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/radar")
    public Index getRadarIndexes(@RequestParam(defaultValue = "1") int minutes, @RequestParam String[] radars,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getRadarIndexes(minutes, radars,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/region")
    public Index getRegionIndex(@RequestParam(defaultValue = "1") int minutes, @RequestParam String region,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getRegionIndex(minutes, region,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/series")
    public List<Index> getCityIndexSeries(@RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getCityIndexSeries(minutes,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/radar/series")
    public List<Index> getRadarIndexesSeries(@RequestParam(defaultValue = "1") int minutes, @RequestParam String[] radars,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getRadarIndexesSeries(minutes, radars,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/region/series")
    public List<Index> getRegionIndexSeries(@RequestParam(defaultValue = "1") int minutes, @RequestParam String region,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getRegionIndexSeries(minutes, region,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

     @GetMapping("/address")
    public Index getAddressIndexes(@RequestParam(defaultValue = "1") int minutes, @RequestParam String address,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getAddressIndexes(minutes, address,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }
}
