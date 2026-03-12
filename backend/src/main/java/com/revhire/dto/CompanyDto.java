package com.revhire.dto;

import com.revhire.entity.Company;
import com.revhire.entity.Notification;
import lombok.Data;
import java.time.LocalDateTime;

public class CompanyDto {

    @Data
    public static class UpdateCompanyRequest {
        private String name;
        private String industry;
        private Company.CompanySize size;
        private String description;
        private String website;
        private String location;
        private Integer foundedYear;
    }

    @Data
    public static class CompanyResponse {
        private Long id;
        private String name;
        private String industry;
        private Company.CompanySize size;
        private String description;
        private String website;
        private String location;
        private Integer foundedYear;
        private long totalJobs;
        private long activeJobs;
        private long totalApplications;
        private long pendingApplications;
    }
}
