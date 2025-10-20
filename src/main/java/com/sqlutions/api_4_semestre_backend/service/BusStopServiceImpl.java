package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqlutions.api_4_semestre_backend.entity.BusStop;
import com.sqlutions.api_4_semestre_backend.repository.BusStopRepository;

@Service
public class BusStopServiceImpl implements BusStopService {

    @Autowired
    private BusStopRepository busStopRepository;

    @Override
    public List<BusStop> listBusStops() {
        return busStopRepository.findAll();
    }

    @Override
    public BusStop searchBusStopById(Long id) {
        return busStopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parada de ônibus não encontrada."));
    }

    @Override
    public void deleteBusStop(Long id) {
        busStopRepository.deleteById(id);
    }
}
