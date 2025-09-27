package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;

@Service
public class ReadingServiceImpl implements ReadingService {
    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private TimeService timeService;

    @Override
    public List<Object[]> getReadingTypes() {
        java.time.LocalDateTime timeEnd = timeService.getCurrentTimeClampedToDatabase();
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(6000);
        return readingRepository.countReadingsByVehicleTypeDateBetween(timeStart, timeEnd);
    }

}
