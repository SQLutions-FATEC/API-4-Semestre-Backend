package com.sqlutions.api_4_semestre_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sqlutions.api_4_semestre_backend.entity.Region;


public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT r FROM Region r")
    List<Region> findAllRegions();
}