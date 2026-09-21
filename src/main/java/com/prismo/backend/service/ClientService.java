package com.prismo.backend.service;

import com.prismo.backend.model.ApprovalRequest;
import com.prismo.backend.model.ApprovalStatus;
import com.prismo.backend.model.Project;
import com.prismo.backend.model.User;
import com.prismo.backend.dto.ApprovalRequestDTO;
import com.prismo.backend.repository.ApprovalRequestRepository;
import com.prismo.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import com.prismo.backend.model.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ApprovalRequestRepository repository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final com.prismo.backend.repository.UserRepository userRepository;

    public List<ApprovalRequest> getApprovalsForUser(User user) {
        if (user.getRole() == com.prismo.backend.model.Role.CLIENT) {
            return repository.findByProjectClientId(user.getId());
        } else if (user.getRole() == com.prismo.backend.model.Role.PROJECT_MANAGER) {
            return repository.findByProjectManagerId(user.getId());
        }
        return repository.findAll();
    }

    public ApprovalRequest createApproval(ApprovalRequestDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ApprovalRequest request = ApprovalRequest.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .dateRequested(dto.getDateRequested() != null ? dto.getDateRequested() : LocalDate.now())
                .status(ApprovalStatus.valueOf(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "PENDING"))
                .project(project)
                .client(project.getClient())
                .auditTrail(toJson(dto.getAuditTrail()))
                .attachments(toJson(dto.getAttachments()))
                .linkedLogIds(toJson(dto.getLinkedLogIds()))
                .build();

        ApprovalRequest savedRequest = repository.save(request);

        // Notify Client
        if (savedRequest.getClient() != null) {
            String msg = "New Approval Request: " + savedRequest.getTitle();
            notificationService.createNotification(savedRequest.getClient().getId(), msg, "approval-" + savedRequest.getId());
        }

        return savedRequest;
    }

    public ApprovalRequest updateApproval(Long id, ApprovalRequestDTO dto) {
        ApprovalRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval request not found"));

        if (dto.getStatus() != null) {
            request.setStatus(ApprovalStatus.valueOf(dto.getStatus().toUpperCase()));
        }
        if (dto.getAuditTrail() != null) {
            request.setAuditTrail(toJson(dto.getAuditTrail()));
        }
        if (dto.getFeedback() != null) {
            request.setFeedback(dto.getFeedback());
        }
        if (dto.getPmReply() != null) {
            request.setPmReply(dto.getPmReply());
        }
        if (dto.getDueDate() != null) {
            request.setDueDate(dto.getDueDate());
        }
        if (dto.getAttachments() != null) {
            request.setAttachments(toJson(dto.getAttachments()));
        }
        if (dto.getLinkedLogIds() != null) {
            request.setLinkedLogIds(toJson(dto.getLinkedLogIds()));
        }
        ApprovalRequest savedRequest = repository.save(request);

        // If status changed by client or PM, notify the other party
        // For simplicity, we'll notify PMs when it's updated
        if (dto.getStatus() != null && !dto.getStatus().equalsIgnoreCase("PENDING")) {
            List<User> pms = userRepository.findByRole(Role.PROJECT_MANAGER);
            for (User pm : pms) {
                String msg = "Approval Request Updated: " + savedRequest.getTitle();
                notificationService.createNotification(pm.getId(), msg, "approval-" + savedRequest.getId());
            }
        }
        
        return savedRequest;
    }

    public void deleteApproval(Long id) {
        repository.deleteById(id);
    }

    private String toJson(com.fasterxml.jackson.databind.JsonNode value) {
        if (value == null)
            return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid audit trail");
        }
    }
}
