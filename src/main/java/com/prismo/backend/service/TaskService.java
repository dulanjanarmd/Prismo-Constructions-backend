package com.prismo.backend.service;

import com.prismo.backend.model.Project;
import com.prismo.backend.model.Task;
import com.prismo.backend.model.TaskStatus;
import com.prismo.backend.model.User;
import com.prismo.backend.repository.ProjectRepository;
import com.prismo.backend.repository.TaskRepository;
import com.prismo.backend.repository.UserRepository;
import com.prismo.backend.model.Milestone;
import com.prismo.backend.repository.MilestoneRepository;
import com.prismo.backend.repository.ProgressLogRepository;
import com.prismo.backend.repository.SiteIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProgressLogRepository progressLogRepository;
    private final SiteIssueRepository siteIssueRepository;

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public List<Task> getTasksByProject(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    public Task createTask(Long projectId, Long assigneeId, Long milestoneId, Task task) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));
        task.setProject(project);
        task.setAssignee(assignee);
        if (milestoneId != null) {
            Milestone milestone = milestoneRepository.findById(milestoneId).orElse(null);
            task.setMilestone(milestone);
        }
        task.setStatus(TaskStatus.TO_DO);
        return repository.save(task);
    }

    public Task updateTask(Long id, Task updates) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (updates.getTitle() != null)
            task.setTitle(updates.getTitle());
        if (updates.getDescription() != null)
            task.setDescription(updates.getDescription());
        if (updates.getPriority() != null)
            task.setPriority(updates.getPriority());
        if (updates.getDueDate() != null)
            task.setDueDate(updates.getDueDate());
        if (updates.getStatus() != null)
            task.setStatus(updates.getStatus());
        if (updates.getCompletionEvidence() != null)
            task.setCompletionEvidence(updates.getCompletionEvidence());
        return repository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        var progressLogs = progressLogRepository.findByTaskId(id);
        progressLogs.forEach(log -> log.setTask(null));
        progressLogRepository.saveAll(progressLogs);
        
        var siteIssues = siteIssueRepository.findByTaskId(id);
        siteIssues.forEach(issue -> issue.setTask(null));
        siteIssueRepository.saveAll(siteIssues);
        
        repository.delete(task);
    }
}
