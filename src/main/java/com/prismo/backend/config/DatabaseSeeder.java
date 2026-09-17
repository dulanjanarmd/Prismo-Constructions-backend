package com.prismo.backend.config;

import com.prismo.backend.model.*;
import com.prismo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final ProgressLogRepository progressLogRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY role VARCHAR(50)");
        } catch (Exception e) {
            System.out.println("Could not alter users table: " + e.getMessage());
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE'");
        } catch (Exception e) {
            System.out.println("Could not add status column to users table: " + e.getMessage());
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE approval_requests MODIFY status VARCHAR(50)");
        } catch (Exception e) {
            System.out.println("Could not alter approval_requests table status: " + e.getMessage());
        }

        if (userRepository.findByEmail("admin@prismo.com").isEmpty()) {
            String defaultPassword = passwordEncoder.encode("password123");
            User admin = User.builder().name("System Admin").email("admin@prismo.com").password(defaultPassword).role(Role.ADMIN).status(UserStatus.ACTIVE).build();
            userRepository.save(admin);
            System.out.println("Admin user force seeded.");
        } else {
            var admin = userRepository.findByEmail("admin@prismo.com").get();
            if (admin.getStatus() == null) {
                admin.setStatus(UserStatus.ACTIVE);
                userRepository.save(admin);
                System.out.println("Admin user status updated.");
            }
        }
        
        if (userRepository.count() <= 1) {
            String defaultPassword = passwordEncoder.encode("password123");

            User ceo = User.builder().name("CEO User").email("ceo@prismo.com").password(defaultPassword).role(Role.CEO).status(UserStatus.ACTIVE).build();
            User admin = User.builder().name("System Admin").email("admin@prismo.com").password(defaultPassword).role(Role.ADMIN).status(UserStatus.ACTIVE).build();
            User pm = User.builder().name("Project Manager 1").email("pm@prismo.com").password(defaultPassword).role(Role.PROJECT_MANAGER).status(UserStatus.ACTIVE).build();
            User engineer = User.builder().name("Site Engineer A").email("engineer@prismo.com").password(defaultPassword).role(Role.SITE_ENGINEER).status(UserStatus.ACTIVE).build();
            User client = User.builder().name("Client Corp").email("client@company.com").password(defaultPassword).role(Role.CLIENT).status(UserStatus.ACTIVE).build();

            try {
                userRepository.saveAll(List.of(ceo, admin, pm, engineer, client));
            } catch (Exception e) {
                System.out.println("Could not seed users: " + e.getMessage());
            }

            Project p1 = Project.builder()
                    .name("Colombo Commercial Complex")
                    .client(client)
                    .location("Colombo 03")
                    .startDate(LocalDate.parse("2026-09-01"))
                    .endDate(LocalDate.parse("2027-12-31"))
                    .description("A multi-story commercial complex in the heart of Colombo.")
                    .status(ProjectStatus.IN_PROGRESS)
                    .progressPercentage(15.0)
                    .build();
            
            projectRepository.save(p1);

            Milestone m1 = Milestone.builder().project(p1).name("Foundation Completed").dueDate(LocalDate.parse("2026-11-01")).status("Incomplete").build();
            Milestone m2 = Milestone.builder().project(p1).name("Structure Completed").dueDate(LocalDate.parse("2027-05-01")).status("Incomplete").build();
            milestoneRepository.saveAll(List.of(m1, m2));

            Task t1 = Task.builder()
                    .project(p1)
                    .title("Site Survey and Marking")
                    .description("Complete the initial topographical survey and mark boundaries.")
                    .priority("High")
                    .dueDate(LocalDate.parse("2026-09-05"))
                    .assignee(engineer)
                    .status(TaskStatus.IN_PROGRESS)
                    .build();
            taskRepository.save(t1);

            ProgressLog l1 = ProgressLog.builder()
                    .project(p1)
                    .date(LocalDate.parse("2026-08-27"))
                    .weather("Sunny")
                    .manpower(12)
                    .workDone("Cleared the initial debris and set up the site office.")
                    .percentageCompleted(2.0)
                    .siteEngineer(engineer)
                    .build();
            progressLogRepository.save(l1);

            ApprovalRequest a1 = ApprovalRequest.builder()
                    .project(p1)
                    .client(client)
                    .title("Foundation Design Approval")
                    .description("Please review and approve the finalized foundation designs.")
                    .status(ApprovalStatus.PENDING)
                    .dateRequested(LocalDate.parse("2026-08-25"))
                    .build();
            approvalRequestRepository.save(a1);

            System.out.println("Database seeded successfully with initial mock data.");
        }
    }
}
