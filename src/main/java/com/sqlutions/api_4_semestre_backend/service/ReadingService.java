package com.sqlutions.api_4_semestre_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;

import io.micrometer.common.lang.Nullable;

public interface ReadingService {
    List<ReadingInformation> getReadingsFromLastMinutes(int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getReadingsFromLastMinutesByAddress(String[] address, int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getReadingsFromLastMinutesByRadar(Radar[] radar, int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getReadingsFromLastMinutesByAddressRegion(String[] regions, int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getReadingsFromLastMinutesByAddressNeighborhood(String[] neighborhoods, int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getAllReadings();

    List<ReadingInformation> groupReadings(List<Reading> readings);

    Reading getReadingById(Integer id);

    Reading createReading(Reading reading);

    Reading updateReading(Reading reading);

    Void deleteReading(Integer id);
}
