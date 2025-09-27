package com.sqlutions.api_4_semestre_backend.entity;

public class Index {
    private Integer securityIndex;
    private Integer trafficIndex;

    public Index(Integer securityIndex, Integer trafficIndex) {
        this.securityIndex = securityIndex;
        this.trafficIndex = trafficIndex;
    }

    public Integer getSecurityIndex() {
        return securityIndex;
    }

    public void setSecurityIndex(Integer securityIndex) {
        this.securityIndex = securityIndex;
    }

    public Integer getTrafficIndex() {
        return trafficIndex;
    }

    public void setTrafficIndex(Integer trafficIndex) {
        this.trafficIndex = trafficIndex;
    }

    public Integer getCombinedIndex() {
        return (this.trafficIndex + this.securityIndex) / 2;
    }

}
