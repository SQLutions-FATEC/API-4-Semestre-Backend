package com.sqlutions.api_4_semestre_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sqlutions.api_4_semestre_backend.entity.Reading;

public interface ReadingRepository extends JpaRepository<Reading, Integer> {
    @Query("SELECT MIN(r.date) FROM Reading r")
    java.time.LocalDateTime findMinDate();

    @Query("SELECT MAX(r.date) FROM Reading r")
    java.time.LocalDateTime findMaxDate();

    List<Reading> findByDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    List<Reading> findByRadarId(String radarId);

    List<Reading> findByRadarAddressRegion(String region);

    List<Reading> findByRadarAddressAddress(String address);

    @Query("SELECT r.vehicleType, COUNT(r) FROM Reading r WHERE r.date BETWEEN :start AND :end GROUP BY r.vehicleType")
    List<Object[]> countReadingsByVehicleTypeDateBetween(java.time.LocalDateTime start,
            java.time.LocalDateTime end);

    List<Reading> findByRadarAddressAddressInAndDateBetween(List<String> address, LocalDateTime startTime,
            LocalDateTime endTime);

    List<Reading> findByRadarIdInAndDateBetween(List<String> radarIds, LocalDateTime startTime,
            LocalDateTime endTime);

    List<Reading> findByRadarAddressRegionInAndDateBetween(List<String> region, LocalDateTime startTime,
            LocalDateTime endTime);

    List<Reading> findByRadarAddressInAndDateBetween(List<String> address, LocalDateTime startTime,
            LocalDateTime endTime);

    List<Reading> findByDateBetweenAndVehicleType(LocalDateTime startTime, LocalDateTime endTime,
            String vehicleType);

    @Query("SELECT DISTINCT r.vehicleType FROM Reading r WHERE r.date BETWEEN :startTime AND :endTime")
    List<String> findDistinctVehicleTypesBetweenDates(LocalDateTime startTime, LocalDateTime endTime);

    // Métodos para filtrar por região e tipo de veículo combinados
    List<Reading> findByRadarAddressRegionInAndDateBetweenAndVehicleType(List<String> regions,
            LocalDateTime startTime, LocalDateTime endTime, String vehicleType);

    @Query("SELECT DISTINCT r.vehicleType FROM Reading r WHERE r.radar.address.region IN :regions AND r.date BETWEEN :startTime AND :endTime")
    List<String> findDistinctVehicleTypesByRegionAndDateBetween(List<String> regions, LocalDateTime startTime,
            LocalDateTime endTime);
}