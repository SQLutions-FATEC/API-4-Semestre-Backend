package com.sqlutions.api_4_semestre_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Polygon; 

@Entity
@Table(name = "regioes")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_regiao", length = 100, nullable = false, unique = true)
    private String nomeRegiao;

    // Mapeamento da GEOMETRY(Polygon, 4326)
    @Column(name = "area_regiao", columnDefinition = "geometry(Polygon, 4326)", nullable = false)
    private Polygon areaRegiao;

    // Construtores
    public Region() {
    }

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeRegiao() {
        return nomeRegiao;
    }

    public void setNomeRegiao(String nomeRegiao) {
        this.nomeRegiao = nomeRegiao;
    }

    public Polygon getAreaRegiao() {
        return areaRegiao;
    }

    public void setAreaRegiao(Polygon areaRegiao) {
        this.areaRegiao = areaRegiao;
    }
}
