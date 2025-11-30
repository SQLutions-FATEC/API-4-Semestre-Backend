package com.sqlutions.api_4_semestre_backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.service.NotificationLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/logs")
@Tag(name = "Notification Log", description = "Endpoints for managing notification logs")
public class NotificationLogController {

    @Autowired
    private NotificationLogService service;

    @GetMapping
    @Operation(summary = "Get all notification logs")
    @ApiResponse(responseCode = "200", description = "Logs retrieved successfully")
    public ResponseEntity<List<NotificationLog>> getAllLogs() {
        return ResponseEntity.ok(service.getAllLogs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification log by ID")
    @ApiResponse(responseCode = "200", description = "Log retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Log not found")
    public ResponseEntity<NotificationLog> getLogById(@PathVariable Long id) {
        NotificationLog log = service.getLogById(id); // já lança 404 no service
        return ResponseEntity.ok(log);
    }

    @PostMapping
    @Operation(summary = "Create a new notification log")
    @ApiResponse(responseCode = "201", description = "Log created successfully")
    public ResponseEntity<NotificationLog> createLog(@RequestBody NotificationLog log) {
        NotificationLog saved = service.createLog(log);
        return ResponseEntity
                .created(URI.create("/logs/" + saved.getId()))
                .body(saved);
    }

    @PostMapping("/test")
    @Operation(summary = "Create a dummy test notification log")
    @ApiResponse(responseCode = "201", description = "Test log created successfully")
    public ResponseEntity<NotificationLog> createTestLog() {

        NotificationLog saved = service.createTestLog();

        return ResponseEntity
                .created(URI.create("/logs/" + saved.getId()))
                .body(saved);
    }



    @PutMapping
    @Operation(summary = "Update an existing notification log")
    @ApiResponse(responseCode = "200", description = "Log updated successfully")
    @ApiResponse(responseCode = "404", description = "Log not found")
    public ResponseEntity<NotificationLog> updateLog(@RequestBody NotificationLog log) {
        NotificationLog updated = service.updateLog(log);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification log")
    @ApiResponse(responseCode = "202", description = "Log deletion accepted")
    @ApiResponse(responseCode = "404", description = "Log not found")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        return ResponseEntity.accepted()
                .body(service.deleteLog(id));
    }
}
