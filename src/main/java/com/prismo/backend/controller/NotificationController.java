package com.prismo.backend.controller;

import com.prismo.backend.model.Notification;
import com.prismo.backend.model.User;
import com.prismo.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(currentUser.getId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(notificationService.markAsRead(id, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        notificationService.deleteNotification(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearAllNotifications(
            @AuthenticationPrincipal User currentUser) {
        notificationService.clearAllNotifications(currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
