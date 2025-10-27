package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Region;

public interface RegionService {

    List<Region> listRegions();

    Region searchRegionById(Long id);

    Region updateRegion(Long id, Region region);

    void deleteRegion(Long id);
}
