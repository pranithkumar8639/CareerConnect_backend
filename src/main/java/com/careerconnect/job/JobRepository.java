
package com.careerconnect.job;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByActiveTrueOrderByCreatedAtDesc();

    List<Job> findByRecruiter_IdOrderByCreatedAtDesc(Long recruiterId);
}
