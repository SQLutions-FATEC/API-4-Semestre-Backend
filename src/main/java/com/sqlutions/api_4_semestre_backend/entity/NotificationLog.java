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
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;
    
    @Column(name = "mensagem", columnDefinition = "TEXT", nullable = false)
    private String messageText;
    
    @Column(name = "texto_relatorio", columnDefinition = "TEXT")
    private String reportText;
    
    @Column(name = "data_emissao")
    private LocalDateTime emissionDate;
    
    @Column(name = "data_conclusao")
    private LocalDateTime completionDate;

    public NotificationLog() {}

    public NotificationLog(String mensagem, String textoRelatorio, String tipoIndice,
                       Integer valorIndice, LocalDateTime dataInicio,
                       LocalDateTime dataConclusao, boolean status, User usuario) {
        this.mensagem = mensagem;
        this.textoRelatorio = textoRelatorio;
        this.tipoIndice = tipoIndice;
        this.valorIndice = valorIndice;
        this.startedAt = dataInicio;
        this.completedAt = dataConclusao;
        this.status = status;
        this.usuario = usuario;
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

    public String getMessage() {
        return messageText;
    }

    public void setMessage(String messageText) {
        this.messageText = messageText;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
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