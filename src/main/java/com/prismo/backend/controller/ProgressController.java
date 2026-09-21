package com.prismo.backend.controller;

import com.prismo.backend.model.ProgressLog;
import com.prismo.backend.model.User;
import com.prismo.backend.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'SITE_ENGINEER', 'CLIENT')")
    public ResponseEntity<List<ProgressLog>> getAllLogs(@org.springframework.security.core.annotation.AuthenticationPrincipal com.prismo.backend.model.User currentUser) {
        return ResponseEntity.ok(service.getAllLogs(currentUser));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'SITE_ENGINEER')")
    public ResponseEntity<List<ProgressLog>> getLogsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.getLogsByProject(projectId));
    }

    @PostMapping
    @PreAuthorize("hasRole('SITE_ENGINEER')")
    public ResponseEntity<ProgressLog> createLog(
            @RequestBody ProgressLog log,
            @RequestParam Long projectId,
            @RequestParam(required = false) Long taskId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.createLog(projectId, taskId, currentUser.getId(), log));
    }

    @PutMapping("/{logId}")
    @PreAuthorize("hasRole('SITE_ENGINEER')")
    public ResponseEntity<ProgressLog> updateLog(
            @PathVariable Long logId,
            @RequestParam(required = false) Long taskId,
            @RequestBody ProgressLog log) {
        return ResponseEntity.ok(service.updateLog(logId, taskId, log));
    }

    @DeleteMapping("/{logId}")
    @PreAuthorize("hasRole('SITE_ENGINEER')")
    public ResponseEntity<?> deleteLog(@PathVariable Long logId) {
        service.deleteLog(logId);
        return ResponseEntity.ok().build();
    }
}
