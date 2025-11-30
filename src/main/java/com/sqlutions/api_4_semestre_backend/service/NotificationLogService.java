package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;

public interface NotificationLogService {

    List<NotificationLog> getAllLogs();

    NotificationLog getLogById(Long id);

    NotificationLog createLog(NotificationLog log);

    NotificationLog updateLog(NotificationLog log);

    Void deleteLog(Long id);
}
