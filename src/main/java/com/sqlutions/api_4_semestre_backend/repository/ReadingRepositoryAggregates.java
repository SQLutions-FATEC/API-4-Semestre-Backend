package com.sqlutions.api_4_semestre_backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Your DTO class
import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate; 

public interface ReadingRepositoryAggregates {

    List<ReadingGroupAggregate> findAggregatedReadings(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Optional<String> radarId,
            Optional<Integer> addressId,
            Optional<String> region
    );
}