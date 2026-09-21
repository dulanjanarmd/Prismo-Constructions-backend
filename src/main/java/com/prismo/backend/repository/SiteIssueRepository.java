package com.prismo.backend.repository;

import com.prismo.backend.model.SiteIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteIssueRepository extends JpaRepository<SiteIssue, Long> {
    List<SiteIssue> findByProjectId(Long projectId);
    List<SiteIssue> findByTaskId(Long taskId);
    List<SiteIssue> findByProjectManagerId(Long managerId);
    List<SiteIssue> findByProjectClientId(Long clientId);
    List<SiteIssue> findByReportedById(Long reporterId);
}
