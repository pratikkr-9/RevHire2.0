package com.revhire.service;

import com.revhire.dto.JobDto;
import com.revhire.entity.Company;
import com.revhire.entity.Job;
import com.revhire.exception.AppException;
import com.revhire.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private SavedJobRepository savedJobRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    public void testGetJobById_notFound_throwsException() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());
        try {
            jobService.getJobById(999L, null);
            fail("Expected AppException");
        } catch (AppException e) {
            assertEquals("Job not found", e.getMessage());
        }
    }

    @Test
    public void testGetEmployerJobs_companyNotFound_throwsException() {
        when(companyRepository.findByUserId(1L)).thenReturn(Optional.empty());
        try {
            jobService.getEmployerJobs(1L);
            fail("Expected AppException");
        } catch (AppException e) {
            assertEquals("Company not found", e.getMessage());
        }
    }

    @Test
    public void testMapJobToResponse_setsCorrectFields() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Test Corp");
        company.setLocation("Mumbai");

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Software Engineer");
        job.setDescription("Great job");
        job.setJobType(Job.JobType.FULL_TIME);
        job.setStatus(Job.JobStatus.ACTIVE);
        job.setCompany(company);

        JobDto.JobResponse response = jobService.mapJobToResponse(job, company, false, false);

        assertEquals("Software Engineer", response.getTitle());
        assertEquals("Test Corp", response.getCompanyName());
        assertFalse(response.isAlreadyApplied());
        assertFalse(response.isSaved());
    }

    @Test
    public void testDeleteJob_companyNotFound_throwsException() {
        when(companyRepository.findByUserId(1L)).thenReturn(Optional.empty());
        try {
            jobService.deleteJob(1L, 1L);
            fail("Expected AppException");
        } catch (AppException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testJobType_values() {
        assertNotNull(Job.JobType.FULL_TIME);
        assertNotNull(Job.JobType.PART_TIME);
        assertNotNull(Job.JobType.CONTRACT);
        assertNotNull(Job.JobType.INTERNSHIP);
    }

    @Test
    public void testJobStatus_values() {
        assertNotNull(Job.JobStatus.ACTIVE);
        assertNotNull(Job.JobStatus.CLOSED);
        assertNotNull(Job.JobStatus.FILLED);
    }
}
