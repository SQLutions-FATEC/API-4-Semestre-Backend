package com.sqlutions.api_4_semestre_backend.controller;

import java.net.URI;
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

    @GetMapping()
    public ResponseEntity<List<ReadingGroupAggregate>> getReadingGroups(
            @RequestParam int minutes,
            @RequestParam(required = false) String radarId,
            @RequestParam(required = false) Integer addressId,
            @RequestParam(required = false) String regionId) {
        return ResponseEntity.ok(readingService.getReadingGroups(minutes, radarId, addressId, regionId));
    }
}
