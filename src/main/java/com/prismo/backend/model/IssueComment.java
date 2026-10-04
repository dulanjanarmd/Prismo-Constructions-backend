package com.prismo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private SiteIssue issue;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String commentType; // GENERAL, INFO_REQUEST, SOLUTION

    @Column(columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }
}
