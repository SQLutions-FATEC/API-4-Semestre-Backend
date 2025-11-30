package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.repository.NotificationLogRepository;

@Service
public class NotificationLogServiceImpl implements NotificationLogService {

    @Autowired
    private NotificationLogRepository repository;

    @Override
    public List<NotificationLog> getAllLogs() {
        return repository.findAll();
    }

    @Override
    public NotificationLog getLogById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "NotificationLog with id " + id + " not found"));
    }

    @Override
    public NotificationLog createLog(NotificationLog log) {

        if (log.getMessageText() == null || log.getMessageText().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must be provided");
        }

        if (log.getReportText() == null || log.getReportText().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Report text must be provided");
        }

        return repository.save(log);
    }

    @Override
    public NotificationLog createTestLog() {

        NotificationLog log = new NotificationLog();

        Random random = new Random();

        boolean typeSelector = random.nextBoolean();
        String indexType = typeSelector ? "TRANSITO" : "SEGURANCA";
        log.setIndexType(indexType);

        int indexValue = random.nextBoolean() ? 4 : 5;
        log.setIndexValue(indexValue);

        // Gera mensagem automaticamente (exemplo)
        String message = "Notificação de teste criada para " 
                    + randomType.name() 
                    + " com valor " 
                    + randomIndexValue;

        log.setMessageText(message);

        // Deixa reportText e completionDate nulos (vai ser preenchido depois no modal)
        log.setReportText(null);
        log.setCompletionDate(null);

        return repository.save(log);
    }



    @Override
    public NotificationLog updateLog(NotificationLog log) {

        if (log.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Log ID must be provided for update");
        }

        NotificationLog existing = getLogById(log.getId());

        if (log.getMessageText() != null) {
            existing.setMessageText(log.getMessageText());
        }

        if (log.getReportText() != null) {
            existing.setReportText(log.getReportText());
        }

        if (log.getCompletionDate() != null) {
            existing.setCompletionDate(log.getCompletionDate());
        }

        return repository.save(existing);
    }

    @Override
    public Void deleteLog(Long id) {
        getLogById(id); // valida se existe
        repository.deleteById(id);
        return null;
    }
}
