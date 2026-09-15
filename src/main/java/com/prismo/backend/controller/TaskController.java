package com.prismo.backend.controller;

import com.prismo.backend.model.Task;
import com.prismo.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'SITE_ENGINEER')")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(service.getAllTasks());
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('CEO', 'PROJECT_MANAGER', 'SITE_ENGINEER')")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.getTasksByProject(projectId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'CEO')")
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> body) {
        Task task = new Task();
        task.setTitle((String) body.get("title"));
        task.setDescription((String) body.get("description"));
        task.setPriority((String) body.get("priority"));
        String dueDateStr = (String) body.get("dueDate");
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            task.setDueDate(java.time.LocalDate.parse(dueDateStr));
        }
        Long projectId = Long.valueOf(body.get("projectId").toString());
        Long assigneeId = Long.valueOf(body.get("assigneeId").toString());
        Long milestoneId = body.get("milestoneId") != null ? Long.valueOf(body.get("milestoneId").toString()) : null;
        return ResponseEntity.ok(service.createTask(projectId, assigneeId, milestoneId, task));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'SITE_ENGINEER', 'CEO')")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Task task = new Task();
        if (body.containsKey("title")) task.setTitle((String) body.get("title"));
        if (body.containsKey("description")) task.setDescription((String) body.get("description"));
        if (body.containsKey("priority")) task.setPriority((String) body.get("priority"));
        if (body.containsKey("status") && body.get("status") != null) {
            task.setStatus(com.prismo.backend.model.TaskStatus.valueOf((String) body.get("status")));
        }
        if (body.containsKey("completionEvidence")) task.setCompletionEvidence((String) body.get("completionEvidence"));
        
        if (body.containsKey("dueDate") && body.get("dueDate") != null) {
            String dueDateStr = (String) body.get("dueDate");
            if (!dueDateStr.isEmpty()) {
                task.setDueDate(java.time.LocalDate.parse(dueDateStr));
            }
        }
        
        return ResponseEntity.ok(service.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'CEO')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
