package com.sqlutions.api_4_semestre_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReadingGroupAggregate {

    LocalDateTime timeInterval;
    BigDecimal averageSpeed;
    Long readingCount;
    LocalDateTime endTime;
    LocalDateTime startTime;
    BigDecimal avgSpeed;
    BigDecimal maxSpeed;
    BigDecimal minSpeed;
    BigDecimal avgSpeedLimit;
    Long speedingCount;
    BigDecimal averageSpeedingAmount;
    Long carCount;
    Long camioneteCount;
    Long onibusCount;
    Long vanCount;
    Long caminhaoGrandeCount;
    Long motoCount;
    Long indefinidoCount;

    public ReadingGroupAggregate(LocalDateTime timeInterval, BigDecimal averageSpeed, Long readingCount,
            LocalDateTime endTime, LocalDateTime startTime, BigDecimal avgSpeed, BigDecimal maxSpeed,
            BigDecimal minSpeed, BigDecimal avgSpeedLimit, Long speedingCount, BigDecimal averageSpeedingAmount,
            Long carCount, Long camioneteCount, Long onibusCount, Long vanCount, Long caminhaoGrandeCount,
            Long motoCount, Long indefinidoCount) {
        this.timeInterval = timeInterval;
        this.averageSpeed = averageSpeed;
        this.readingCount = readingCount;
        this.endTime = endTime;
        this.startTime = startTime;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.avgSpeedLimit = avgSpeedLimit;
        this.speedingCount = speedingCount;
        this.averageSpeedingAmount = averageSpeedingAmount;
        this.carCount = carCount;
        this.camioneteCount = camioneteCount;
        this.onibusCount = onibusCount;
        this.vanCount = vanCount;
        this.caminhaoGrandeCount = caminhaoGrandeCount;
        this.motoCount = motoCount;
        this.indefinidoCount = indefinidoCount;
    }

    public void setTimeInterval(LocalDateTime timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setAverageSpeed(BigDecimal averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setReadingCount(Long readingCount) {
        this.readingCount = readingCount;
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

    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
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

    public Long getSpeedingCount() {
        return speedingCount;
    }

    public void setSpeedingCount(Long speedingCount) {
        this.speedingCount = speedingCount;
    }

    public BigDecimal getAverageSpeedingAmount() {
        return averageSpeedingAmount;
    }

    public void setAverageSpeedingAmount(BigDecimal averageSpeedingAmount) {
        this.averageSpeedingAmount = averageSpeedingAmount;
    }

    public Long getCarCount() {
        return carCount;
    }

    public void setCarCount(Long carCount) {
        this.carCount = carCount;
    }

    public Long getCamioneteCount() {
        return camioneteCount;
    }

    public void setCamioneteCount(Long camioneteCount) {
        this.camioneteCount = camioneteCount;
    }

    public Long getOnibusCount() {
        return onibusCount;
    }

    public void setOnibusCount(Long onibusCount) {
        this.onibusCount = onibusCount;
    }

    public Long getVanCount() {
        return vanCount;
    }

    public void setVanCount(Long vanCount) {
        this.vanCount = vanCount;
    }

    public Long getCaminhaoGrandeCount() {
        return caminhaoGrandeCount;
    }

    public void setCaminhaoGrandeCount(Long caminhaoGrandeCount) {
        this.caminhaoGrandeCount = caminhaoGrandeCount;
    }

    public Long getMotoCount() {
        return motoCount;
    }

    public void setMotoCount(Long motoCount) {
        this.motoCount = motoCount;
    }

    public Long getIndefinidoCount() {
        return indefinidoCount;
    }

    public void setIndefinidoCount(Long indefinidoCount) {
        this.indefinidoCount = indefinidoCount;
    }

    public LocalDateTime getTimeInterval() {
        return timeInterval;
    }

    public BigDecimal getAverageSpeed() {
        return averageSpeed;
    }

    public Long getReadingCount() {
        return readingCount;
    }

}