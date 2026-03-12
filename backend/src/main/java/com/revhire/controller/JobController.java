package com.revhire.controller;

import com.revhire.dto.JobDto;
import com.revhire.entity.Job;
import com.revhire.security.UserDetailsImpl;
import com.revhire.service.JobService;
import com.revhire.service.SavedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private SavedJobService savedJobService;

    // Public endpoints
    @GetMapping("/search")
    public ResponseEntity<List<JobDto.JobResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Integer maxExperience,
            @AuthenticationPrincipal UserDetailsImpl user) {

        JobDto.JobSearchRequest request = new JobDto.JobSearchRequest();
        request.setTitle(title);
        request.setLocation(location);
        request.setCompanyName(companyName);
        request.setMaxExperience(maxExperience);
        if (jobType != null && !jobType.isEmpty()) {
            try { request.setJobType(Job.JobType.valueOf(jobType)); } catch (Exception ignored) {}
        }

        Long userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(jobService.searchJobs(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDto.JobResponse> getJobById(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserDetailsImpl user) {
        Long userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(jobService.getJobById(id, userId));
    }

    // Employer endpoints
    @PostMapping("/employer")
    public ResponseEntity<JobDto.JobResponse> createJob(@RequestBody JobDto.CreateJobRequest request,
                                                         @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(jobService.createJob(user.getId(), request));
    }

    @GetMapping("/employer/my-jobs")
    public ResponseEntity<List<JobDto.JobResponse>> getMyJobs(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(jobService.getEmployerJobs(user.getId()));
    }

    @PutMapping("/employer/{id}")
    public ResponseEntity<JobDto.JobResponse> updateJob(@PathVariable Long id,
                                                         @RequestBody JobDto.CreateJobRequest request,
                                                         @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(jobService.updateJob(user.getId(), id, request));
    }

    @PatchMapping("/employer/{id}/status")
    public ResponseEntity<Map<String, String>> updateJobStatus(@PathVariable Long id,
                                                                @RequestBody Map<String, String> body,
                                                                @AuthenticationPrincipal UserDetailsImpl user) {
        jobService.updateJobStatus(user.getId(), id, Job.JobStatus.valueOf(body.get("status")));
        return ResponseEntity.ok(Map.of("message", "Status updated"));
    }

    @DeleteMapping("/employer/{id}")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserDetailsImpl user) {
        jobService.deleteJob(user.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Job deleted"));
    }

    // Seeker endpoints
    @GetMapping("/seeker/saved")
    public ResponseEntity<List<JobDto.JobResponse>> getSavedJobs(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(savedJobService.getSavedJobs(user.getId()));
    }

    @PostMapping("/seeker/saved/{jobId}")
    public ResponseEntity<Map<String, String>> saveJob(@PathVariable Long jobId,
                                                        @AuthenticationPrincipal UserDetailsImpl user) {
        savedJobService.saveJob(user.getId(), jobId);
        return ResponseEntity.ok(Map.of("message", "Job saved"));
    }

    @DeleteMapping("/seeker/saved/{jobId}")
    public ResponseEntity<Map<String, String>> unsaveJob(@PathVariable Long jobId,
                                                          @AuthenticationPrincipal UserDetailsImpl user) {
        savedJobService.unsaveJob(user.getId(), jobId);
        return ResponseEntity.ok(Map.of("message", "Job removed from saved"));
    }
}
