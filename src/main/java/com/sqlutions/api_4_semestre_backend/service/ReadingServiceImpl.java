package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.repository.RadarRepository;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;

@Service
public class ReadingServiceImpl implements ReadingService {
    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private RadarRepository radarRepository;

    @Autowired
    private TimeService timeService;

    @Override
    public List<Object[]> getReadingVehicleTypes(int minutes) {
        return readingRepository.countReadingsByVehicleTypeDateBetween(
                timeService.getCurrentTimeClampedToDatabase().minusMinutes(minutes),
                timeService.getCurrentTimeClampedToDatabase());
    }

    @Override
    public List<Reading> getReadingsFromLastMinutes(int minutes) {
        return readingRepository.findByDateBetween(
                timeService.getCurrentTimeClampedToDatabase().minusMinutes(minutes),
                timeService.getCurrentTimeClampedToDatabase());
    }

    @Override
    public List<Reading> getReadingsFromLastMinutesByAddress(String[] address, int minutes) {
        return readingRepository.findByRadarAddressInAndDateBetween(List.of(address),
                timeService.getCurrentTimeClampedToDatabase().minusMinutes(minutes),
                timeService.getCurrentTimeClampedToDatabase());
    }

    @Override
    public List<Reading> getReadingsFromLastMinutesByRadar(Radar[] radar, int minutes) {
        return readingRepository.findByRadarInAndDateBetween(List.of(radar),
                timeService.getCurrentTimeClampedToDatabase().minusMinutes(minutes),
                timeService.getCurrentTimeClampedToDatabase());
    }

    @Override
    public List<Reading> getReadingsFromLastMinutesByAddressRegion(String[] regions, int minutes) {
        return readingRepository.findByRadarAddressRegionInAndDateBetween(List.of(regions),
                timeService.getCurrentTimeClampedToDatabase().minusMinutes(minutes),
                timeService.getCurrentTimeClampedToDatabase());
    }

    @Override
    public List<Reading> getReadingsFromLastMinutesByAddressNeighborhood(String[] neighborhoods, int minutes) {
        return readingRepository.findByRadarAddressNeighborhoodInAndDateBetween(List.of(neighborhoods),
                timeService.getCurrentTimeClampedToDatabase().minusMinutes(minutes),
                timeService.getCurrentTimeClampedToDatabase());
    }

    @Override
    public List<Reading> getAllReadings() {
        return readingRepository.findAll();
    }

    @Override
    public Reading getReadingById(Integer id) {
        return readingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading with id " + id + " not found"));
    }

    @Override
    public Reading createReading(Reading reading) {
        String radarId = reading.getRadar().getId();
        Radar radar = radarRepository.findById(radarId).orElse(null);
        if (radar == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Radar with id " + radarId + " not found");
        } else {
            reading.setRadar(radar);
        }
        return readingRepository.save(reading);
    }

    @Override
    public Reading updateReading(Reading reading) {
        String radarId = reading.getRadar().getId();
        Radar radar = radarRepository.findById(radarId).orElse(null);
        if (radar == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Radar with id " + radarId + " not found");
        } else {
            reading.setRadar(radar);
        }
        return readingRepository.save(reading);
    }

    @Override
    public Void deleteReading(Integer id) {
        readingRepository.deleteById(id);
        return null;
    }

}
