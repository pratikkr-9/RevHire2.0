-- RevHire Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS revhire_db;
USE revhire_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    location VARCHAR(255),
    role ENUM('JOB_SEEKER', 'EMPLOYER') NOT NULL,
    employment_status ENUM('EMPLOYED', 'UNEMPLOYED', 'STUDENT', 'FREELANCER'),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

-- Companies table
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(255),
    size ENUM('STARTUP', 'SMALL', 'MEDIUM', 'LARGE', 'ENTERPRISE'),
    description TEXT,
    website VARCHAR(500),
    location VARCHAR(255),
    logo_url VARCHAR(500),
    founded_year INT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Job Seeker Profiles table
CREATE TABLE IF NOT EXISTS job_seeker_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    objective TEXT,
    education TEXT,
    experience TEXT,
    skills TEXT,
    projects TEXT,
    certifications TEXT,
    total_experience_years INT DEFAULT 0,
    current_job_title VARCHAR(255),
    linkedin_url VARCHAR(500),
    portfolio_url VARCHAR(500),
    resume_file_name VARCHAR(255),
    resume_file_path VARCHAR(500),
    resume_content_type VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Jobs table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    required_skills TEXT,
    required_education VARCHAR(255),
    min_experience_years INT,
    max_experience_years INT,
    location VARCHAR(255),
    is_remote BOOLEAN DEFAULT FALSE,
    min_salary DECIMAL(12, 2),
    max_salary DECIMAL(12, 2),
    job_type ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'FREELANCE', 'INTERNSHIP'),
    status ENUM('ACTIVE', 'CLOSED', 'FILLED', 'DRAFT') DEFAULT 'ACTIVE',
    application_deadline DATE,
    number_of_openings INT,
    posted_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- Applications table
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    job_seeker_profile_id BIGINT NOT NULL,
    cover_letter TEXT,
    status ENUM('APPLIED', 'UNDER_REVIEW', 'SHORTLISTED', 'REJECTED', 'WITHDRAWN') DEFAULT 'APPLIED',
    employer_note TEXT,
    withdrawal_reason TEXT,
    applied_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (job_seeker_profile_id) REFERENCES job_seeker_profiles(id),
    UNIQUE KEY unique_application (job_id, job_seeker_profile_id)
);

-- Saved Jobs table
CREATE TABLE IF NOT EXISTS saved_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_seeker_profile_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    saved_at DATETIME,
    FOREIGN KEY (job_seeker_profile_id) REFERENCES job_seeker_profiles(id),
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    UNIQUE KEY unique_saved (job_seeker_profile_id, job_id)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    message TEXT,
    type ENUM('APPLICATION_STATUS', 'JOB_RECOMMENDATION', 'GENERAL'),
    is_read BOOLEAN DEFAULT FALSE,
    reference_id BIGINT,
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
