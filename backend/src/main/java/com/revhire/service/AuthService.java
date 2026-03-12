package com.revhire.service;

import com.revhire.dto.AuthDto;
import com.revhire.entity.Company;
import com.revhire.entity.JobSeekerProfile;
import com.revhire.entity.User;
import com.revhire.exception.AppException;
import com.revhire.repository.CompanyRepository;
import com.revhire.repository.JobSeekerProfileRepository;
import com.revhire.repository.UserRepository;
import com.revhire.security.JwtUtils;
import com.revhire.security.UserDetailsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        logger.info("Registering new user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setLocation(request.getLocation());
        user.setRole(User.UserRole.valueOf(request.getRole()));
        user.setEmploymentStatus(request.getEmploymentStatus());
        user.setActive(true);

        user = userRepository.save(user);

        if (user.getRole() == User.UserRole.JOB_SEEKER) {
            JobSeekerProfile profile = new JobSeekerProfile();
            profile.setUser(user);
            jobSeekerProfileRepository.save(profile);
            logger.info("Job seeker profile created for: {}", user.getEmail());
        } else {
            Company company = new Company();
            company.setUser(user);
            company.setName(request.getCompanyName() != null ? request.getCompanyName() : user.getName() + "'s Company");
            company.setIndustry(request.getIndustry());
            company.setDescription(request.getCompanyDescription());
            company.setWebsite(request.getWebsite());
            company.setLocation(request.getLocation());
            if (request.getCompanySize() != null) {
                try {
                    company.setSize(Company.CompanySize.valueOf(request.getCompanySize()));
                } catch (Exception e) {
                    company.setSize(Company.CompanySize.SMALL);
                }
            }
            companyRepository.save(company);
            logger.info("Company profile created for: {}", user.getEmail());
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtUtils.generateToken(auth);

        return new AuthDto.AuthResponse(token, user.getRole().name(), user.getId(), user.getName(), user.getEmail());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        logger.info("Login attempt: {}", request.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        String token = jwtUtils.generateToken(auth);

        logger.info("Login successful: {}", request.getEmail());
        return new AuthDto.AuthResponse(token, userDetails.getRole().name(),
                userDetails.getId(), userDetails.getName(), userDetails.getUsername());
    }
}
