package com.prismo.backend.service;

import com.prismo.backend.model.Notification;
import com.prismo.backend.model.User;
import com.prismo.backend.repository.NotificationRepository;
import com.prismo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void createNotification(Long recipientId, String message, String referenceId) {
        User recipient = userRepository.findById(recipientId).orElse(null);
        if (recipient != null) {
            Notification notification = new Notification();
            notification.setRecipient(recipient);
            notification.setMessage(message);
            notification.setReferenceId(referenceId);
            notificationRepository.save(notification);
        }
    }
}
