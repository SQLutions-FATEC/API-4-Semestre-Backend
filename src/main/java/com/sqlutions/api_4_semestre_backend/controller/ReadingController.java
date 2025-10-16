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
import com.sqlutions.api_4_semestre_backend.service.ReadingService;

@RestController
@RequestMapping("/reading")
public class ReadingController {

    @Autowired
    private ReadingService readingService;

    @GetMapping("/all")
    public ResponseEntity<List<Reading>> getAllReadings() {
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
    public ResponseEntity<List<Reading>> getReadingsFromLastMinutes(@RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            java.time.LocalDateTime timestamp) {
        System.out.println(timestamp);
        return ResponseEntity.ok(readingService.getReadingsFromLastMinutes(minutes, timestamp));
    }

    @GetMapping("/address")
    public ResponseEntity<List<Reading>> getReadingsFromLastMinutesByAddress(@RequestBody String[] address,
            @RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return ResponseEntity.ok(readingService.getReadingsFromLastMinutesByAddress(address, minutes, timestamp));
    }

    @GetMapping("/address/region")
    public ResponseEntity<List<Reading>> getReadingsFromLastMinutesByAddressRegion(@RequestBody String[] region,
            @RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return ResponseEntity.ok(readingService.getReadingsFromLastMinutesByAddressRegion(region, minutes, timestamp));
    }

    @GetMapping("/radar")
    public ResponseEntity<List<Reading>> getReadingsFromLastMinutesByRadar(@RequestBody Radar[] radar,
            @RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return ResponseEntity.ok(readingService.getReadingsFromLastMinutesByRadar(radar, minutes, timestamp));
    }

    @GetMapping("/percentage-types")
    public ResponseEntity<List<Object[]>> getReadingVehicleTypes(@RequestParam(defaultValue = "1") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        // essa função deve retornar uma série de dados para um gráfico de pizza
        // representando a porcentagem de cada tipo de veículo nas leituras
        // SELECT tip_vei, COUNT(*) FROM leitura GROUP BY tip_vei;
        // e depois mapear o resultado para o formato desejado

        // retorno (que eu me lembre): {["carro", "moto", "caminhão"], [1, 2, 3], [[12,
        // 13, 14], [30, 40, 50], [20, 25, 30]]}
        List<Object[]> results = readingService.getReadingVehicleTypes(minutes, timestamp);
        return ResponseEntity.ok(results);
    }

}
