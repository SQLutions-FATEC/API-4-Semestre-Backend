package com.sqlutions.api_4_semestre_backend.dto;

public class AddressHeatMap {

    private String nomeEndereco;
    private String areaRuaGeoJson;
    private Integer trafficIndex;
    private Integer securityIndex;
    private Integer volumeIndex;
    private Integer overallIndex;

    public AddressHeatMap(String nomeEndereco, String areaRuaGeoJson, Integer trafficIndex, Integer securityIndex,
            Integer overallIndex) {
        this.nomeEndereco = nomeEndereco;
        this.areaRuaGeoJson = areaRuaGeoJson;
        this.trafficIndex = trafficIndex;
        this.securityIndex = securityIndex;
        this.volumeIndex = volumeIndex;
        this.overallIndex = overallIndex;
    }

    public String getNomeEndereco() {
        return nomeEndereco;
    }

    public void setNomeEndereco(String nomeEndereco) {
        this.nomeEndereco = nomeEndereco;
    }

    public String getAreaRuaGeoJson() {
        return areaRuaGeoJson;
    }

    public void setAreaRuaGeoJson(String areaRuaGeoJson) {
        this.areaRuaGeoJson = areaRuaGeoJson;
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

}
