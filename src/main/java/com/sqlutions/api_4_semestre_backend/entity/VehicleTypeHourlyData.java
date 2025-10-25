package com.sqlutions.api_4_semestre_backend.entity;

import java.util.List;

public class VehicleTypeHourlyData {
    private String vehicleType;
    private List<HourlyVehicleData> data;

    public VehicleTypeHourlyData(String vehicleType, List<HourlyVehicleData> data) {
        this.vehicleType = vehicleType;
        this.data = data;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<HourlyVehicleData> getData() {
        return data;
    }

    public void setData(List<HourlyVehicleData> data) {
        this.data = data;
    }
}