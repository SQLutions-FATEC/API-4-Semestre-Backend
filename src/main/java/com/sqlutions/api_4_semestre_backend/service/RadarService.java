package com.sqlutions.api_4_semestre_backend.service;

public interface RadarService {
    Radar createRadar(Radar radar);
    List<Radar> getAllRadars();
    Optional<Radar> getRadarById(String id);
    Radar updateRadar(String id, Radar radar);
    void deleteRadar(String id);
    List<Radar> getRadarsByAddress(Long addressId);
}
