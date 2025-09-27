package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    // get readings from last {minutes: number = 1, addresses?: [address], radars?: [radar], vehicles?: [vehicle_type]}
    
    @GetMapping // get readings from last minutes
    public List<Reading> getReadingsFromLastMinutes(@RequestParam(defaultValue = "1") int minutes) {
        return readingService.getReadingsFromLastMinutes(minutes);
    }

    @GetMapping("/address") // provavelmente não será utilizado. não acho que é possível enviar os endereços para o usuários e exibi-los.
    public List<Reading> getReadingsFromLastMinutesByAddress(@RequestBody String[] address, @RequestParam(defaultValue = "1") int minutes) {
        return readingService.getReadingsFromLastMinutesByAddress(address, minutes);
    }
    @GetMapping("/address/region")
    public List<Reading> getReadingsFromLastMinutesByAddressRegion(@RequestBody String[] regions, @RequestParam(defaultValue = "1") int minutes) {
        return readingService.getReadingsFromLastMinutesByAddressRegion(regions, minutes);
    }
    @GetMapping("/address/neighborhood")
    public List<Reading> getReadingsFromLastMinutesByAddressNeighborhood(@RequestBody String[] neighborhoods, @RequestParam(defaultValue = "1") int minutes) {
        return readingService.getReadingsFromLastMinutesByAddressNeighborhood(neighborhoods, minutes);
    }

    @GetMapping("/radar")
    public List<Reading> getReadingsFromLastMinutesByRadar(@RequestBody Radar[] radar, @RequestParam(defaultValue = "1") int minutes) {
        return readingService.getReadingsFromLastMinutesByRadar(radar, minutes);
    }

    @GetMapping("/percentage-types")
    public List<Object[]> getReadingVehicleTypes(@RequestParam(defaultValue = "1") int minutes) {
        // essa função deve retornar uma série de dados para um gráfico de pizza
        // representando a porcentagem de cada tipo de veículo nas leituras
        // SELECT tip_vei, COUNT(*) FROM leitura GROUP BY tip_vei;
        // e depois mapear o resultado para o formato desejado

        // retorno (que eu me lembre): {["carro", "moto", "caminhão"], [1, 2, 3], [[12, 13, 14], [30, 40, 50], [20, 25, 30]]}
        List<Object[]> results = readingService.getReadingVehicleTypes(minutes);
        return results;
    }

}
