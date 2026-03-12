package com.revhire.dto;

import com.revhire.entity.Application;
import lombok.Data;
import java.time.LocalDateTime;

public class ApplicationDto {

    @Data
    public static class ApplyRequest {
        private Long jobId;
        private String coverLetter;
    }

    @Data
    public static class WithdrawRequest {
        private String reason;
    }

    @Data
    public static class StatusUpdateRequest {
        private Application.ApplicationStatus status;
        private String note;
    }

    @Data
    public static class ApplicationResponse {
        private Long id;
        private Long jobId;
        private String jobTitle;
        private String companyName;
        private String companyLocation;
        private String coverLetter;
        private Application.ApplicationStatus status;
        private String employerNote;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;
        // Applicant details (for employers)
        private Long applicantId;
        private String applicantName;
        private String applicantEmail;
        private String applicantPhone;
        private String applicantLocation;
        private String resumeFileName;
        private Integer totalExperienceYears;
        private String currentJobTitle;
        private String skills;
        private String objective;
        private String education;
        private String experience;
        private String certifications;
        private String projects;
    }
}
