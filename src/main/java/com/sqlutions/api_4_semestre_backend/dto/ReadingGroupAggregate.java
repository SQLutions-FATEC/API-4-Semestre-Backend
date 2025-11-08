package com.sqlutions.api_4_semestre_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReadingGroupAggregate {

    LocalDateTime timeInterval;
    BigDecimal averageSpeed;
    Integer readingCount;
    LocalDateTime endTime;
    LocalDateTime startTime;
    BigDecimal avgSpeed;
    BigDecimal maxSpeed;
    BigDecimal minSpeed;
    BigDecimal avgSpeedLimit;
    Integer speedingCount;
    BigDecimal averageSpeedingAmount;
    Integer carCount;
    Integer camioneteCount;
    Integer onibusCount;
    Integer vanCount;
    Integer caminhaoGrandeCount;
    Integer motoCount;
    Integer indefinidoCount;

    public ReadingGroupAggregate(LocalDateTime timeInterval, BigDecimal averageSpeed, Integer readingCount,
            LocalDateTime endTime, LocalDateTime startTime, BigDecimal avgSpeed, BigDecimal maxSpeed,
            BigDecimal minSpeed, BigDecimal avgSpeedLimit, Integer speedingCount, BigDecimal averageSpeedingAmount,
            Integer carCount, Integer camioneteCount, Integer onibusCount, Integer vanCount, Integer caminhaoGrandeCount,
            Integer motoCount, Integer indefinidoCount) {
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

    public void setReadingCount(Integer readingCount) {
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

    public Integer getCarCount() {
        return carCount;
    }

    public void setCarCount(Integer carCount) {
        this.carCount = carCount;
    }

    public Integer getCamioneteCount() {
        return camioneteCount;
    }

    public void setCamioneteCount(Integer camioneteCount) {
        this.camioneteCount = camioneteCount;
    }

    public Integer getOnibusCount() {
        return onibusCount;
    }

    public void setOnibusCount(Integer onibusCount) {
        this.onibusCount = onibusCount;
    }

    public Integer getVanCount() {
        return vanCount;
    }

    public void setVanCount(Integer vanCount) {
        this.vanCount = vanCount;
    }

    public Integer getCaminhaoGrandeCount() {
        return caminhaoGrandeCount;
    }

    public void setCaminhaoGrandeCount(Integer caminhaoGrandeCount) {
        this.caminhaoGrandeCount = caminhaoGrandeCount;
    }

    public Integer getMotoCount() {
        return motoCount;
    }

    public void setMotoCount(Integer motoCount) {
        this.motoCount = motoCount;
    }

    public Integer getIndefinidoCount() {
        return indefinidoCount;
    }

    public void setIndefinidoCount(Integer indefinidoCount) {
        this.indefinidoCount = indefinidoCount;
    }

    public LocalDateTime getTimeInterval() {
        return timeInterval;
    }

    public BigDecimal getAverageSpeed() {
        return averageSpeed;
    }

    public Integer getReadingCount() {
        return readingCount;
    }

}