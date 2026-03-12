# RevHire - Application Architecture

## Overview

RevHire is a **monolithic full-stack web application** built using a layered architecture pattern.

```
┌─────────────────────────────────────────────────────────────────┐
│                     REVHIRE ARCHITECTURE                         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     FRONTEND (Angular 17)                        │
│                    http://localhost:4200                          │
│                                                                   │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐  ┌──────────────┐   │
│  │  Auth    │  │  Shared  │  │ Job Seeker│  │   Employer   │   │
│  │ Pages    │  │ Comps    │  │  Pages    │  │   Pages      │   │
│  │          │  │          │  │           │  │              │   │
│  │ Login    │  │ Navbar   │  │ Dashboard │  │ Dashboard    │   │
│  │ Register │  │ JobList  │  │ Profile   │  │ Post Job     │   │
│  │          │  │ JobDetail│  │ Apps      │  │ Manage Jobs  │   │
│  │          │  │          │  │ Saved Jobs│  │ Applicants   │   │
│  │          │  │          │  │           │  │ Company Mgmt │   │
│  └──────────┘  └──────────┘  └───────────┘  └──────────────┘   │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │ Angular Services: AuthService, JobService, ApplnService, │    │
│  │ ProfileService, CompanyService, NotificationService      │    │
│  └──────────────────────────────────────────────────────────┘    │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │  AuthGuard (Route Protection) + JWT Interceptor          │    │
│  └──────────────────────────────────────────────────────────┘    │
└───────────────────────────────┬─────────────────────────────────┘
                                │ HTTP REST API Calls
                                │ (with JWT Bearer Token)
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     BACKEND (Spring Boot 3.2)                    │
│                    http://localhost:8080                          │
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │  SPRING SECURITY FILTER CHAIN                           │     │
│  │  JwtAuthFilter → UsernamePasswordAuthFilter             │     │
│  │  CORS Configuration (allow localhost:4200)              │     │
│  └───────────────────────┬────────────────────────────────┘     │
│                           │                                       │
│  ┌────────────────────────▼────────────────────────────────┐    │
│  │  CONTROLLER LAYER (REST API)                             │    │
│  │  ┌──────────┐ ┌──────────┐ ┌────────────┐ ┌─────────┐  │    │
│  │  │   Auth   │ │   Job    │ │Application │ │Profile  │  │    │
│  │  │Controller│ │Controller│ │ Controller │ │Company  │  │    │
│  │  └────┬─────┘ └─────┬────┘ └─────┬──────┘ │Notif   │  │    │
│  │       │              │             │         └─────────┘  │    │
│  └───────┼──────────────┼─────────────┼─────────────────────┘    │
│           │              │             │                           │
│  ┌────────▼──────────────▼─────────────▼──────────────────┐     │
│  │  SERVICE LAYER (Business Logic)                          │     │
│  │  AuthService | JobService | ApplicationService           │     │
│  │  ProfileService | CompanyService | NotificationService   │     │
│  │  SavedJobService                                         │     │
│  └───────────────────────────┬──────────────────────────────┘    │
│                               │                                    │
│  ┌────────────────────────────▼────────────────────────────┐     │
│  │  REPOSITORY LAYER (Spring Data JPA)                      │     │
│  │  UserRepo | JobRepo | ApplicationRepo | ProfileRepo       │     │
│  │  CompanyRepo | NotificationRepo | SavedJobRepo            │     │
│  └───────────────────────────┬──────────────────────────────┘    │
│                               │                                    │
│  ┌────────────────────────────▼────────────────────────────┐     │
│  │  ENTITY LAYER (JPA/Hibernate)                            │     │
│  │  User | Job | Application | JobSeekerProfile             │     │
│  │  Company | Notification | SavedJob                       │     │
│  └───────────────────────────┬──────────────────────────────┘    │
└───────────────────────────────┼─────────────────────────────────┘
                                │ JPA / JDBC
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     MySQL Database                               │
│                    revhire_db                                     │
│                                                                   │
│  users | companies | job_seeker_profiles | jobs                  │
│  applications | saved_jobs | notifications                       │
└─────────────────────────────────────────────────────────────────┘
```

## Three-Layer Architecture

### 1. Controller Layer (Presentation)
- Handles HTTP requests and responses
- Input validation using Bean Validation
- Delegates business logic to services
- Returns structured DTOs

### 2. Service Layer (Business Logic)
- Implements all business rules
- Transaction management (`@Transactional`)
- Authentication and authorization checks
- Sends notifications on state changes

### 3. Repository Layer (Data Access)
- Spring Data JPA repositories
- Custom JPQL queries for complex searches
- Entity relationships managed by Hibernate

## Security Architecture

```
Request → JwtAuthFilter → extract & validate JWT
       → SecurityContextHolder (stores UserDetailsImpl)
       → Controller (reads @AuthenticationPrincipal)
       → Service (uses userId for data scoping)
```

- **Authentication**: JWT tokens with 24h expiry
- **Password Storage**: BCrypt with default strength 10
- **Authorization**: Role-based (`ROLE_JOB_SEEKER`, `ROLE_EMPLOYER`)

## Data Flow Example: Job Application

```
1. Job Seeker clicks "Apply"
2. Angular: ApplicationService.apply() sends POST /api/applications/seeker/apply
3. JWT Interceptor adds Authorization header
4. Backend: JwtAuthFilter validates token
5. ApplicationController receives request
6. ApplicationService:
   a. Checks job exists and is ACTIVE
   b. Checks seeker hasn't already applied
   c. Creates Application entity (status=APPLIED)
   d. Saves to database
   e. Creates notification for employer
7. Returns ApplicationResponse DTO
8. Angular updates UI with success message
```
