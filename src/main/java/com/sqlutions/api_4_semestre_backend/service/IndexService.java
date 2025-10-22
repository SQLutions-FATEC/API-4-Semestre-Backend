package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.RegionMap;

public interface IndexService {
    // pegar o nível atual da cidade
    Index getCityIndex(int minutes, java.time.LocalDateTime timestamp);

    // pegar o nível atual de um radar
    Index getRadarIndexes(int minutes, Radar[] radars, java.time.LocalDateTime timestamp);

    // pegar o nível atual de uma região
    Index getRegionIndex(int minutes, String region, java.time.LocalDateTime timestamp);


    //pegar os indices das regioes junto do nome e da linestring
    List<RegionMap> getRegionsIndex(int minutes, java.time.LocalDateTime timestamp);


    Index getAddressIndexes(int minutes, String address, java.time.LocalDateTime timestamp);

    Index getIndexFromReadings(List<Reading> readings);

    // pegar séries temporais de índices
    List<Index> getCityIndexSeries(int minutes, java.time.LocalDateTime timestamp);

    // pegar séries temporais de índices de radares
    List<Index> getRadarIndexesSeries(int minutes, Radar[] radars, java.time.LocalDateTime timestamp);

    // pegar séries temporais de índices de regiões
    List<Index> getRegionIndexSeries(int minutes, String region, java.time.LocalDateTime timestamp);

    // calcular índices a partir de uma lista de leituras agrupadas por tempo
    List<Index> getIndexesWithGroupedReadings(List<Reading> readings);
}
