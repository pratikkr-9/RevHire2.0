package com.revhire.service;

import com.revhire.dto.JobDto;
import com.revhire.entity.Job;
import com.revhire.entity.JobSeekerProfile;
import com.revhire.entity.SavedJob;
import com.revhire.exception.AppException;
import com.revhire.repository.ApplicationRepository;
import com.revhire.repository.JobRepository;
import com.revhire.repository.JobSeekerProfileRepository;
import com.revhire.repository.SavedJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedJobService {

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerProfileRepository profileRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobService jobService;

    @Transactional
    public void saveJob(Long userId, Long jobId) {
        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException("Job not found"));

        if (savedJobRepository.existsByJobSeekerProfileIdAndJobId(profile.getId(), jobId)) {
            throw new AppException("Job already saved");
        }

        SavedJob savedJob = new SavedJob();
        savedJob.setJobSeekerProfile(profile);
        savedJob.setJob(job);
        savedJobRepository.save(savedJob);
    }

    @Transactional
    public void unsaveJob(Long userId, Long jobId) {
        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));
        savedJobRepository.deleteByJobSeekerProfileIdAndJobId(profile.getId(), jobId);
    }

    public List<JobDto.JobResponse> getSavedJobs(Long userId) {
        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));
        return savedJobRepository.findByJobSeekerProfileId(profile.getId())
                .stream().map(sj -> {
                    Job job = sj.getJob();
                    boolean applied = applicationRepository.existsByJobIdAndJobSeekerProfileId(job.getId(), profile.getId());
                    return jobService.mapJobToResponse(job, job.getCompany(), applied, true);
                }).collect(Collectors.toList());
    }
}
