package com.sqlutions.api_4_semestre_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point; // Importante: classe do JTS

@Entity
@Table(name = "pontos_onibus")
public class BusStop {

    // A chave primária já existe no banco (BIGINT), então não usamos GenerationType
    @Id
    private Long id;

    // Mapeamento da GEOMETRY(Point, 4326)
    // O Hibernate Spatial usará a classe JTS Point
    @Column(name = "ponto", columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point ponto;

    // Construtores
    public BusStop() {
    }

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Point getPonto() {
        return ponto;
    }

    public void setPonto(Point ponto) {
        this.ponto = ponto;
    }
}
