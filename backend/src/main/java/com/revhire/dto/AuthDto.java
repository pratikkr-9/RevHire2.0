package com.revhire.dto;

import com.revhire.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String name;
        @Email
        @NotBlank
        private String email;
        @NotBlank
        @Size(min = 6, max = 100)
        private String password;
        private String phone;
        private String location;
        @NotBlank
        private String role; // JOB_SEEKER or EMPLOYER
        private User.EmploymentStatus employmentStatus;
        // Employer fields
        private String companyName;
        private String industry;
        private String companySize;
        private String companyDescription;
        private String website;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String role;
        private Long userId;
        private String name;
        private String email;

        public AuthResponse(String token, String role, Long userId, String name, String email) {
            this.token = token;
            this.role = role;
            this.userId = userId;
            this.name = name;
            this.email = email;
        }
    }
}
