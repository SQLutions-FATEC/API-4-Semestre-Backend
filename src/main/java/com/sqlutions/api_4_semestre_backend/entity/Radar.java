package com.sqlutions.api_4_semestre_backend.entity;

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

    @Column(name = "id_end")
    private Integer adress;

    @Column(name = "latitude")
    private Long latitude;
    
    @Column(name = "longitude")
    private Long longitude;

    @Column(name = "vel_reg")
    private Integer speed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAdress() {
        return adress;
    }

    public void setAdress(Integer adress) {
        this.adress = adress;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

}
    