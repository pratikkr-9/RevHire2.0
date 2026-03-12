package com.revhire.service;

import com.revhire.dto.ProfileDto;
import com.revhire.entity.JobSeekerProfile;
import com.revhire.entity.User;
import com.revhire.exception.AppException;
import com.revhire.repository.JobSeekerProfileRepository;
import com.revhire.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileService {

    private static final Logger logger = LogManager.getLogger(ProfileService.class);

    @Autowired
    private JobSeekerProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ProfileDto.ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found"));
        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));
        return mapToResponse(user, profile);
    }

    @Transactional
    public ProfileDto.ProfileResponse updateProfile(Long userId, ProfileDto.UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found"));
        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        userRepository.save(user);

        if (request.getObjective() != null) profile.setObjective(request.getObjective());
        if (request.getEducation() != null) profile.setEducation(request.getEducation());
        if (request.getExperience() != null) profile.setExperience(request.getExperience());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getProjects() != null) profile.setProjects(request.getProjects());
        if (request.getCertifications() != null) profile.setCertifications(request.getCertifications());
        if (request.getTotalExperienceYears() != null) profile.setTotalExperienceYears(request.getTotalExperienceYears());
        if (request.getCurrentJobTitle() != null) profile.setCurrentJobTitle(request.getCurrentJobTitle());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        profileRepository.save(profile);

        logger.info("Profile updated for user: {}", userId);
        return mapToResponse(user, profile);
    }

    @Transactional
    public String uploadResume(Long userId, MultipartFile file) throws IOException {
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new AppException("File size must not exceed 2MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.contains("pdf") && !contentType.contains("word") && !contentType.contains("document"))) {
            throw new AppException("Only PDF and DOCX files are allowed");
        }

        JobSeekerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Profile not found"));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // Delete old resume if exists
        if (profile.getResumeFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(profile.getResumeFilePath()));
            } catch (Exception e) {
                logger.warn("Could not delete old resume: {}", e.getMessage());
            }
        }

        profile.setResumeFileName(file.getOriginalFilename());
        profile.setResumeFilePath(filePath.toString());
        profile.setResumeContentType(contentType);
        profileRepository.save(profile);

        logger.info("Resume uploaded for user: {}", userId);
        return filename;
    }

    private ProfileDto.ProfileResponse mapToResponse(User user, JobSeekerProfile profile) {
        ProfileDto.ProfileResponse r = new ProfileDto.ProfileResponse();
        r.setUserId(user.getId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setLocation(user.getLocation());
        r.setEmploymentStatus(user.getEmploymentStatus() != null ? user.getEmploymentStatus().name() : null);
        r.setProfileId(profile.getId());
        r.setObjective(profile.getObjective());
        r.setEducation(profile.getEducation());
        r.setExperience(profile.getExperience());
        r.setSkills(profile.getSkills());
        r.setProjects(profile.getProjects());
        r.setCertifications(profile.getCertifications());
        r.setTotalExperienceYears(profile.getTotalExperienceYears());
        r.setCurrentJobTitle(profile.getCurrentJobTitle());
        r.setLinkedinUrl(profile.getLinkedinUrl());
        r.setPortfolioUrl(profile.getPortfolioUrl());
        r.setResumeFileName(profile.getResumeFileName());
        return r;
    }
}
