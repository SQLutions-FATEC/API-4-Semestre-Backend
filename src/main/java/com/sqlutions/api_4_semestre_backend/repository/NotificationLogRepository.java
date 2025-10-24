package com.sqlutions.api_4_semestre_backend.repository;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
