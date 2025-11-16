package com.sqlutions.api_4_semestre_backend.entity;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Polygon;

public class RegionMap {

    private String regionName;
    private Polygon areaRegiao;
    private Integer trafficIndex;
    private Integer securityIndex;
    private Integer volumeIndex;
    private Integer overallIndex;
    private Map<String, Integer> vehicleTypeCounts = new HashMap<>();

    public RegionMap(String regionName, Polygon areaRegiao, Integer trafficIndex, Integer securityIndex,
            Integer volumeIndex, Integer overallIndex, Map<String, Integer> vehicleTypeCounts) {
        this.regionName = regionName;
        this.areaRegiao = areaRegiao;
        this.trafficIndex = trafficIndex;
        this.securityIndex = securityIndex;
        this.volumeIndex = volumeIndex;
        this.overallIndex = overallIndex;
        this.vehicleTypeCounts = vehicleTypeCounts;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Polygon getAreaRegiao() {
        return areaRegiao;
    }

    public void setAreaRegiao(Polygon areaRegiao) {
        this.areaRegiao = areaRegiao;
    }

    public Integer getTrafficIndex() {
        return trafficIndex;
    }

    public void setTrafficIndex(Integer trafficIndex) {
        this.trafficIndex = trafficIndex;
    }

    public Integer getSecurityIndex() {
        return securityIndex;
    }

    public void setSecurityIndex(Integer securityIndex) {
        this.securityIndex = securityIndex;
    }

    public Integer getVolumeIndex() {
        return volumeIndex;
    }

    public void setVolumeIndex(Integer volumeIndex) {
        this.volumeIndex = volumeIndex;
    }

    public Integer getOverallIndex() {
        return overallIndex;
    }

    public void setOverallIndex(Integer overallIndex) {
        this.overallIndex = overallIndex;
    }

    public Map<String, Integer> getVehicleTypeCounts() {
        return vehicleTypeCounts;
    }

    public void setVehicleTypeCounts(Map<String, Integer> vehicleTypeCounts) {
        this.vehicleTypeCounts = vehicleTypeCounts;
    }
}
