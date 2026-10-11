package com.careerconnect.application;

import com.careerconnect.job.Job;
import com.careerconnect.user.AppUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "job_applications",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_job_application_student_job",
        columnNames = {"student_id", "job_id"}
    )
)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private AppUser student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(nullable = false, length = 30)
    private String status = "APPLIED";

    @Column(nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    protected JobApplication() {
    }

    public JobApplication(AppUser student, Job job) {
        this.student = student;
        this.job = job;
        this.status = "APPLIED";
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getStudent() {
        return student;
    }

    public Job getJob() {
        return job;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
}