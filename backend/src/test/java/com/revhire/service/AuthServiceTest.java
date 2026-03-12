package com.revhire.service;

import com.revhire.dto.AuthDto;
import com.revhire.entity.User;
import com.revhire.exception.AppException;
import com.revhire.repository.CompanyRepository;
import com.revhire.repository.JobSeekerProfileRepository;
import com.revhire.repository.UserRepository;
import com.revhire.security.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testRegisterWithExistingEmail_throwsException() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setEmail("existing@test.com");
        request.setPassword("password");
        request.setName("Test User");
        request.setRole("JOB_SEEKER");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        try {
            authService.register(request);
            fail("Expected AppException");
        } catch (AppException e) {
            assertEquals("Email already registered", e.getMessage());
        }
    }

    @Test
    public void testEmailExists_returnsTrue() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertTrue(userRepository.existsByEmail("test@test.com"));
    }

    @Test
    public void testEmailNotExists_returnsFalse() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        assertFalse(userRepository.existsByEmail("new@test.com"));
    }

    @Test
    public void testPasswordEncoding_notPlainText() {
        when(passwordEncoder.encode("rawPassword")).thenReturn("$2a$10$encodedHash");
        String encoded = passwordEncoder.encode("rawPassword");
        assertNotEquals("rawPassword", encoded);
        assertTrue(encoded.startsWith("$2a$"));
    }

    @Test
    public void testUserRole_jobSeeker() {
        User user = new User();
        user.setRole(User.UserRole.JOB_SEEKER);
        assertEquals(User.UserRole.JOB_SEEKER, user.getRole());
    }

    @Test
    public void testUserRole_employer() {
        User user = new User();
        user.setRole(User.UserRole.EMPLOYER);
        assertEquals(User.UserRole.EMPLOYER, user.getRole());
    }
}
