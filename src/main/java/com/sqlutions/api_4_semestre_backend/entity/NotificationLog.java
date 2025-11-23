package com.sqlutions.api_4_semestre_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(columnDefinition = "TEXT")
    private String reportText; // descrição

    private String title; // título do log

    private String indexType; // tipo do índice (Segurança, Trafego ou Volume)

    private Integer indexValue; // valor do índice (1-5)

    private LocalDateTime startedAt; 

    private LocalDateTime completedAt; 

    private boolean success; 

    @Column(columnDefinition = "TEXT")
    private String errorDetails; 

    public NotificationLog() {}

    public NotificationLog(
            String reportText,
            String title,
            String indexType,
            Integer indexValue,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            boolean success,
            String errorDetails
    ) {
        this.reportText = reportText; 
        this.title = title;
        this.indexType = indexType;
        this.indexValue = indexValue;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.success = success;
        this.errorDetails = errorDetails;
    }

    public Long getId() {
        return id; 
    }

    public String getReportText() {
        return reportText; 
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public String getTitle() {
        return title; 
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIndexType() {
        return indexType; 
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public Integer getIndexValue() {
        return indexValue; 
    }

    public void setIndexValue(Integer indexValue) {
        this.indexValue = indexValue;
    }

    public LocalDateTime getStartedAt() {
        return startedAt; 
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt; 
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isSuccess() {
        return success; 
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorDetails() {
        return errorDetails; 
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
}
