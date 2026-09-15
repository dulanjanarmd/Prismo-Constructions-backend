package com.prismo.backend.service;

import com.prismo.backend.model.Milestone;
import com.prismo.backend.model.Project;
import com.prismo.backend.repository.MilestoneRepository;
import com.prismo.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.prismo.backend.model.Task;
import com.prismo.backend.repository.TaskRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public List<Milestone> getMilestonesByProject(Long projectId) {
        return milestoneRepository.findByProjectId(projectId);
    }

    public Milestone addMilestone(Long projectId, Milestone milestone) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        milestone.setProject(project);
        return milestoneRepository.save(milestone);
    }

    public Milestone updateMilestone(Long id, Milestone updates) {
        Milestone existing = milestoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milestone not found"));
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getDueDate() != null) existing.setDueDate(updates.getDueDate());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getBudgetAllocated() != null) existing.setBudgetAllocated(updates.getBudgetAllocated());
        if (updates.getDeliverables() != null) existing.setDeliverables(updates.getDeliverables());
        return milestoneRepository.save(existing);
    }

    @Transactional
    public void deleteMilestone(Long id) {
        List<Task> tasks = taskRepository.findByMilestoneId(id);
        for (Task task : tasks) {
            task.setMilestone(null);
            taskRepository.save(task);
        }
        milestoneRepository.deleteById(id);
    }
}
