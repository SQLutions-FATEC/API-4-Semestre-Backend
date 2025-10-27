package com.sqlutions.api_4_semestre_backend.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingInformation {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Reading> readings;
    private int totalReadings;
    private double averageSpeed;
    private int maxSpeed;
    private int minSpeed;
    private Index index;
    private Map<String, Integer> vehicleTypeCounts = new HashMap<>();

    public ReadingInformation(List<Reading> readings) {
        this.readings = readings;
        this.totalReadings = readings.size();

        if (!readings.isEmpty()) {
            this.startTime = readings.stream()
                    .map(Reading::getDate)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            this.endTime = readings.stream()
                    .map(Reading::getDate)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            this.averageSpeed = readings.stream()
                    .mapToInt(Reading::getSpeed)
                    .average()
                    .orElse(0.0);
            this.maxSpeed = readings.stream()
                    .mapToInt(Reading::getSpeed)
                    .max()
                    .orElse(0);
            this.minSpeed = readings.stream()
                    .mapToInt(Reading::getSpeed)
                    .min()
                    .orElse(0);

            countVehicleTypes();
        }
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

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Reading> getReadings() {
        return readings;
    }

    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }

    public int getTotalReadings() {
        return totalReadings;
    }

    public void setTotalReadings(int totalReadings) {
        this.totalReadings = totalReadings;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(int minSpeed) {
        this.minSpeed = minSpeed;
    }

    public Map<String, Integer> getVehicleTypeCounts() {
        return vehicleTypeCounts;
    }

    private void countVehicleTypes() {
        vehicleTypeCounts.clear();
        if (readings != null) {
            for (Reading reading : readings) {
                String type = reading.getVehicleType();
                vehicleTypeCounts.put(type, vehicleTypeCounts.getOrDefault(type, 0) + 1);
            }
        }
    }
}
