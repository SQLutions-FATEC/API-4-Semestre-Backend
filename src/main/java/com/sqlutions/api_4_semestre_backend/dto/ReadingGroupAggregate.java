package com.sqlutions.api_4_semestre_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ReadingGroupAggregate {

    LocalDateTime timeInterval;
    BigDecimal averageSpeed;
    Integer totalReadings;
    LocalDateTime endTime;
    LocalDateTime startTime;
    BigDecimal maxSpeed;
    BigDecimal minSpeed;
    BigDecimal avgSpeedLimit;
    Integer speedingCount;
    BigDecimal averageSpeedingAmount;
    BigDecimal avgCarsPerMinute;
    BigDecimal maxCarsPerMinute;
    BigDecimal readingFrequency;
    Integer activeRadarCount;
    private Map<String, Integer> vehicleTypeCounts = new HashMap<>();

    public ReadingGroupAggregate(LocalDateTime timeInterval, BigDecimal averageSpeed, Integer totalReadings,
            LocalDateTime endTime, LocalDateTime startTime, BigDecimal maxSpeed,
            BigDecimal minSpeed, BigDecimal avgSpeedLimit, Integer speedingCount, BigDecimal averageSpeedingAmount,
            BigDecimal avgCarsPerMinute, BigDecimal maxCarsPerMinute, BigDecimal readingFrequency,
            Integer activeRadarCount,
            Integer carCount, Integer camioneteCount,
            Integer onibusCount,
            Integer vanCount,
            Integer caminhaoGrandeCount,
            Integer motoCount, Integer indefinidoCount) {
        this.timeInterval = timeInterval;
        this.averageSpeed = averageSpeed;
        this.totalReadings = totalReadings;
        this.endTime = endTime;
        this.startTime = startTime;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.avgSpeedLimit = avgSpeedLimit;
        this.speedingCount = speedingCount;
        this.averageSpeedingAmount = averageSpeedingAmount;
        this.avgCarsPerMinute = avgCarsPerMinute;
        this.maxCarsPerMinute = maxCarsPerMinute;
        this.readingFrequency = readingFrequency;
        this.activeRadarCount = activeRadarCount;
        this.vehicleTypeCounts.put("Indefinido", indefinidoCount);
        this.vehicleTypeCounts.put("Carro", carCount);
        this.vehicleTypeCounts.put("Camionete", camioneteCount);
        this.vehicleTypeCounts.put("Ônibus", onibusCount);
        this.vehicleTypeCounts.put("Van", vanCount);
        this.vehicleTypeCounts.put("Caminhão grande", caminhaoGrandeCount);
        this.vehicleTypeCounts.put("Moto", motoCount);
    }

    public void setTimeInterval(LocalDateTime timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setAverageSpeed(BigDecimal averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setTotalReadings(Integer totalReadings) {
        this.totalReadings = totalReadings;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(BigDecimal maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public BigDecimal getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(BigDecimal minSpeed) {
        this.minSpeed = minSpeed;
    }

    public BigDecimal getAvgSpeedLimit() {
        return avgSpeedLimit;
    }

    public void setAvgSpeedLimit(BigDecimal avgSpeedLimit) {
        this.avgSpeedLimit = avgSpeedLimit;
    }

    public Integer getSpeedingCount() {
        return speedingCount;
    }

    public void setSpeedingCount(Integer speedingCount) {
        this.speedingCount = speedingCount;
    }

    public BigDecimal getAverageSpeedingAmount() {
        return averageSpeedingAmount;
    }

    public void setAverageSpeedingAmount(BigDecimal averageSpeedingAmount) {
        this.averageSpeedingAmount = averageSpeedingAmount;
    }

    public Map<String, Integer> getVehicleTypeCounts() {
        return vehicleTypeCounts;
    }

    public LocalDateTime getTimeInterval() {
        return timeInterval;
    }

    public BigDecimal getAverageSpeed() {
        return averageSpeed;
    }

    public Integer getTotalReadings() {
        return totalReadings;
    }

    public BigDecimal getAvgCarsPerMinute() {
        return avgCarsPerMinute;
    }

    public BigDecimal getMaxCarsPerMinute() {
        return maxCarsPerMinute;
    }

    public void setAvgCarsPerMinute(BigDecimal avgCarsPerMinute) {
        this.avgCarsPerMinute = avgCarsPerMinute;
    }

    public void setMaxCarsPerMinute(BigDecimal maxCarsPerMinute) {
        this.maxCarsPerMinute = maxCarsPerMinute;
    }

    public BigDecimal getReadingFrequency() {
        return readingFrequency;
    }

    public void setReadingFrequency(BigDecimal readingFrequency) {
        this.readingFrequency = readingFrequency;
    }

    public Integer getActiveRadarCount() {
        return activeRadarCount;
    }

    public void setActiveRadarCount(Integer activeRadarCount) {
        this.activeRadarCount = activeRadarCount;
    }

    public void setVehicleTypeCounts(Map<String, Integer> vehicleTypeCounts) {
        this.vehicleTypeCounts = vehicleTypeCounts;
    }

}