package com.sqlutions.api_4_semestre_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;

import io.micrometer.common.lang.Nullable;

public interface ReadingService {
    List<ReadingInformation> getReadingsFromLastMinutes(int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getReadingsFromLastMinutesByAddress(List<String> address, int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getReadingsFromLastMinutesByRadar(List<String> radar, int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getReadingsFromLastMinutesByAddressRegion(List<String> regions, int minutes, @Nullable LocalDateTime startDate);

    List<ReadingInformation> getAllReadings();

    List<ReadingInformation> groupReadings(List<Reading> readings);

    Reading getReadingById(Integer id);

    Reading createReading(Reading reading);

    Reading updateReading(Reading reading);

    Void deleteReading(Integer id);

    ReadingGroupAggregate getReadings(int minutes, @Nullable LocalDateTime timestamp, @Nullable List<String> radarId, @Nullable List<String> addresses, @Nullable List<String> regionId);

    List<ReadingGroupAggregate> getReadingSeries(int minutes, @Nullable LocalDateTime timestamp, @Nullable List<String> radarIds, @Nullable List<String> addresses, @Nullable List<String> regionIds);
}
