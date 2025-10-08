package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Radar;

public interface RadarService {

    List<Radar> getAllRadars();

    Radar createRadar(Radar radar);

    Radar updateRadar(Radar radar);

    Void deleteRadar(String id);

    Radar getRadarById(String id);
}
