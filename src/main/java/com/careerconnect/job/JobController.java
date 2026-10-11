
package com.careerconnect.job;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<Job> getActiveJobs() {
        return jobService.getActiveJobs();
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Job createJob(
            @RequestBody CreateJobRequest request,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please sign in");
        }

        return jobService.createJob(
                request, authentication.getName());
    }

    @GetMapping("/my-jobs")
    public List<Job> getMyJobs(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please sign in");
        }

        return jobService.getJobsForRecruiter(
                authentication.getName());
    }
}
