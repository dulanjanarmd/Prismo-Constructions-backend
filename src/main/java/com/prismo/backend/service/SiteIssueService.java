package com.prismo.backend.service;

import com.prismo.backend.model.Project;
import com.prismo.backend.model.SiteIssue;
import com.prismo.backend.model.User;
import com.prismo.backend.repository.ProjectRepository;
import com.prismo.backend.repository.SiteIssueRepository;
import com.prismo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.prismo.backend.model.Role;

@Service
public class SiteIssueService {

    @Autowired
    private SiteIssueRepository repository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.prismo.backend.repository.TaskRepository taskRepository;
    
    @Autowired
    private NotificationService notificationService;

    public List<SiteIssue> getIssuesByProject(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    public List<SiteIssue> getAllIssues() {
        return repository.findAll();
    }

    public SiteIssue createIssue(Long projectId, Long taskId, Long reportedById, SiteIssue issue) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findById(reportedById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (taskId != null) {
            com.prismo.backend.model.Task task = taskRepository.findById(taskId).orElse(null);
            issue.setTask(task);
        }

        issue.setProject(project);
        issue.setReportedBy(user);
        
        if (issue.getStatus() == null) {
            issue.setStatus("OPEN");
        }

        SiteIssue savedIssue = repository.save(issue);

        // Notify Project Managers
        List<User> pms = userRepository.findByRole(Role.PROJECT_MANAGER);
        for (User pm : pms) {
            String msg = "New Site Issue logged: " + savedIssue.getTitle();
            notificationService.createNotification(pm.getId(), msg, "issue-" + savedIssue.getId());
        }

        return savedIssue;
    }

    public SiteIssue updateIssueStatus(Long issueId, String status, Long assigneeId) {
        SiteIssue issue = repository.findById(issueId).orElseThrow();
        issue.setStatus(status);
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId).orElseThrow();
            issue.setAssignee(assignee);
        }
        return repository.save(issue);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteIssue(Long issueId) {
        List<com.prismo.backend.model.IssueComment> comments = commentRepo.findByIssueIdOrderByCreatedAtAsc(issueId);
        commentRepo.deleteAll(comments);
        List<com.prismo.backend.model.IssueMeeting> meetings = meetingRepo.findByIssueIdOrderByCreatedAtAsc(issueId);
        meetingRepo.deleteAll(meetings);
        repository.deleteById(issueId);
    }

    public SiteIssue updateIssue(Long issueId, SiteIssue updatedData) {
        SiteIssue issue = repository.findById(issueId).orElseThrow();
        if(updatedData.getTitle() != null) issue.setTitle(updatedData.getTitle());
        if(updatedData.getDescription() != null) issue.setDescription(updatedData.getDescription());
        if(updatedData.getSeverity() != null) issue.setSeverity(updatedData.getSeverity());
        if(updatedData.getLocation() != null) issue.setLocation(updatedData.getLocation());
        if(updatedData.getEquipmentInvolved() != null) issue.setEquipmentInvolved(updatedData.getEquipmentInvolved());
        if(updatedData.getEstimatedDelayDays() != null) issue.setEstimatedDelayDays(updatedData.getEstimatedDelayDays());
        return repository.save(issue);
    }

    @Autowired
    private com.prismo.backend.repository.IssueCommentRepository commentRepo;

    public com.prismo.backend.model.IssueComment addComment(Long issueId, Long senderId, com.prismo.backend.model.IssueComment comment) {
        SiteIssue issue = repository.findById(issueId).orElseThrow();
        User sender = userRepository.findById(senderId).orElseThrow();
        comment.setIssue(issue);
        comment.setSender(sender);
        return commentRepo.save(comment);
    }

    public List<com.prismo.backend.model.IssueComment> getComments(Long issueId) {
        return commentRepo.findByIssueIdOrderByCreatedAtAsc(issueId);
    }

    @Autowired
    private com.prismo.backend.repository.IssueMeetingRepository meetingRepo;

    public com.prismo.backend.model.IssueMeeting addMeeting(Long issueId, Long organizerId, com.prismo.backend.model.IssueMeeting meeting) {
        SiteIssue issue = repository.findById(issueId).orElseThrow();
        User organizer = userRepository.findById(organizerId).orElseThrow();
        meeting.setIssue(issue);
        meeting.setOrganizer(organizer);
        return meetingRepo.save(meeting);
    }

    public List<com.prismo.backend.model.IssueMeeting> getMeetings(Long issueId) {
        return meetingRepo.findByIssueIdOrderByCreatedAtAsc(issueId);
    }
}
