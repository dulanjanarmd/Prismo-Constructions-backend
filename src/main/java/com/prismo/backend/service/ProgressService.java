package com.prismo.backend.service;

import com.prismo.backend.model.ProgressLog;
import com.prismo.backend.model.Project;
import com.prismo.backend.model.User;
import com.prismo.backend.model.Task;
import com.prismo.backend.repository.ProgressLogRepository;
import com.prismo.backend.repository.ProjectRepository;
import com.prismo.backend.repository.TaskRepository;
import com.prismo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.prismo.backend.model.Role;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressLogRepository repository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    public List<ProgressLog> getAllLogs(User currentUser) {
        if (currentUser.getRole() == Role.PROJECT_MANAGER) {
            return repository.findByProjectManagerId(currentUser.getId());
        } else if (currentUser.getRole() == Role.CLIENT) {
            return repository.findByProjectClientId(currentUser.getId());
        } else if (currentUser.getRole() == Role.SITE_ENGINEER) {
            return repository.findBySiteEngineerId(currentUser.getId());
        }
        return repository.findAll();
    }

    public List<ProgressLog> getLogsByProject(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    public ProgressLog createLog(Long projectId, Long taskId, Long engineerId, ProgressLog log) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.setProject(project);
        log.setSiteEngineer(engineer);
        
        if (log.getDate() != null) {
            if (project.getStartDate() != null && log.getDate().isBefore(project.getStartDate())) {
                throw new IllegalArgumentException("Log date cannot be before project start date (" + project.getStartDate() + ")");
            }
            if (project.getEndDate() != null && log.getDate().isAfter(project.getEndDate())) {
                throw new IllegalArgumentException("Log date cannot be after project end date (" + project.getEndDate() + ")");
            }
        }
        
        if (taskId != null) {
            Task task = taskRepository.findById(taskId).orElse(null);
            log.setTask(task);
            if (task != null && log.getDate() != null && task.getDueDate() != null && log.getDate().isAfter(task.getDueDate())) {
                throw new IllegalArgumentException("Log date cannot be after task due date (" + task.getDueDate() + ")");
            }
        }
        if (log.getPhotos() != null) {
            log.getPhotos().forEach(photo -> photo.setProgressLog(log));
        }
        ProgressLog savedLog = repository.save(log);

        List<User> pms = userRepository.findByRole(Role.PROJECT_MANAGER);
        for (User pm : pms) {
            String msg = "New Progress Log submitted for " + project.getName();
            notificationService.createNotification(pm.getId(), msg, "log-" + savedLog.getId());
        }

        return savedLog;
    }

    public ProgressLog updateLog(Long logId, Long taskId, ProgressLog updatedLog) {
        ProgressLog existingLog = repository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        
        existingLog.setDate(updatedLog.getDate());
        existingLog.setWeather(updatedLog.getWeather());
        existingLog.setTemperature(updatedLog.getTemperature());
        existingLog.setManpower(updatedLog.getManpower());
        existingLog.setPercentageCompleted(updatedLog.getPercentageCompleted());
        existingLog.setWorkDone(updatedLog.getWorkDone());
        existingLog.setEquipmentUsed(updatedLog.getEquipmentUsed());
        existingLog.setMaterialsDelivered(updatedLog.getMaterialsDelivered());
        existingLog.setSafetyIncidents(updatedLog.getSafetyIncidents());
        existingLog.setDelayHours(updatedLog.getDelayHours());

        Project project = existingLog.getProject();
        if (updatedLog.getDate() != null && project != null) {
            if (project.getStartDate() != null && updatedLog.getDate().isBefore(project.getStartDate())) {
                throw new IllegalArgumentException("Log date cannot be before project start date (" + project.getStartDate() + ")");
            }
            if (project.getEndDate() != null && updatedLog.getDate().isAfter(project.getEndDate())) {
                throw new IllegalArgumentException("Log date cannot be after project end date (" + project.getEndDate() + ")");
            }
        }

        if (taskId != null) {
            Task task = taskRepository.findById(taskId).orElse(null);
            existingLog.setTask(task);
            if (task != null && updatedLog.getDate() != null && task.getDueDate() != null && updatedLog.getDate().isAfter(task.getDueDate())) {
                throw new IllegalArgumentException("Log date cannot be after task due date (" + task.getDueDate() + ")");
            }
        } else {
            existingLog.setTask(null);
        }

        if (updatedLog.getPhotos() != null) {
            // Simplify photo handling by replacing old ones (in a real app you might want smart merging)
            existingLog.getPhotos().clear();
            updatedLog.getPhotos().forEach(photo -> {
                photo.setProgressLog(existingLog);
                existingLog.getPhotos().add(photo);
            });
        }
        return repository.save(existingLog);
    }

    public void deleteLog(Long logId) {
        repository.deleteById(logId);
    }
}
