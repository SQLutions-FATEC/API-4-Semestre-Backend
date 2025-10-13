package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;

public interface IndexService {
    Index getCityIndex(int minutes, java.time.LocalDateTime timestamp);

    Index getRadarIndexes(int minutes, Radar[] radars, java.time.LocalDateTime timestamp);

    Index getRegionIndexes(int minutes, String region, java.time.LocalDateTime timestamp);

    Index getNeighborhoodIndexes(int minutes, String neighborhood, java.time.LocalDateTime timestamp);

    Index getAdressIndexes(int minutes, String adress, java.time.LocalDateTime timestamp);

    Index getIndexFromReadings(List<Reading> readings);
}
