package com.sqlutions.api_4_semestre_backend.entity;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "radar")
public class Radar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_end")
    private Address address;

    @Column(name = "localizacao", columnDefinition = "GEOMETRY(Point, 4326)")
    private Point location;

    @Column(name = "vel_reg")
    private Integer regulatedSpeed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Integer getRegulatedSpeed() {
        return regulatedSpeed;
    }

    public void setRegulatedSpeed(Integer regulatedSpeed) {
        this.regulatedSpeed = regulatedSpeed;
    }

}
