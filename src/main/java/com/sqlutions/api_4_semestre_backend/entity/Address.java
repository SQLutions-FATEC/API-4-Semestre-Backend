package com.sqlutions.api_4_semestre_backend.entity;

import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Id; 
import jakarta.persistence.Table;

@Entity
@Table(name = "endereco")
public class Address {

    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "ende")
    private String address;

    @Column(nullable = true, name = "regiao")
    private String region;

    @Column(nullable = true, name = "trecho", columnDefinition = "GEOMETRY(LineString,4326)")
    private org.locationtech.jts.geom.LineString stretch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public org.locationtech.jts.geom.LineString getStretch() {
        return stretch;
    }

    public void setStretch(org.locationtech.jts.geom.LineString stretch) {
        this.stretch = stretch;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
