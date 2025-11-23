package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.service.NotificationLogService;

@RestController
@RequestMapping("/logs")
@CrossOrigin
public class NotificationLogController {

    private final NotificationLogService service;

    public NotificationLogController(NotificationLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationLog> getAllLogs() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public NotificationLog getLogById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public NotificationLog createLog(@RequestBody NotificationLog log) {
        return service.save(log);
    }

    @DeleteMapping("/{id}")
    public void deleteLog(@PathVariable Long id) {
        service.delete(id);
    }
}
