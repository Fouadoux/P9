# 📊 GlucoVision – Microservices-Based Diabetes Risk Platform

**GlucoVision** is a Java & Angular-based web application that evaluates diabetes risk levels, manages patient data, and stores medical notes using a robust microservices architecture.

---
## 🧰 Technologies Used

GlucoVision is built with a modern and modular tech stack designed for scalability, clarity, and developer productivity:

### 🔙 Backend
- **Java 21 + Spring Boot 3** – Robust microservice foundation
- **Spring Security** – JWT-based authentication and role-based access control
- **Spring Cloud Gateway** – Central API routing and token validation
- **Spring Cloud OpenFeign** – Declarative REST clients for inter-service calls
- **MySQL** – Relational database for user and patient data
- **MongoDB** – NoSQL storage for flexible medical notes
- **Docker & Docker Compose** – Containerized infrastructure
- **JUnit 5, Mockito** – Full unit and integration testing support

### 🔛 Inter-service Communication
- **Feign Clients** secured with **internal JWT tokens**
- **Environment variables** for all sensitive configs
- **Custom Role Converters** to extract roles from JWT payloads

### 🔜 Frontend
- **Angular 16+** – Standalone components & Signal-based reactivity
- **Angular Material** – Responsive and accessible UI components
- **RxJS** – Reactive streams and async handling
- **JWT Auth with localStorage** – Seamless secure user sessions
- **RESTful communication** via the Spring Gateway

This combination allows for clean separation of concerns, scalability, and smooth communication between all parts of the system.

---
## ▶️ Running the Application

Nothing easier! To launch the full GlucoVision stack locally:

```bash
git clone https://github.com/Fouadoux/P9
cd glucovision
docker-compose up --build
```

> ℹ️ Make sure Docker and Docker Compose are installed on your system.
---

## 🏃‍♂️ Running Application

Once all services are running, you can access the application via:

- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **Backend API Gateway**: [http://localhost:8080](http://localhost:8080) *(API only – no UI here)*

### 🧪 Test Data

To support pagination and testing features, the database is pre-populated with:

- **15 users**
- **25 patients**

However, only **test patients** are linked to:

- **notes (medical reports)**
- and a **calculated diabetes risk assessment**

The remaining patients are for pagination purposes only and have no associated medical data.

### 🔐 Test Accounts

To explore the application quickly, you can log in using one of the predefined test accounts.  
Each of these accounts uses the same password: **`password123`**

| Email                   | Role / Status       |
|-------------------------|---------------------|
| `admin@example.com`     | ADMIN               |
| `user@example.com`      | USER                |
| `pending@example.com`   | PENDING account     |
| `notactive@example.com` | Not activated       |

> ⚠️ All other accounts are **dummy user accounts** created for pagination testing and do **not** have access to the application's features.

### 📝 Registering a New Account

You can also register a new account via the frontend.  
⚠️ However, newly created accounts start with the `PENDING` role and **cannot access the application** until an `ADMIN` user changes their role using the admin panel.

---
## 🧱 Microservices Overview

This project is structured around five key microservices. Each section below includes a description, available endpoints, and handled errors.

---

### ✨ `auth-service`

**Handles user registration, login, JWT issuance, and internal authentication tokens.**

🗄️ **Database**: Uses a relational database (e.g., MySQL) to store user credentials and roles.

#### 📖 Endpoints

| Method | Path                            | Description                     | Access       |
| ------ | ------------------------------- | ------------------------------- | ------------ |
| POST   | `/api/auth/login`               | User login and JWT generation   | Public       |
| POST   | `/api/auth/register`            | Register a new user             | Public       |
| POST   | `/internal-auth/internal-token` | Get token for internal services | API Key only |

#### ❌ Handled Errors

| Exception                         | HTTP Status | Message                                        |
| --------------------------------- | ----------- | ---------------------------------------------- |
| `BadCredentialsException`         | 401         | Invalid email or password                      |
| `DisabledAccountException`        | 403         | Account is disabled                            |
| `MethodArgumentNotValidException` | 400         | Input validation errors (field-level messages) |

---

### 👩‍🎓 `patient-service`

**Manages personal data of patients (name, birthdate, gender, etc.) and their active/inactive status.**

🗄️ **Database**: Stores patient records in a relational database (e.g., MySQL).

#### 📖 Endpoints

| Method | Path                            | Description                        | Required Role                  |
| ------ | ------------------------------- | ---------------------------------- | ------------------------------ |
| POST   | `/api/patients`                 | Create a new patient               | ROLE\_USER, ROLE\_ADMIN        |
| GET    | `/api/patients/{uid}`           | Get patient by UID                 | USER, ADMIN, INTERNAL\_SERVICE |
| GET    | `/api/patients`                 | List all patients                  | ROLE\_USER, ROLE\_ADMIN        |
| GET    | `/api/patients/active`          | List only active patients          | ROLE\_USER, ROLE\_ADMIN        |
| GET    | `/api/patients/name/{lastName}` | Search by last name                | ROLE\_USER, ROLE\_ADMIN        |
| PUT    | `/api/patients/{id}`            | Update patient                     | ROLE\_USER, ROLE\_ADMIN        |
| PUT    | `/api/patients/toggle/{id}`     | Activate/deactivate patient        | ROLE\_ADMIN                    |
| GET    | `/api/patients/{uid}/exists`    | Check if patient exists (internal) | Public/Internal                |
| DELETE | `/api/patients/{id}`            | Delete patient                     | ROLE\_ADMIN                    |

#### ❌ Handled Errors

| Exception                   | HTTP Status | Message                                   |
| --------------------------- | ----------- | ----------------------------------------- |
| `PatientNotFoundException`  | 404         | Patient with given ID does not exist      |
| `DuplicatePatientException` | 409         | Patient with same name/DOB already exists |

---

### 📝 `note-service`

**Handles creation and retrieval of medical notes linked to patients. Used by the risk service.**

🗄️ **Database**: Uses MongoDB to persist unstructured medical notes.

#### 🔗 Feign Clients

These endpoints are consumed internally by this service:

| Method | Path                            | Target Service    | Description                               |
| ------ | ------------------------------- | ----------------- | ----------------------------------------- |
| GET    | `/api/patients/{id}/exists`     | `patient-service` | Check if a patient exists and is active   |
| POST   | `/internal-auth/internal-token` | `auth-service`    | Get internal JWT for secure communication |

#### 📖 Endpoints

| Method | Path                      | Description                 | Required Role           |
| ------ | ------------------------- | --------------------------- | ----------------------- |
| POST   | `/api/notes`              | Create a new note           | ROLE\_USER, ROLE\_ADMIN |
| GET    | `/api/notes/patient/{id}` | Get all notes for a patient | ROLE\_USER, ROLE\_ADMIN |
| PUT    | `/api/notes`              | Update note (if < 24h old)  | ROLE\_USER, ROLE\_ADMIN |
| DELETE | `/api/notes`              | Delete note (if < 24h old)  | ROLE\_USER, ROLE\_ADMIN |
| PUT    | `/api/notes/admin/update` | Admin update (any note)     | ROLE\_ADMIN             |
| DELETE | `/api/notes/admin/delete` | Admin delete (any note)     | ROLE\_ADMIN             |

#### ❌ Handled Errors

| Exception                      | HTTP Status | Message                                     |
| ------------------------------ | ----------- | ------------------------------------------- |
| `NoteNotFoundException`        | 404         | Note not found                              |
| `UnauthorizedNoteModification` | 403         | Note is older than 24h and cannot be edited |

---

### 🧠 `diabetes-risk-service`

**Computes diabetes risk based on patient age, gender, and medical note content.**

#### 🔗 Feign Clients

These endpoints are consumed internally by this service:

| Method | Path                            | Target Service    | Description                        |
| ------ | ------------------------------- | ----------------- | ---------------------------------- |
| GET    | `/api/notes/patient/{id}`       | `note-service`    | Retrieve all notes for a patient   |
| GET    | `/api/patients/{id}`            | `patient-service` | Fetch patient info by internal ID  |
| POST   | `/internal-auth/internal-token` | `auth-service`    | Get internal JWT for service calls |

#### 📖 Endpoints

| Method | Path                     | Description                       | Required Role           |
| ------ | ------------------------ | --------------------------------- | ----------------------- |
| GET    | `/api/risk/patient/{id}` | Evaluate risk for a given patient | ROLE\_USER, ROLE\_ADMIN |

#### ❌ Handled Errors

| Exception                  | HTTP Status | Message                            |
| -------------------------- | ----------- | ---------------------------------- |
| `PatientNotFoundException` | 404         | Patient not found                  |
| `NoteRetrievalException`   | 500         | Failed to fetch notes from service |

---

### 🚪 `gateway-service`

**Entry point that routes and filters requests, verifying JWT tokens and propagating user identity.**

---

## 🔐 JWT Authentication Flow

1. **Client logs in** via `/api/auth/login` → receives JWT
2. **Client sends request** to a protected endpoint:

   ```http
   GET /api/notes/patient/123
   Authorization: Bearer <token>
   ```
3. **Gateway intercepts**:

   * Validates the token using `JwtUtil`
   * If valid: adds `X-auth-email`, `X-auth-role` headers
   * If invalid: returns `401 Unauthorized`
4. **Request routed** to downstream service (e.g. `note-service`)

---



## 🔐 JWT Security in Microservices

Even after Gateway validation, each microservice (e.g. `note-service`) has its own security filter chain:

* Validates the token again using Spring Security + `NimbusJwtDecoder`
* Extracts roles using a `CustomRoleConverter`
* Applies access rules per endpoint or with annotations:

  ```java
  @PreAuthorize("hasRole('ADMIN')")
  ```

---

## 🔧 CORS Configuration

The gateway allows CORS requests from:

```yaml
http://localhost:4200
```

Allowed methods: `GET, POST, PUT, DELETE, PATCH, OPTIONS`
Exposed headers: `Authorization, Content-Disposition`

---

## 🔑 Token Headers Added by Gateway

If the token is valid, the Gateway adds the following headers to all forwarded requests:

```http
X-auth-email: user@example.com
X-auth-role: USER
```

These headers can be used by microservices for logging or additional authorization logic.

---

## 🧠 Summary Flow Diagram

```
[Client]
   │
   ├── POST /api/auth/login → [Auth Service] → 🔑 Generates JWT
   │                              ↓
   └── GET /api/notes/... + Authorization: Bearer ...
           ↓
    [Gateway]
       ├─ Validates token
       ├─ Adds X-auth-email, X-auth-role
       └─ Forwards request to...
           ↓
    [Note Service]
       ├─ Validates token again (Spring Security)
       └─ Applies access rules (e.g. ROLE_USER)
```

---

#### ✅ Features

* Central routing to all microservices
* Validates JWT and adds headers (`X-auth-email`, `X-auth-role`)
* Allows CORS from Angular frontend (`http://localhost:4200`)

#### 🔐 JWT Headers Added by Gateway

```http
X-auth-email: user@example.com
X-auth-role: USER
```

#### ❌ Common Errors

| Issue               | HTTP Status | Message                 |
| ------------------- | ----------- | ----------------------- |
| Missing/Invalid JWT | 401         | Unauthorized request    |
| CORS issue          | 403         | Forbidden origin/method |

---

## 📊 Swagger Documentation

Each microservice exposes its own Swagger UI at:

| Service                 | Swagger URL                             |
| ----------------------- | --------------------------------------- |
| `auth-service`          | `http://localhost:8081/swagger-ui.html` |
| `patient-service`       | `http://localhost:8082/swagger-ui.html` |
| `note-service`          | `http://localhost:8083/swagger-ui.html` |
| `diabetes-risk-service` | `http://localhost:8084/swagger-ui.html` |

> ℹ️ Internal endpoints (like `/internal-auth/internal-token`) are secured with API keys and are not meant to be exposed via Swagger.

---

## 🐳 Dockerization & Environment Configuration

GlucoVision microservices are containerized using Docker and orchestrated via `docker-compose`.

### 📦 Docker Compose Overview

* Each microservice has its own Dockerfile
* A `docker-compose.yml` file orchestrates the services and their dependencies
* Databases (MySQL, MongoDB) are included as services

### 🔑 Environment Variables

Security-sensitive data (like JWT secrets and internal API keys) is **never hardcoded**.
They are injected at runtime using environment variables, ensuring secure and configurable deployment.

**Examples of required environment variables:**

| Variable Name           | Description                                  |
| ----------------------- | -------------------------------------------- |
| `JWT_SECRET`            | Secret used to sign/verify JWT tokens        |
| `INTERNAL_API_KEY`      | API key for internal service communication   |
| `SPRING_DATASOURCE_URL` | JDBC URL for MySQL connection (auth/patient) |
| `MONGO_URI`             | MongoDB connection URI for note service      |

Environment variables can be defined in a `.env` file or directly in `docker-compose.yml` like:

```yaml
services:
  auth-service:
    environment:
      - JWT_SECRET=${JWT_SECRET}
      - INTERNAL_API_KEY=${INTERNAL_API_KEY}
```

### ⚠️ Security Notice

For educational and demonstration purposes, sensitive environment variables such as `JWT_SECRET` and `INTERNAL_API_KEY` are defined directly in the `docker-compose.yml` file.

> In a real production environment, these should be managed via secret managers or CI/CD pipelines to ensure proper security.

### ▶️ Run the stack locally

```bash
docker-compose up --build
```

All services will start and be accessible via the Gateway on port `8080`.

---

## 🧪 Testing Strategy

GlucoVision includes both **unit tests** and **integration tests** to ensure functional coverage and reliability across all microservices.

### ✅ Coverage

* Current test coverage ranges from **70% to 90%** depending on the microservice.

### 🧪 Unit Testing

* Focused on service layers and utility classes
* Tools: `JUnit 5`, `Mockito`
* Example: ensuring notes cannot be updated after 24h

### 🔗 Integration Testing

* Full-stack tests using `Spring Boot Test`
* Some tests use embedded DBs like **H2**
* Example:

   * `NoteControllerIntegrationTest` with MongoDB or H2
   * `RiskControllerIntegrationTest` using real Feign calls to `note` and `patient`

### 🛠️ Tools Used

| Tool/Library     | Purpose                            |
| ---------------- | ---------------------------------- |
| Spring Boot Test | Integration testing (REST, Beans)  |
| Mockito          | Unit testing & mocking             |
| Testcontainers   | Real DB containers (MySQL/MongoDB) |
| H2 DB            | In-memory testing alternative      |

---

## 💻 `frontend` – Angular Web Client Interface

The **frontend** of GlucoVision is built with **Angular 16+**, using **standalone components**, **Angular Material**, and **modern state management via Signals**.

This web interface allows both **users** and **administrators** to interact seamlessly with the backend microservices via a secure, responsive UI routed through the API gateway.

---

## 🌐 Main Features

| Feature | Description |
|--------|-------------|
| 🔐 Authentication | Login/Register using JWT |
| 👥 Role-based access | `ADMIN`, `USER`, and `PENDING` roles |
| 🧑‍⚕️ Patient & Note Management | View/edit patient data and medical notes |
| 🧠 Risk Visualization | Diabetes risk levels displayed dynamically |
| 🧹 UI Reusability | Standalone components (modals, cards, dialogs) |
| 📡 Backend Integration | REST communication through Gateway |

---

## 📁 Project Structure

```
src/app
├── components/          # Reusable UI components (cards, dialogs, header, etc.)
├── pages/               # Feature pages (login, register, patients, admin, etc.)
├── services/            # HTTP services for backend interaction
├── model/               # TypeScript interfaces (Patient, Note, AppUser)
├── guards/              # Route protection (authGuard, adminGuard)
├── interceptors/        # JWT injection in HTTP headers
└── app-routing.module.ts
```

---

## 🧩 Core Components

| Component                      | Description                                      |
|-------------------------------|--------------------------------------------------|
| `LoginComponent`              | Handles user login and JWT retrieval             |
| `RegisterComponent`           | Handles new user registration                    |
| `PatientsComponent`           | Lists patients (USER role only)                  |
| `PatientDetailsComponent`     | Shows full patient info, notes, and risk level   |
| `AdminUserManagementComponent`| Admin dashboard for user accounts                |
| `AdminPatientsManagementComponent` | Admin dashboard for patients             |
| `AdminNotesComponent`         | Note management for each patient                |
| `AppCardComponent`            | Reusable card (user, patient, note)             |
| `EditDialogComponent`         | Generic form modal for editing any entity       |
| `AppHeaderComponent`          | Header with app title, user name, and logout    |

---

## 🛡️ Route Guards

| Guard        | Role                                    |
|--------------|-----------------------------------------|
| `authGuard`  | Requires a valid JWT to access a route  |
| `adminGuard` | Restricts access to `ADMIN` users only  |

---

## 🚀 Launch Instructions

```bash
cd frontend
npm install
ng serve
```

> 🧪 Available at: [http://localhost:4200](http://localhost:4200)  
> Ensure that the backend services are running at [http://localhost:8080](http://localhost:8080) for full integration.

---

## 📦 Build for Production

```bash
ng build
```

Output is generated in the `dist/` directory.

---

## 💡 Notes

- JWT and user info (`firstName`, `role`, `email`) are stored in `localStorage`
- All HTTP requests go through the **gateway** with a **Bearer token** handled by the `authInterceptor`
- **ADMIN** users can manage everything, while **USER** roles are limited to patients and notes

---

## 🪵 Centralized Logging for Microservices

Each Spring Boot microservice is configured to write its logs to a separate file, allowing for better traceability and a clear separation of logs per service.

The log files are centralized in a shared folder between Docker containers and the host machine. This enables both local access to the log files and their collection by observability tools.

To visualize the logs, a stack composed of **Loki**, **Promtail**, and **Grafana** is integrated into the project:

- **Promtail** automatically collects the log files generated by the microservices.
- **Loki** stores the logs in a structured and efficient way.
- **Grafana** provides a web interface for viewing, filtering, and searching the logs in real-time.

Logs are accessible locally from the `logs/` folder at the root of the project, and can also be viewed via the Grafana web interface.

---

### 📊 Accessing Grafana

- **URL**: [http://localhost:3000](http://localhost:3000)
- **Default credentials** (if first time use): `admin` / `admin`

#### Configuration steps in Grafana:

1. Go to **Configuration > Data Sources**
2. Add a new data source of type **Loki**
3. Enter the URL: `http://loki:3100`
4. Save

#### Viewing logs:

1. Go to **Explore**
2. Select the **Loki** data source
3. Use the following query to display all logs:

---
## Technical Notes

⚠️ On 26/05/2025, the `main` branch was reset to match `dev` (forced reset).  
The previous history was intentionally deleted.
