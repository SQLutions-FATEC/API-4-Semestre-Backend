package com.sqlutions.api_4_semestre_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sqlutions.api_4_semestre_backend.entity.Radar;

public interface RadarRepository extends JpaRepository<Radar, String> {
    
}