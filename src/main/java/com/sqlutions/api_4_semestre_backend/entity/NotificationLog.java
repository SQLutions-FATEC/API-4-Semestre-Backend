package com.sqlutions.api_4_semestre_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String recipient;
    private boolean success;
    private LocalDateTime timestamp;

    @Column(length = 500)
    private String errorDetails;

    private String indexType;  
    private Integer indexValue;

    public NotificationLog() {}

    public NotificationLog(String message, String recipient, boolean success, String errorDetails,
                           String indexType, Integer indexValue) {
        this.message = message;
        this.recipient = recipient;
        this.success = success;
        this.errorDetails = errorDetails;
        this.indexType = indexType;
        this.indexValue = indexValue;
        this.timestamp = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
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
}
