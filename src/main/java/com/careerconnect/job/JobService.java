
package com.careerconnect.job;

import com.careerconnect.user.AppUser;
import com.careerconnect.user.Role;
import com.careerconnect.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(
            JobRepository jobRepository,
            UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public List<Job> getActiveJobs() {
        return jobRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Job> getJobsForRecruiter(String email) {
        AppUser recruiter = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Recruiter account not found"));

        if (recruiter.getRole() != Role.RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view their jobs");
        }

        return jobRepository.findByRecruiter_IdOrderByCreatedAtDesc(
                recruiter.getId());
    }

    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .filter(Job::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"));
    }

    @Transactional
    public Job createJob(CreateJobRequest request, String email) {
        AppUser recruiter = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User account not found"));

        if (recruiter.getRole() != Role.RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can create jobs");
        }

        Job job = new Job();
        job.setTitle(request.title());
        job.setCompany(request.company());
        job.setLocation(request.location());
        job.setJobType(request.jobType());
        job.setDescription(request.description());
        job.setRequirements(request.requirements());
        job.setApplicationDeadline(request.applicationDeadline());
        job.setActive(true);
        job.setRecruiter(recruiter);

        return jobRepository.save(job);
    }
}
