package com.revhire.service;

import com.revhire.dto.CompanyDto;
import com.revhire.entity.Company;
import com.revhire.exception.AppException;
import com.revhire.repository.ApplicationRepository;
import com.revhire.repository.CompanyRepository;
import com.revhire.repository.JobRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private static final Logger logger = LogManager.getLogger(CompanyService.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public CompanyDto.CompanyResponse getCompany(Long userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company not found"));
        return mapToResponse(company);
    }

    @Transactional
    public CompanyDto.CompanyResponse updateCompany(Long userId, CompanyDto.UpdateCompanyRequest request) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company not found"));

        if (request.getName() != null) company.setName(request.getName());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getSize() != null) company.setSize(request.getSize());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getWebsite() != null) company.setWebsite(request.getWebsite());
        if (request.getLocation() != null) company.setLocation(request.getLocation());
        if (request.getFoundedYear() != null) company.setFoundedYear(request.getFoundedYear());

        company = companyRepository.save(company);
        logger.info("Company profile updated: {}", company.getName());
        return mapToResponse(company);
    }

    public CompanyDto.CompanyResponse getDashboard(Long userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Company not found"));
        return mapToResponse(company);
    }

    private CompanyDto.CompanyResponse mapToResponse(Company company) {
        CompanyDto.CompanyResponse r = new CompanyDto.CompanyResponse();
        r.setId(company.getId());
        r.setName(company.getName());
        r.setIndustry(company.getIndustry());
        r.setSize(company.getSize());
        r.setDescription(company.getDescription());
        r.setWebsite(company.getWebsite());
        r.setLocation(company.getLocation());
        r.setFoundedYear(company.getFoundedYear());
        r.setTotalJobs(jobRepository.countByCompanyId(company.getId()));
        r.setActiveJobs(jobRepository.countActiveByCompanyId(company.getId()));
        r.setTotalApplications(applicationRepository.countByCompanyId(company.getId()));
        r.setPendingApplications(applicationRepository.countPendingByCompanyId(company.getId()));
        return r;
    }
}
