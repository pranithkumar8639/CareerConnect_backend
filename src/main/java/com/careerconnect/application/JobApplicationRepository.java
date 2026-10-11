package com.careerconnect.application;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    boolean existsByStudent_IdAndJob_Id(Long studentId, Long jobId);

    List<JobApplication> findByStudent_IdOrderByAppliedAtDesc(
            Long studentId);
}