package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;

public interface ReadingService {
    List<Reading> getReadingsFromLastMinutes(int minutes);

    List<Reading> getReadingsFromLastMinutesByAddress(String[] address, int minutes);

    List<Object[]> getReadingVehicleTypes(int minutes);

    List<Reading> getReadingsFromLastMinutesByRadar(Radar[] radar, int minutes);

    List<Reading> getReadingsFromLastMinutesByAddressRegion(String[] regions, int minutes);

    List<Reading> getReadingsFromLastMinutesByAddressNeighborhood(String[] neighborhoods, int minutes);
}
