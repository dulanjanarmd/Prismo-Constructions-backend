package com.prismo.backend.service;

import com.prismo.backend.dto.CreateUserRequest;
import com.prismo.backend.dto.UpdateUserRequest;
import com.prismo.backend.model.Role;
import com.prismo.backend.model.User;
import com.prismo.backend.model.UserStatus;
import com.prismo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final jakarta.persistence.EntityManager entityManager;

    public User createUser(CreateUserRequest request) {
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin accounts cannot be created via the admin interface. Please contact system administrator.");
        }
        
        if (request.getRole() == Role.CEO) {
            boolean ceoExists = userRepository.findAll().stream()
                    .anyMatch(user -> user.getRole() == Role.CEO);
            if (ceoExists) {
                throw new IllegalArgumentException("CEO account already exists. System can have only one CEO.");
            }
        }
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();
        
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && 
                userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            // Prevent role changes for CEO and Admin
            if (user.getRole() == Role.ADMIN) {
                // Ignore role change for admin
            } else if (user.getRole() == Role.CEO) {
                // Ignore role change for CEO
            } else {
                if (request.getRole() == Role.CEO) {
                    boolean ceoExists = userRepository.findAll().stream()
                            .filter(u -> u.getId() != id)
                            .anyMatch(u -> u.getRole() == Role.CEO);
                    if (ceoExists) {
                        throw new IllegalArgumentException("CEO account already exists. System can have only one CEO.");
                    }
                }
                user.setRole(request.getRole());
            }
        }
        
        return userRepository.save(user);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot delete admin users");
        }
        
        if (user.getRole() == Role.CEO) {
            throw new IllegalArgumentException("Cannot delete CEO users");
        }

        // Unlink user from associated entities to prevent foreign key constraint violations
        entityManager.createQuery("UPDATE ApprovalRequest a SET a.client = null WHERE a.client = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Budget b SET b.createdBy = null WHERE b.createdBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE ConsultationNote c SET c.createdBy = null WHERE c.createdBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Document d SET d.uploadedBy = null WHERE d.uploadedBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Inquiry i SET i.assignedTo = null WHERE i.assignedTo = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Inquiry i SET i.createdBy = null WHERE i.createdBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE IssueComment i SET i.sender = null WHERE i.sender = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE IssueMeeting i SET i.organizer = null WHERE i.organizer = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("DELETE FROM Notification n WHERE n.recipient = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE ProgressLog p SET p.siteEngineer = null WHERE p.siteEngineer = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Project p SET p.client = null WHERE p.client = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Project p SET p.manager = null WHERE p.manager = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Proposal p SET p.createdBy = null WHERE p.createdBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE ProposalDocument p SET p.uploadedBy = null WHERE p.uploadedBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE ProposalResponse p SET p.respondedBy = null WHERE p.respondedBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE SiteIssue s SET s.reportedBy = null WHERE s.reportedBy = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE SiteIssue s SET s.assignee = null WHERE s.assignee = :user").setParameter("user", user).executeUpdate();
        entityManager.createQuery("UPDATE Task t SET t.assignee = null WHERE t.assignee = :user").setParameter("user", user).executeUpdate();
        
        userRepository.delete(user);
    }

    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot deactivate admin users");
        }
        
        if (user.getRole() == Role.CEO) {
            throw new IllegalArgumentException("Cannot deactivate CEO users");
        }
        
        user.setStatus(UserStatus.INACTIVE);
        return userRepository.save(user);
    }

    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    public User suspendUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot suspend admin users");
        }
        
        if (user.getRole() == Role.CEO) {
            throw new IllegalArgumentException("Cannot suspend CEO users");
        }
        
        user.setStatus(UserStatus.SUSPENDED);
        return userRepository.save(user);
    }

    public void resetUserPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
