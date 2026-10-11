package com.careerconnect.application;

import java.time.LocalDateTime;

public record MyApplicationResponse(
        Long applicationId,
        Long jobId,
        String jobTitle,
        String company,
        String location,
        String jobType,
        String status,
        LocalDateTime appliedAt
) {
}