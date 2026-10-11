
package com.careerconnect.job;

import java.time.LocalDate;

public record CreateJobRequest(
    String title,
    String company,
    String location,
    String jobType,
    String description,
    String requirements,
    LocalDate applicationDeadline
) {}
