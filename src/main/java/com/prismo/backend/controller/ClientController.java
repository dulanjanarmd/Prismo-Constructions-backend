package com.prismo.backend.controller;

import com.prismo.backend.model.ApprovalRequest;
import com.prismo.backend.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.prismo.backend.model.User;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @GetMapping("/approvals")
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'CLIENT')")
    public ResponseEntity<List<ApprovalRequest>> getAllApprovals(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getApprovalsForUser(currentUser));
    }

    @PostMapping("/approvals")
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'CLIENT')")
    public ResponseEntity<ApprovalRequest> createApproval(@RequestBody com.prismo.backend.dto.ApprovalRequestDTO dto) {
        return ResponseEntity.ok(service.createApproval(dto));
    }

    @PutMapping("/approvals/{id}")
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'CLIENT')")
    public ResponseEntity<ApprovalRequest> updateApproval(
            @PathVariable Long id,
            @RequestBody com.prismo.backend.dto.ApprovalRequestDTO dto) {
        return ResponseEntity.ok(service.updateApproval(id, dto));
    }

    @DeleteMapping("/approvals/{id}")
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER')")
    public ResponseEntity<Void> deleteApproval(@PathVariable Long id) {
        service.deleteApproval(id);
        return ResponseEntity.noContent().build();
    }
}
