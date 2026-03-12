package com.revhire.repository;

import com.revhire.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByJobSeekerProfileId(Long profileId);
    Optional<SavedJob> findByJobSeekerProfileIdAndJobId(Long profileId, Long jobId);
    boolean existsByJobSeekerProfileIdAndJobId(Long profileId, Long jobId);
    void deleteByJobSeekerProfileIdAndJobId(Long profileId, Long jobId);
}
