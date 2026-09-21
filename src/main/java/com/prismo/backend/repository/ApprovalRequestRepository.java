package com.prismo.backend.repository;

import com.prismo.backend.model.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    List<ApprovalRequest> findByProjectId(Long projectId);
    List<ApprovalRequest> findByClientId(Long clientId);
    List<ApprovalRequest> findByProjectManagerId(Long managerId);
    List<ApprovalRequest> findByProjectClientId(Long clientId);
}
