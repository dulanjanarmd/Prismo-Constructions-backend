package com.prismo.backend.controller;

import com.prismo.backend.model.GlobalMessage;
import com.prismo.backend.model.User;
import com.prismo.backend.service.GlobalMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class GlobalMessageController {

    @Autowired
    private GlobalMessageService service;

    @GetMapping("/{projectId}")
    public ResponseEntity<List<GlobalMessage>> getMessages(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.getMessagesByProject(projectId));
    }

    @PostMapping("/{projectId}")
    public ResponseEntity<GlobalMessage> sendMessage(
            @PathVariable Long projectId,
            @RequestBody GlobalMessage message,
            @AuthenticationPrincipal User currentUser) {
        message.setProjectId(projectId);
        return ResponseEntity.ok(service.sendMessage(currentUser.getId(), message));
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<GlobalMessage> updateMessage(
            @PathVariable Long messageId,
            @RequestBody GlobalMessage message,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.updateMessage(messageId, currentUser.getId(), message.getMessageText()));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal User currentUser) {
        service.deleteMessage(messageId, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
