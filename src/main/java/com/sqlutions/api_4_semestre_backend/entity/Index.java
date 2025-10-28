package com.sqlutions.api_4_semestre_backend.entity;

import java.time.LocalDateTime;

public class Index {
    private Integer securityIndex;
    private Integer trafficIndex;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Index() {
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Index(Integer securityIndex, Integer trafficIndex, LocalDateTime startTime, LocalDateTime endTime) {
        this.securityIndex = securityIndex;
        this.trafficIndex = trafficIndex;
        this.startTime = startTime;
        this.endTime = endTime;
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
