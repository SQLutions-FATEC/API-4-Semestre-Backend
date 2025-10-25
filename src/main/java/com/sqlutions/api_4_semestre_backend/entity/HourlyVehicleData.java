package com.sqlutions.api_4_semestre_backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class HourlyVehicleData {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime hour;
    private Integer vehicleCount;
    private Double averageSpeed;

    public HourlyVehicleData(LocalDateTime hour, Integer vehicleCount, Double averageSpeed) {
        this.hour = hour;
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public Integer getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(Integer vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public Double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
}