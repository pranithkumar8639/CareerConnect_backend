
package com.careerconnect.application;

import com.careerconnect.job.Job;
import com.careerconnect.job.JobService;
import com.careerconnect.user.AppUser;
import com.careerconnect.user.Role;
import com.careerconnect.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobService jobService;

    public JobApplicationService(
            JobApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobService jobService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobService = jobService;
    }

    @Transactional
    public JobApplication apply(Long jobId, String email) {
        AppUser student = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User account not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can apply for jobs");
        }

        Job job = jobService.getJobById(jobId);

        if (applicationRepository.existsByStudent_IdAndJob_Id(
                student.getId(), job.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already applied for this job");
        }

        return applicationRepository.save(
                new JobApplication(student, job));
    }

    @Transactional(readOnly = true)
    public List<MyApplicationResponse> getMyApplications(
            String email) {
        AppUser student = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User account not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can view their applications");
        }

        return applicationRepository
                .findByStudent_IdOrderByAppliedAtDesc(student.getId())
                .stream()
                .map(application -> new MyApplicationResponse(
                        application.getId(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getJob().getCompany(),
                        application.getJob().getLocation(),
                        application.getJob().getJobType(),
                        application.getStatus(),
                        application.getAppliedAt()
                ))
                .toList();
    }
}
