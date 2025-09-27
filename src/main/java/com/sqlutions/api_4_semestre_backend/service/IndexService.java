package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.Radar;

public interface IndexService {
    Integer getCityIndex(int minutes, java.time.LocalDateTime timestamp);

    Integer getRadarIndexes(int minutes, Radar[] radars, java.time.LocalDateTime timestamp);

    Integer getRegionIndexes(int minutes, String region, java.time.LocalDateTime timestamp);
}
