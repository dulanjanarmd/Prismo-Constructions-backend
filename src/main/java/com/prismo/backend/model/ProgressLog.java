package com.prismo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "progress_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @NotBlank(message = "Weather is required")
    private String weather;

    @NotNull(message = "Manpower count is required")
    @Min(value = 0, message = "Manpower cannot be negative")
    private Integer manpower;

    @NotBlank(message = "Work done description is required")
    @Column(columnDefinition = "TEXT")
    private String workDone;

    @NotNull(message = "Percentage completed is required")
    @DecimalMin(value = "0.0", message = "Percentage cannot be less than 0")
    @DecimalMax(value = "100.0", message = "Percentage cannot be greater than 100")
    private Double percentageCompleted;

    @ManyToOne
    @JoinColumn(name = "site_engineer_id")
    private User siteEngineer;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(columnDefinition = "TEXT")
    private String equipmentUsed;

    @Column(columnDefinition = "TEXT")
    private String materialsDelivered;

    @Column(columnDefinition = "TEXT")
    private String safetyIncidents;

    @Min(value = 0, message = "Delay hours cannot be negative")
    private Integer delayHours;

    @NotNull(message = "Temperature is required")
    private Double temperature;

    // --- New Structured Fields ---

    @NotBlank(message = "Shift type is required")
    private String shiftType; // Day, Night

    @NotBlank(message = "Site conditions are required")
    private String siteConditions; // Dry, Wet, Muddy, etc.

    @Column(columnDefinition = "TEXT")
    private String visitors; // Anyone visiting the site

    @Column(columnDefinition = "TEXT")
    private String nextDayPlan; // Plan for tomorrow

    @Column(columnDefinition = "TEXT")
    private String subcontractors; // List of subcontractors on site

    @Column(columnDefinition = "TEXT")
    private String inspections; // QA/QC inspections performed

    @OneToMany(mappedBy = "progressLog", cascade = CascadeType.ALL)
    private List<Photo> photos;
}
