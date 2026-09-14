package com.prismo.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.databind.JsonNode;

@Data
public class ApprovalRequestDTO {
    private String title;
    private String description;
    private String dueDate;
    private Long projectId;
    private String status;
    private LocalDate dateRequested;
    private JsonNode auditTrail;
    private String feedback;
    private String pmReply;
    private JsonNode attachments;
}
