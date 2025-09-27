package com.sqlutions.api_4_semestre_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;

public interface ReadingRepository extends JpaRepository<Reading, Integer> {
    // estrutura:
    // CREATE TABLE leitura (
    // id SERIAL PRIMARY KEY,
    // id_rad VARCHAR(9) NOT NULL, --refere-se ao id do radar
    // dat_hora TIMESTAMP NOT NULL,
    // tip_vei tipo_veiculo NOT NULL,
    // vel INT NOT NULL,
    // CONSTRAINT fk_leitura_radar FOREIGN KEY (id_rad) REFERENCES radar(id)
    // );

    // funções úteis:
    // pegar a primeira leitura do banco de dados
    // pegar a última leitura do banco de dados
    // pegar todas as leituras de um período de tempo
    // pegar todas as leituras de um radar específico
    // pegar todas as leituras de uma região específica

    Reading findFirstByOrderByDateAsc();

    Reading findFirstByOrderByDateDesc();

    @Query("SELECT MIN(r.date) FROM Reading r")
    java.time.LocalDateTime findMinDate();

    @Query("SELECT MAX(r.date) FROM Reading r")
    java.time.LocalDateTime findMaxDate();

    List<Reading> findByDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    List<Reading> findByRadarId(String radarId);

    List<Reading> findByRadarAddressRegion(String region);

    @Query("SELECT r.vehicleType, COUNT(r) FROM Reading r WHERE r.date BETWEEN :start AND :end GROUP BY r.vehicleType")
    List<Object[]> countReadingsByVehicleTypeDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    List<Reading> findByRadarAddressInAndDateBetween(List<String> addresses, LocalDateTime startTime,
            LocalDateTime endTime);

    List<Reading> findByRadarInAndDateBetween(List<Radar> radars, LocalDateTime startTime,
            LocalDateTime endTime);

    List<Reading> findByRadarAddressRegionInAndDateBetween(List<String> regions, LocalDateTime startTime,
            LocalDateTime endTime);

    List<Reading> findByRadarAddressNeighborhoodInAndDateBetween(List<String> neighborhoods, LocalDateTime startTime,
            LocalDateTime endTime);
}