package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.repository.NotificationLogRepository;

@Service
public class NotificationLogService {

    private final NotificationLogRepository repository;

    public NotificationLogService(NotificationLogRepository repository) {
        this.repository = repository;
    }

    public List<NotificationLog> findAll() {
        return repository.findAll();
    }

    public Optional<NotificationLog> findById(Long id) {
        return repository.findById(id);
    }

    public NotificationLog save(NotificationLog log) {
        return repository.save(log);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public NotificationLog update(Long id, NotificationLog logDetails) {
        NotificationLog existingLog = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Log not found with id: " + id));

        existingLog.setMessage(logDetails.getMessage());
        existingLog.setReportText(logDetails.getReportText());
        existingLog.setCompletionDate(logDetails.getCompletionDate());

        return repository.save(existingLog);
    }
}
