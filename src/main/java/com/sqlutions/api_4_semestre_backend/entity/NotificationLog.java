package com.sqlutions.api_4_semestre_backend.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "log_notificacao")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private User user;

    @Column(name = "mensagem", columnDefinition = "TEXT", nullable = false)
    private String messageText;

    @Column(name = "texto_relatorio", columnDefinition = "TEXT")
    private String reportText;

    @Column(name = "tipo_indice", nullable = false)
    private String indexType;

    @Column(name = "valor_indice", nullable = false)
    private Integer indexValue;

    @Column(name = "data_emissao")
    private LocalDateTime emissionDate;

    @Column(name = "data_conclusao")
    private LocalDateTime completionDate;

    public NotificationLog() {
    }

    public NotificationLog(String reportText, String messageText) {
        this.reportText = reportText;
        this.messageText = messageText;
    }

    public NotificationLog(User user, String messageText, String reportText, LocalDateTime emissionDate, LocalDateTime completionDate) {
        this.user = user;
        this.messageText = messageText;
        this.reportText = reportText;
        this.emissionDate = emissionDate;
        this.completionDate = completionDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
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


    public LocalDateTime getEmissionDate() {
        return emissionDate;
    }

    public void setEmissionDate(LocalDateTime emissionDate) {
        this.emissionDate = emissionDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

}