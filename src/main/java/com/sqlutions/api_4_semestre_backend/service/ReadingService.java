package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;

import io.micrometer.common.lang.Nullable;

public interface ReadingService {
    List<Reading> getReadingsFromLastMinutes(int minutes, @Nullable java.time.LocalDateTime startDate);

    List<Reading> getReadingsFromLastMinutesByAddress(String[] address, int minutes, @Nullable java.time.LocalDateTime startDate);

    List<Object[]> getReadingVehicleTypes(int minutes, @Nullable java.time.LocalDateTime startDate);

    List<Reading> getReadingsFromLastMinutesByRadar(Radar[] radar, int minutes, @Nullable java.time.LocalDateTime startDate);

    List<Reading> getReadingsFromLastMinutesByAddressRegion(String[] region, int minutes, @Nullable java.time.LocalDateTime startDate);

    List<Reading> getReadingsFromLastMinutesByAddressNeighborhood(String[] neighborhood, int minutes, @Nullable java.time.LocalDateTime startDate);

    List<Reading> getAllReadings();

    Reading getReadingById(Integer id);

    Reading createReading(Reading reading);

    Reading updateReading(Reading reading);

    Void deleteReading(Integer id);
}
