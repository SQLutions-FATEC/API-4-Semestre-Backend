package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqlutions.api_4_semestre_backend.entity.Region;
import com.sqlutions.api_4_semestre_backend.repository.RegionRepository;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public List<Region> listRegions() {
        return regionRepository.findAll();
    }

    @Override
    public Region searchRegionById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Região não encontrada."));
    }

    @Override
    public Region updateRegion(Long id, Region updateRegion) {
        Region region = searchRegionById(id);
        region.setNomeRegiao(updateRegion.getNomeRegiao());
        return regionRepository.save(region);
    }

    @Override
    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }
}
