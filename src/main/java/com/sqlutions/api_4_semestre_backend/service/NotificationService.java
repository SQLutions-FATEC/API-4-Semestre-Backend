package com.sqlutions.api_4_semestre_backend.service;

public interface NotificationService {

    void sendAlert(String reportText, String indexType, Integer indexValue);

}
