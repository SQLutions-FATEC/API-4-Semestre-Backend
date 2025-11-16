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
}
