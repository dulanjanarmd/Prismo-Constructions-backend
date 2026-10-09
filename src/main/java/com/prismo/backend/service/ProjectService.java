package com.prismo.backend.service;

import com.prismo.backend.model.Project;
import com.prismo.backend.repository.ProjectRepository;
import com.prismo.backend.repository.TaskRepository;
import com.prismo.backend.repository.ApprovalRequestRepository;
import com.prismo.backend.repository.SiteIssueRepository;
import com.prismo.backend.repository.ProgressLogRepository;
import com.prismo.backend.repository.DocumentRepository;
import com.prismo.backend.repository.UserRepository;
import com.prismo.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final TaskRepository taskRepository;
    private final ApprovalRequestRepository approvalRepository;
    private final SiteIssueRepository issueRepository;
    private final ProgressLogRepository logRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<Project> getAllProjects() {
        return repository.findAll();
    }

    public List<Project> getProjectsByClientId(Long clientId) {
        return repository.findByClientId(clientId);
    }

    public List<Project> getProjectsByManagerId(Long managerId) {
        return repository.findByManagerId(managerId);
    }

    public Project getProjectById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project createProject(Project project) {
        if (project.getMilestones() != null) {
            project.getMilestones().forEach(m -> m.setProject(project));
        }
        Project savedProject = repository.save(project);

        if (savedProject.getClient() != null && savedProject.getClient().getId() != null) {
            notificationService.createNotification(
                    savedProject.getClient().getId(),
                    "A new project '" + savedProject.getName() + "' has been created for you.",
                    "project-" + savedProject.getId()
            );
        }

        return savedProject;
    }

    public Project updateProject(Long id, Project projectDetails) {
        Project project = getProjectById(id);
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setLocation(projectDetails.getLocation());
        project.setStartDate(projectDetails.getStartDate());
        project.setEndDate(projectDetails.getEndDate());
        project.setStatus(projectDetails.getStatus());
        project.setProgressPercentage(projectDetails.getProgressPercentage());
        if (projectDetails.getClient() != null && projectDetails.getClient().getId() != null) {
            project.setClient(userRepository.findById(projectDetails.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client not found")));
        }
        return repository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        taskRepository.deleteAll(taskRepository.findByProjectId(id));
        approvalRepository.deleteAll(approvalRepository.findByProjectId(id));
        issueRepository.deleteAll(issueRepository.findByProjectId(id));
        logRepository.deleteAll(logRepository.findByProjectId(id));
        documentRepository.deleteAll(documentRepository.findByProjectId(id));
        repository.deleteById(id);
    }
}
