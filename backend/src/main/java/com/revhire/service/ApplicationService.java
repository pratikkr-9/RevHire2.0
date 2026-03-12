package com.revhire.service;

import com.revhire.dto.ApplicationDto;
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
public class ApplicationService {

    private static final Logger logger = LogManager.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ApplicationDto.ApplicationResponse apply(Long userId, ApplicationDto.ApplyRequest request) {
        JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new AppException("Job not found"));

        if (job.getStatus() != Job.JobStatus.ACTIVE) {
            throw new AppException("This job is no longer accepting applications");
        }

        if (applicationRepository.existsByJobIdAndJobSeekerProfileId(job.getId(), profile.getId())) {
            throw new AppException("Already applied to this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setJobSeekerProfile(profile);
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(Application.ApplicationStatus.APPLIED);
        application = applicationRepository.save(application);

        logger.info("User {} applied to job {}", userId, job.getId());

        // Notify employer
        notificationService.createNotification(
                job.getCompany().getUser(),
                "New Application Received",
                profile.getUser().getName() + " applied for " + job.getTitle(),
                Notification.NotificationType.APPLICATION_STATUS,
                application.getId()
        );

        return mapToResponse(application);
    }

    @Transactional
    public void withdraw(Long userId, Long applicationId, String reason) {
        Application application = getApplicationForSeeker(userId, applicationId);
        if (application.getStatus() == Application.ApplicationStatus.WITHDRAWN) {
            throw new AppException("Application already withdrawn");
        }
        application.setStatus(Application.ApplicationStatus.WITHDRAWN);
        application.setWithdrawalReason(reason);
        applicationRepository.save(application);
        logger.info("User {} withdrew application {}", userId, applicationId);
    }

    public List<ApplicationDto.ApplicationResponse> getSeekerApplications(Long userId) {
        JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));
        return applicationRepository.findByJobSeekerProfileId(profile.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ApplicationDto.ApplicationResponse> getJobApplications(Long userId, Long jobId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException("Job not found"));
        if (!job.getCompany().getId().equals(company.getId())) {
            throw new AppException("Unauthorized");
        }
        return applicationRepository.findByJobId(jobId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDto.ApplicationResponse updateApplicationStatus(Long userId, Long applicationId,
                                                                       ApplicationDto.StatusUpdateRequest request) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company not found"));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException("Application not found"));

        if (!application.getJob().getCompany().getId().equals(company.getId())) {
            throw new AppException("Unauthorized");
        }

        application.setStatus(request.getStatus());
        if (request.getNote() != null) {
            application.setEmployerNote(request.getNote());
        }
        application = applicationRepository.save(application);

        // Notify job seeker
        String statusMsg = request.getStatus().name().replace("_", " ");
        notificationService.createNotification(
                application.getJobSeekerProfile().getUser(),
                "Application Status Updated",
                "Your application for " + application.getJob().getTitle() + " has been " + statusMsg,
                Notification.NotificationType.APPLICATION_STATUS,
                application.getId()
        );

        logger.info("Application {} status updated to {}", applicationId, request.getStatus());
        return mapToResponse(application);
    }

    private Application getApplicationForSeeker(Long userId, Long applicationId) {
        JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException("Application not found"));
        if (!application.getJobSeekerProfile().getId().equals(profile.getId())) {
            throw new AppException("Unauthorized");
        }
        return application;
    }

    public ApplicationDto.ApplicationResponse mapToResponse(Application app) {
        ApplicationDto.ApplicationResponse r = new ApplicationDto.ApplicationResponse();
        r.setId(app.getId());
        r.setCoverLetter(app.getCoverLetter());
        r.setStatus(app.getStatus());
        r.setEmployerNote(app.getEmployerNote());
        r.setAppliedAt(app.getAppliedAt());
        r.setUpdatedAt(app.getUpdatedAt());

        Job job = app.getJob();
        if (job != null) {
            r.setJobId(job.getId());
            r.setJobTitle(job.getTitle());
            if (job.getCompany() != null) {
                r.setCompanyName(job.getCompany().getName());
                r.setCompanyLocation(job.getCompany().getLocation());
            }
        }

        JobSeekerProfile profile = app.getJobSeekerProfile();
        if (profile != null) {
            r.setResumeFileName(profile.getResumeFileName());
            r.setTotalExperienceYears(profile.getTotalExperienceYears());
            r.setCurrentJobTitle(profile.getCurrentJobTitle());
            r.setSkills(profile.getSkills());
            r.setObjective(profile.getObjective());
            r.setEducation(profile.getEducation());
            r.setExperience(profile.getExperience());
            r.setCertifications(profile.getCertifications());
            r.setProjects(profile.getProjects());
            User user = profile.getUser();
            if (user != null) {
                r.setApplicantId(user.getId());
                r.setApplicantName(user.getName());
                r.setApplicantEmail(user.getEmail());
                r.setApplicantPhone(user.getPhone());
                r.setApplicantLocation(user.getLocation());
            }
        }
        return r;
    }
}
