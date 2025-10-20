package com.sqlutions.api_4_semestre_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sqlutions.api_4_semestre_backend.entity.Regions;

public interface RegionsRepository extends JpaRepository<Regions, Long> {
    
}