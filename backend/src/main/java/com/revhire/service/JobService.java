package com.revhire.service;

import com.revhire.dto.JobDto;
import com.revhire.entity.*;
import com.revhire.exception.AppException;
import com.revhire.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private static final Logger logger = LogManager.getLogger(JobService.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Transactional
    public JobDto.JobResponse createJob(Long userId, JobDto.CreateJobRequest request) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company profile not found"));

        Job job = new Job();
        mapRequestToJob(request, job);
        job.setCompany(company);
        job.setStatus(Job.JobStatus.ACTIVE);

        job = jobRepository.save(job);
        logger.info("Job created: {} by company: {}", job.getTitle(), company.getName());
        return mapJobToResponse(job, null, false, false);
    }

    public List<JobDto.JobResponse> getEmployerJobs(Long userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company not found"));
        return jobRepository.findByCompanyId(company.getId())
                .stream().map(j -> mapJobToResponse(j, company, false, false))
                .collect(Collectors.toList());
    }

    public List<JobDto.JobResponse> searchJobs(JobDto.JobSearchRequest request, Long userId) {
        List<Job> jobs = jobRepository.searchJobs(
                request.getTitle(), request.getLocation(),
                request.getJobType(), request.getMaxExperience(),
                request.getCompanyName());

        JobSeekerProfile profile = null;
        if (userId != null) {
            profile = jobSeekerProfileRepository.findByUserId(userId).orElse(null);
        }

        final JobSeekerProfile finalProfile = profile;
        return jobs.stream().map(j -> {
            boolean applied = finalProfile != null && applicationRepository.existsByJobIdAndJobSeekerProfileId(j.getId(), finalProfile.getId());
            boolean saved = finalProfile != null && savedJobRepository.existsByJobSeekerProfileIdAndJobId(finalProfile.getId(), j.getId());
            return mapJobToResponse(j, j.getCompany(), applied, saved);
        }).collect(Collectors.toList());
    }

    public JobDto.JobResponse getJobById(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException("Job not found"));

        JobSeekerProfile profile = null;
        if (userId != null) {
            profile = jobSeekerProfileRepository.findByUserId(userId).orElse(null);
        }

        boolean applied = profile != null && applicationRepository.existsByJobIdAndJobSeekerProfileId(jobId, profile.getId());
        boolean saved = profile != null && savedJobRepository.existsByJobSeekerProfileIdAndJobId(profile.getId(), jobId);
        return mapJobToResponse(job, job.getCompany(), applied, saved);
    }

    @Transactional
    public JobDto.JobResponse updateJob(Long userId, Long jobId, JobDto.CreateJobRequest request) {
        Job job = getJobForEmployer(userId, jobId);
        mapRequestToJob(request, job);
        job = jobRepository.save(job);
        return mapJobToResponse(job, job.getCompany(), false, false);
    }

    @Transactional
    public void updateJobStatus(Long userId, Long jobId, Job.JobStatus status) {
        Job job = getJobForEmployer(userId, jobId);
        job.setStatus(status);
        jobRepository.save(job);
        logger.info("Job {} status updated to {}", jobId, status);
    }

    @Transactional
    public void deleteJob(Long userId, Long jobId) {
        Job job = getJobForEmployer(userId, jobId);
        jobRepository.delete(job);
        logger.info("Job {} deleted", jobId);
    }

    private Job getJobForEmployer(Long userId, Long jobId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException("Job not found"));
        if (!job.getCompany().getId().equals(company.getId())) {
            throw new AppException("Unauthorized access to this job");
        }
        return job;
    }

    private void mapRequestToJob(JobDto.CreateJobRequest request, Job job) {
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setRequiredEducation(request.getRequiredEducation());
        job.setMinExperienceYears(request.getMinExperienceYears());
        job.setMaxExperienceYears(request.getMaxExperienceYears());
        job.setLocation(request.getLocation());
        job.setIsRemote(request.getIsRemote());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setJobType(request.getJobType());
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setNumberOfOpenings(request.getNumberOfOpenings());
    }

    public JobDto.JobResponse mapJobToResponse(Job job, Company company, boolean applied, boolean saved) {
        JobDto.JobResponse response = new JobDto.JobResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setRequiredEducation(job.getRequiredEducation());
        response.setMinExperienceYears(job.getMinExperienceYears());
        response.setMaxExperienceYears(job.getMaxExperienceYears());
        response.setLocation(job.getLocation());
        response.setIsRemote(job.getIsRemote());
        response.setMinSalary(job.getMinSalary());
        response.setMaxSalary(job.getMaxSalary());
        response.setJobType(job.getJobType());
        response.setStatus(job.getStatus());
        response.setApplicationDeadline(job.getApplicationDeadline());
        response.setNumberOfOpenings(job.getNumberOfOpenings());
        response.setPostedAt(job.getPostedAt());
        response.setAlreadyApplied(applied);
        response.setSaved(saved);

        Company c = company != null ? company : job.getCompany();
        if (c != null) {
            response.setCompanyId(c.getId());
            response.setCompanyName(c.getName());
            response.setCompanyIndustry(c.getIndustry());
            response.setCompanyLocation(c.getLocation());
        }
        return response;
    }
}
