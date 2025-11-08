package com.sqlutions.api_4_semestre_backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Your DTO class
import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate; 

public interface ReadingRepositoryAggregates {

    List<ReadingGroupAggregate> findAggregatedReadingSeries(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Optional<List<String>> radarIds,
            Optional<List<String>> addresses,
            Optional<List<String>> regions
    );

    ReadingGroupAggregate findSingleAggregatedReading(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Optional<List<String>> radarIds,
            Optional<List<String>> addresses,
            Optional<List<String>> regions
    );
}