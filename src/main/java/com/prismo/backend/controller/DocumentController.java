package com.prismo.backend.controller;

import com.prismo.backend.dto.DocumentResponse;
import com.prismo.backend.model.Document;
import com.prismo.backend.model.Project;
import com.prismo.backend.model.User;
import com.prismo.backend.repository.DocumentRepository;
import com.prismo.backend.repository.ProjectRepository;
import com.prismo.backend.repository.UserRepository;
import com.prismo.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(@PathVariable Long projectId) {
        return ResponseEntity.ok(documentRepository.findByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String note,
            Authentication authentication) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        User uploader = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String fileUrl = fileStorageService.store(file);

        Document document = Document.builder()
                .project(project)
                .fileName(name == null || name.isBlank() ? file.getOriginalFilename() : name)
                .fileUrl(fileUrl)
                .category(category)
                .note(note)
                .uploadedBy(uploader)
                .build();
        return ResponseEntity.ok(toResponse(documentRepository.save(document)));
    }

    private DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .category(document.getCategory())
                .note(document.getNote())
                .uploadedBy(document.getUploadedBy() == null ? null : document.getUploadedBy().getName())
                .createdAt(document.getCreatedAt())
                .build();
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long projectId, @PathVariable Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        if (!document.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Document does not belong to this project");
        }
        fileStorageService.delete(document.getFileUrl());
        documentRepository.delete(document);
        return ResponseEntity.noContent().build();
    }
}