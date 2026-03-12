package com.revhire.repository;

import com.revhire.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobSeekerProfileId(Long profileId);

    List<Application> findByJobId(Long jobId);

    List<Application> findByJobIdAndStatus(Long jobId, Application.ApplicationStatus status);

    Optional<Application> findByJobIdAndJobSeekerProfileId(Long jobId, Long profileId);

    boolean existsByJobIdAndJobSeekerProfileId(Long jobId, Long profileId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.company.id = :companyId AND a.status = 'APPLIED'")
    long countPendingByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT a FROM Application a WHERE a.job.company.id = :companyId AND " +
           "(:status IS NULL OR a.status = :status)")
    List<Application> findByCompanyIdAndStatus(@Param("companyId") Long companyId,
                                                @Param("status") Application.ApplicationStatus status);
}
