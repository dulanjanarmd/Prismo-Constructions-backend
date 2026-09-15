package com.prismo.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "global_messages")
public class GlobalMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageText;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(name = "message_type")
    private String messageType = "TEXT"; // TEXT, FILE, IMAGE, POLL, EVENT, LINK

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "poll_data", columnDefinition = "TEXT")
    private String pollData;

    @Column(name = "event_date")
    private java.time.LocalDateTime eventDate;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getPollData() {
        return pollData;
    }

    public void setPollData(String pollData) {
        this.pollData = pollData;
    }

    public java.time.LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(java.time.LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
