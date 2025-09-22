package com.sqlutions.api_4_semestre_backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Radar")
public class Radar {
    
    @Id
    @Column(name = "Id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_end")
    private Address address;

    @Column(name = "latitude")
    private BigDecimal latitude;
    
    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "vel_reg")
    private Integer speed;

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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BifDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BiGDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

}
    