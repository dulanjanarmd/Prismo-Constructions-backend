package com.prismo.backend.service;

import com.prismo.backend.model.GlobalMessage;
import com.prismo.backend.model.User;
import com.prismo.backend.repository.GlobalMessageRepository;
import com.prismo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalMessageService {

    @Autowired
    private GlobalMessageRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public List<GlobalMessage> getMessagesByProject(Long projectId) {
        return repository.findAllByProjectIdOrderByCreatedAtAsc(projectId);
    }

    public GlobalMessage sendMessage(Long senderId, GlobalMessage message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        message.setSender(sender);

        if (message.getRecipient() != null && message.getRecipient().getId() != null) {
            User recipient = userRepository.findById(message.getRecipient().getId()).orElse(null);
            message.setRecipient(recipient);
        }

        GlobalMessage savedMessage = repository.save(message);

        if (savedMessage.getRecipient() != null) {
            String notificationMsg = "New private message from " + sender.getName();
            notificationService.createNotification(savedMessage.getRecipient().getId(), notificationMsg, "message-" + savedMessage.getId());
        }

        return savedMessage;
    }

    public GlobalMessage updateMessage(Long messageId, Long userId, String newText) {
        GlobalMessage message = repository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        message.setMessageText(newText);
        return repository.save(message);
    }

    public void deleteMessage(Long messageId, Long userId) {
        GlobalMessage message = repository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getSender().getId().equals(userId) && !userRepository.findById(userId).get().getRole().equals("admin")) {
            throw new RuntimeException("Unauthorized");
        }
        
        repository.delete(message);
    }
}
