# RevHire - Entity Relationship Diagram

## Database Schema

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          REVHIRE DATABASE SCHEMA                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────┐         ┌───────────────────────┐         ┌──────────────────────┐
│     USERS        │         │  JOB_SEEKER_PROFILES   │         │      COMPANIES        │
├──────────────────┤    1:1  ├───────────────────────┤  1:1    ├──────────────────────┤
│ PK id            │◄───────►│ PK id                  │◄───────►│ PK id                │
│ name             │         │ FK user_id              │         │ FK user_id           │
│ email (unique)   │         │ objective (TEXT)        │         │ name                 │
│ password (bcrypt)│         │ education (TEXT)        │         │ industry             │
│ phone            │         │ experience (TEXT)       │         │ size (enum)          │
│ location         │         │ skills (TEXT)           │         │ description (TEXT)   │
│ role (enum)      │         │ projects (TEXT)         │         │ website              │
│ employment_status│         │ certifications (TEXT)   │         │ location             │
│ active           │         │ total_exp_years         │         │ logo_url             │
│ created_at       │         │ current_job_title       │         │ founded_year         │
│ updated_at       │         │ linkedin_url            │         │ created_at           │
└──────────────────┘         │ portfolio_url           │         │ updated_at           │
         │                   │ resume_file_name        │         └──────────────────────┘
         │                   │ resume_file_path        │                    │
         │                   │ resume_content_type     │                    │ 1:N
         │                   └───────────────────────┘                    │
         │                             │                                    ▼
         │ 1:N                         │ 1:N               ┌──────────────────────────┐
         ▼                             │                   │          JOBS             │
┌────────────────────┐                 │                   ├──────────────────────────┤
│   NOTIFICATIONS    │                 │                   │ PK id                    │
├────────────────────┤                 │                   │ FK company_id            │
│ PK id              │                 │                   │ title                    │
│ FK user_id         │                 │                   │ description (TEXT)       │
│ title              │                 │                   │ required_skills (TEXT)   │
│ message            │                 │                   │ required_education       │
│ type (enum)        │                 │                   │ min_experience_years     │
│ read               │                 │                   │ max_experience_years     │
│ reference_id       │                 │                   │ location                 │
│ created_at         │                 │                   │ is_remote                │
└────────────────────┘                 │                   │ min_salary (decimal)     │
                                       │                   │ max_salary (decimal)     │
                                       │                   │ job_type (enum)          │
                        ┌──────────────────────────┐       │ status (enum)            │
                        │       APPLICATIONS        │       │ application_deadline     │
                        ├──────────────────────────┤       │ number_of_openings       │
                        │ PK id                    │       │ posted_at                │
                        │ FK job_id                │◄──────│ updated_at               │
                        │ FK job_seeker_profile_id │N:1    └──────────────────────────┘
                        │ cover_letter (TEXT)      │                    │
                        │ status (enum)            │◄───────────────────┘
                        │ employer_note (TEXT)     │    1:N (job has many applications)
                        │ withdrawal_reason (TEXT) │
                        │ applied_at               │
                        │ updated_at               │
                        └──────────────────────────┘

┌──────────────────────────────────────────┐
│              SAVED_JOBS                   │
├──────────────────────────────────────────┤
│ PK id                                    │
│ FK job_seeker_profile_id                 │
│ FK job_id                                │
│ saved_at                                 │
│ UNIQUE(job_seeker_profile_id, job_id)   │
└──────────────────────────────────────────┘
```

## Entity Relationships

| Relationship | Type | Description |
|-------------|------|-------------|
| User → JobSeekerProfile | One-to-One | Each job seeker has one profile |
| User → Company | One-to-One | Each employer manages one company |
| User → Notification | One-to-Many | Users receive multiple notifications |
| Company → Job | One-to-Many | Companies post multiple jobs |
| Job → Application | One-to-Many | Each job has multiple applications |
| JobSeekerProfile → Application | One-to-Many | Seekers submit multiple applications |
| JobSeekerProfile → SavedJob | One-to-Many | Seekers save multiple jobs |

## Enumerations

### User.UserRole
- `JOB_SEEKER` - Candidate looking for jobs
- `EMPLOYER` - Company representative

### User.EmploymentStatus
- `EMPLOYED`, `UNEMPLOYED`, `STUDENT`, `FREELANCER`

### Job.JobType
- `FULL_TIME`, `PART_TIME`, `CONTRACT`, `FREELANCE`, `INTERNSHIP`

### Job.JobStatus
- `ACTIVE` - Accepting applications
- `CLOSED` - Not accepting applications
- `FILLED` - Position has been filled
- `DRAFT` - Not yet published

### Application.ApplicationStatus
- `APPLIED` → `UNDER_REVIEW` → `SHORTLISTED` or `REJECTED`
- `WITHDRAWN` - Candidate withdrew

### Company.CompanySize
- `STARTUP` (1-10), `SMALL` (11-50), `MEDIUM` (51-200), `LARGE` (201-1000), `ENTERPRISE` (1000+)

### Notification.NotificationType
- `APPLICATION_STATUS`, `JOB_RECOMMENDATION`, `GENERAL`

## Indexes & Constraints

- `users.email` - UNIQUE constraint
- `applications(job_id, job_seeker_profile_id)` - UNIQUE constraint (prevents duplicate applications)
- `saved_jobs(job_seeker_profile_id, job_id)` - UNIQUE constraint
