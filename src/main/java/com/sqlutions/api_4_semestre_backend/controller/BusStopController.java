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

@RestController
@RequestMapping("/bus-stop")
public class BusStopController {

    @Autowired
    private BusStopService busStopService;

    @GetMapping
    public List<BusStop> listBusStops() {
        return busStopService.listBusStops();
    }

    @GetMapping("/{id}")
    public BusStop searchBusStop(@PathVariable Long id) {
        return busStopService.searchBusStopById(id);
    }


    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        busStopService.deleteBusStop(id);
    }
}
