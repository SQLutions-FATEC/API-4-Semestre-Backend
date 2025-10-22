package com.sqlutions.api_4_semestre_backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;
import com.sqlutions.api_4_semestre_backend.service.ReadingService;

@RestController
@RequestMapping("/reading")
public class ReadingController {

    @Autowired
    private ReadingService readingService;

    @GetMapping("/all")
    public ResponseEntity<List<ReadingInformation>> getAllReadings() {
        return ResponseEntity.ok(readingService.getAllReadings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reading> getReadingById(@PathVariable Integer id) {
        return ResponseEntity.ok(readingService.getReadingById(id));
    }

    @PostMapping
    public ResponseEntity<Reading> postReading(@RequestBody Reading reading) {
        return ResponseEntity.created(URI.create("/reading/" + reading.getId()))
                .body(readingService.createReading(reading));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reading> putReading(@PathVariable Integer id, @RequestBody Reading reading) {
        reading.setId(id);
        return ResponseEntity.ok(readingService.updateReading(reading));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReading(@PathVariable Integer id) {
        return ResponseEntity.accepted().body(readingService.deleteReading(id));
    }

    @GetMapping // get readings from last minutes
    public ResponseEntity<List<ReadingInformation>> getReadingsFromLastMinutes(
            @RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime timestamp) {
        List<ReadingInformation> readings = readingService.getReadingsFromLastMinutes(minutes, timestamp);
        for (ReadingInformation info : readings) {
            info.setReadings(null);
        }
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/address")
    public ResponseEntity<List<ReadingInformation>> getReadingsFromLastMinutesByAddress(@RequestBody List<String> address,
            @RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
                System.out.println("QUE");
                System.out.println(address);
        List<ReadingInformation> readings = readingService.getReadingsFromLastMinutesByAddress(address, minutes,
                timestamp);
        for (ReadingInformation info : readings) {
            info.setReadings(null);
        }
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/address/region")
    public ResponseEntity<List<ReadingInformation>> getReadingsFromLastMinutesByAddressRegion(
            @RequestBody List<String> regions,
            @RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        List<ReadingInformation> readings = readingService.getReadingsFromLastMinutesByAddressRegion(regions, minutes,
                timestamp);
        for (ReadingInformation info : readings) {
            info.setReadings(null);
        }
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/radar")
    public ResponseEntity<List<ReadingInformation>> getReadingsFromLastMinutesByRadar(@RequestBody List<Radar> radar,
            @RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        List<ReadingInformation> readings = readingService.getReadingsFromLastMinutesByRadar(radar, minutes, timestamp);
        for (ReadingInformation info : readings) {
            info.setReadings(null);
        }
        return ResponseEntity.ok(readings);
    }
}
