package com.sqlutions.api_4_semestre_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sqlutions.api_4_semestre_backend.entity.Address;
import com.sqlutions.api_4_semestre_backend.projections.AddressGeoData;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = "SELECT " +
                   "e.ende AS nomeEndereco, " +
                   "ST_AsGeoJSON(e.trecho) AS areaRuaGeoJson " + // Converte a LineString para GeoJSON
                   "FROM endereco e", 
           nativeQuery = true)
    List<AddressGeoData> ListAll();

}


