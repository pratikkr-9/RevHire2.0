package com.revhire.repository;

import com.revhire.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    List<Job> findByCompanyId(Long companyId);

    List<Job> findByCompanyIdAndStatus(Long companyId, Job.JobStatus status);

    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:minExp IS NULL OR j.minExperienceYears <= :minExp) AND " +
           "(:companyName IS NULL OR LOWER(j.company.name) LIKE LOWER(CONCAT('%', :companyName, '%')))")
    List<Job> searchJobs(@Param("title") String title,
                         @Param("location") String location,
                         @Param("jobType") Job.JobType jobType,
                         @Param("minExp") Integer minExp,
                         @Param("companyName") String companyName);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId AND j.status = 'ACTIVE'")
    long countActiveByCompanyId(@Param("companyId") Long companyId);
}
