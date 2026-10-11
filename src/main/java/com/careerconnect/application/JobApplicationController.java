package com.careerconnect.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobApplicationController {

    private final JobApplicationService applicationService;

    public JobApplicationController(
            JobApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<Map<String, Object>> applyForJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please sign in to apply"));
        }

        JobApplication application = applicationService.apply(
                jobId, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Application submitted successfully",
                        "applicationId", application.getId(),
                        "jobId", application.getJob().getId(),
                        "status", application.getStatus()
                )
        );
        
    }
    
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Please sign in to view applications"));
        }

        return ResponseEntity.ok(
                applicationService.getMyApplications(
                        authentication.getName()));
    }

}