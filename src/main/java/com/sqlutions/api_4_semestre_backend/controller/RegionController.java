package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.RegionMap;
import com.sqlutions.api_4_semestre_backend.service.IndexService;
import com.sqlutions.api_4_semestre_backend.service.TimeService;


@RestController
@RequestMapping("/regions")
public class RegionController {

    // Injeção de dependência do serviço de região
    @Autowired
    private IndexService indexService;

    @Autowired
    private TimeService timeService;

    // Métodos para manipulação de regiões
    @GetMapping
    public List<RegionMap> getRegionsIndex(@RequestParam(defaultValue = "10") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return indexService.getRegionsIndex(minutes,
                timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }
    
    

}
