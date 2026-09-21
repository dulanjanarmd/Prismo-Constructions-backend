package com.prismo.backend.dto;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;

@Value
@Builder
public class DocumentResponse {
    Long id;
    String fileName;
    String fileUrl;
    String category;
    String note;
    String uploadedBy;
    LocalDateTime createdAt;
}
