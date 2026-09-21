package com.prismo.backend.controller;

import com.prismo.backend.model.SiteIssue;
import com.prismo.backend.model.User;
import com.prismo.backend.service.SiteIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class SiteIssueController {

    @Autowired
    private SiteIssueService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'SITE_ENGINEER', 'CLIENT')")
    public ResponseEntity<List<SiteIssue>> getAllIssues(
            @RequestParam(required = false) Long projectId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.prismo.backend.model.User currentUser) {
        if (projectId != null) {
            return ResponseEntity.ok(service.getIssuesByProject(projectId));
        }
        return ResponseEntity.ok(service.getAllIssues(currentUser));
    }

    @PostMapping
    public ResponseEntity<SiteIssue> createIssue(
            @RequestParam Long projectId,
            @RequestParam(required = false) Long taskId,
            @RequestBody SiteIssue issue,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.createIssue(projectId, taskId, currentUser.getId(), issue));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SiteIssue> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Long assigneeId) {
        return ResponseEntity.ok(service.updateIssueStatus(id, status, assigneeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateIssue(
            @PathVariable Long id,
            @RequestBody SiteIssue issue) {
        service.updateIssue(id, issue);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        service.deleteIssue(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<com.prismo.backend.model.IssueComment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(service.getComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<com.prismo.backend.model.IssueComment> addComment(
            @PathVariable Long id,
            @RequestBody com.prismo.backend.model.IssueComment comment,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.addComment(id, currentUser.getId(), comment));
    }

    @GetMapping("/{id}/meetings")
    public ResponseEntity<List<com.prismo.backend.model.IssueMeeting>> getMeetings(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMeetings(id));
    }

    @PostMapping("/{id}/meetings")
    public ResponseEntity<com.prismo.backend.model.IssueMeeting> addMeeting(
            @PathVariable Long id,
            @RequestBody com.prismo.backend.model.IssueMeeting meeting,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.addMeeting(id, currentUser.getId(), meeting));
    }
}
