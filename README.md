# RevHire - Full Stack Job Portal Application

A complete, production-ready monolithic job portal connecting job seekers with employers.

---

##  Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Angular 17, TypeScript, CSS3 |
| Backend | Java 17, Spring Boot 3.2 |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8.0 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Build | Maven 3.x |
| Logging | Log4j2 |
| Testing | JUnit 4, Mockito |
| IDE | Eclipse / VS Code |

---

##  Prerequisites

- Java 17+
- Node.js 18+ and npm
- MySQL 8.0+
- Maven 3.8+
- Angular CLI 17+ (`npm install -g @angular/cli`)

---

##  Database Setup

```sql
CREATE DATABASE revhire_db;
CREATE USER 'revhire_user'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON revhire_db.* TO 'revhire_user'@'localhost';
FLUSH PRIVILEGES;
```

Or using default root credentials (as configured in application.properties):
```sql
CREATE DATABASE revhire_db;
```

---

##  Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Configure database in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/revhire2_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

3. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080**

---

##  Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
ng serve
```

Frontend starts at: **http://localhost:4200**


---

##  Project Structure

```
revhire/
├── backend/                          # Spring Boot Application
│   ├── src/main/java/com/revhire/
│   │   ├── config/                   # Security & CORS configuration
│   │   ├── controller/               # REST API endpoints
│   │   │   ├── AuthController        # Registration & Login
│   │   │   ├── JobController         # Job CRUD operations
│   │   │   ├── ApplicationController # Application management
│   │   │   ├── ProfileController     # Job seeker profile
│   │   │   ├── CompanyController     # Employer company profile
│   │   │   └── NotificationController
│   │   ├── service/                  # Business logic layer
│   │   ├── repository/               # Data access layer (JPA)
│   │   ├── entity/                   # JPA entities / DB tables
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── security/                 # JWT utilities
│   │   └── exception/                # Global exception handling
│   ├── src/test/                     # JUnit 4 unit tests
│   └── pom.xml
│
├── frontend/                         # Angular Application
│   ├── src/app/
│   │   ├── components/
│   │   │   ├── auth/                 # Login, Register
│   │   │   ├── shared/               # Navbar, Job List, Job Detail
│   │   │   ├── job-seeker/           # Dashboard, Profile, Applications, Saved Jobs
│   │   │   └── employer/             # Dashboard, Post Job, Manage Jobs, Applicants, Company
│   │   ├── services/                 # HTTP services (API, Auth)
│   │   ├── models/                   # TypeScript interfaces
│   │   ├── guards/                   # Route guards (auth, role)
│   │   └── interceptors/             # HTTP auth interceptor
│   ├── src/styles.css                # Global styles
│   └── package.json
│
├── docs/
│   ├── ERD.md                        # Entity Relationship Diagram
│   └── ARCHITECTURE.md               # System Architecture
└── README.md
```

---

##  API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login & get JWT |

### Jobs
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/jobs/search | Public |
| GET | /api/jobs/{id} | Public |
| POST | /api/jobs/employer | Employer |
| PUT | /api/jobs/employer/{id} | Employer |
| PATCH | /api/jobs/employer/{id}/status | Employer |
| DELETE | /api/jobs/employer/{id} | Employer |
| GET | /api/jobs/employer/my-jobs | Employer |
| GET | /api/jobs/seeker/saved | Job Seeker |
| POST | /api/jobs/seeker/saved/{id} | Job Seeker |
| DELETE | /api/jobs/seeker/saved/{id} | Job Seeker |

### Applications
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/applications/seeker/apply | Job Seeker |
| GET | /api/applications/seeker/my-applications | Job Seeker |
| PATCH | /api/applications/seeker/{id}/withdraw | Job Seeker |
| GET | /api/applications/employer/job/{id} | Employer |
| PATCH | /api/applications/employer/{id}/status | Employer |

### Profile & Company
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/profile | Job Seeker |
| PUT | /api/profile | Job Seeker |
| POST | /api/profile/resume | Job Seeker |
| GET | /api/company | Employer |
| PUT | /api/company | Employer |

---

##  Key Features

### Job Seeker
- ✅ Register & login with JWT authentication
- ✅ Create and manage professional profile
- ✅ Upload resume (PDF/DOCX, max 2MB)
- ✅ Search jobs with advanced filters
- ✅ One-click apply with cover letter
- ✅ Track applications with real-time status
- ✅ Withdraw applications with reason
- ✅ Save favourite jobs
- ✅ In-app notifications

### Employer
- ✅ Company registration & profile management
- ✅ Create job postings with comprehensive details
- ✅ Manage job status (Active/Closed/Filled)
- ✅ View all applicants with detailed profiles
- ✅ Shortlist or reject applications
- ✅ Add internal notes to applications
- ✅ Dashboard with statistics
- ✅ Filter applicants by status

---

##  Security Features

- JWT-based stateless authentication
- BCrypt password hashing
- Role-based access control (EMPLOYER/JOB_SEEKER)
- Spring Security filter chain
- CORS configuration for frontend

---

##  Running Tests

```bash
cd backend
mvn test
```

Tests cover:
- AuthService (registration, login, validation)
- JobService (CRUD, search, authorization)
