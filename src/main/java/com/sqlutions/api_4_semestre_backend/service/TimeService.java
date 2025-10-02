package com.sqlutions.api_4_semestre_backend.service;

public interface TimeService {
    java.time.LocalDateTime getCurrentTimeClampedToDatabase();

    java.time.LocalDateTime getStartTime();
    java.time.LocalDateTime getEndTime();
}
