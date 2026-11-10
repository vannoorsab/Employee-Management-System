# Employee Management System

A full-stack employee management application. A **Spring Boot** REST API (Spring Data JPA + Hibernate) stores employee data in **MySQL**, and a **React** frontend lets users add, view, search, edit and delete employees.

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA, Hibernate, Bean Validation |
| Database | MySQL 8 |
| Frontend | React 18, Vite |
| Tests | JUnit 5, Mockito, Spring MockMvc |

## Features

- REST APIs for employee CRUD (create, read, update, delete) using Spring Data JPA and Hibernate
- Search employees by name, email or department
- Request validation with clear field-level error messages
- Centralized exception handling (`@RestControllerAdvice`) that returns one consistent JSON error format
- Duplicate-email protection (HTTP 409)
- React frontend integrated with the backend APIs

## Project structure

```
employee-management-system/
├── backend/                         Spring Boot application
│   ├── pom.xml
│   └── src/main/java/com/vannoor/ems/
│       ├── EmsApplication.java      Entry point
│       ├── controller/              REST endpoints (HTTP only)
│       ├── service/                 Business logic
│       ├── repository/              Spring Data JPA repository
│       ├── model/                   JPA entity (Employee)
│       ├── dto/                     Request and response objects + validation rules
│       └── exception/               Custom exceptions + GlobalExceptionHandler
├── frontend/                        React (Vite) application
│   └── src/
│       ├── api.js                   All calls to the backend
│       ├── App.jsx                  State and page logic
│       └── components/              EmployeeForm, EmployeeTable
└── docs/STUDY_GUIDE.md              Explanation and interview questions
```

## Prerequisites

- JDK 17 or newer
- Maven 3.9+
- MySQL 8 running locally
- Node.js 18 or newer

## Run it

**1. Backend**

```bash
cd backend

# Linux / macOS
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password

# Windows PowerShell
# $env:DB_USERNAME="root"; $env:DB_PASSWORD="your_mysql_password"

mvn spring-boot:run
```

The API starts on `http://localhost:8080`. The `ems_db` database and the `employees` table are created automatically on first start.

**2. Frontend** (in a second terminal)

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. In development, Vite forwards `/api` requests to the backend, so no CORS setup is needed.

**3. Tests**

```bash
cd backend
mvn test
```

## API

| Method | URL | Description | Success |
|---|---|---|---|
| GET | `/api/employees` | List all employees (optional `?search=text`) | 200 |
| GET | `/api/employees/{id}` | Get one employee | 200 |
| POST | `/api/employees` | Create an employee | 201 + `Location` header |
| PUT | `/api/employees/{id}` | Update an employee | 200 |
| DELETE | `/api/employees/{id}` | Delete an employee | 204 |

Example request body:

```json
{
  "firstName": "Asha",
  "lastName": "Rao",
  "email": "asha.rao@example.com",
  "department": "Engineering",
  "jobTitle": "Software Engineer",
  "salary": 65000.00,
  "joiningDate": "2024-06-01"
}
```

Try it with curl:

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Asha","lastName":"Rao","email":"asha.rao@example.com","department":"Engineering","jobTitle":"Software Engineer","salary":65000,"joiningDate":"2024-06-01"}'

curl http://localhost:8080/api/employees
```

## Validation rules

| Field | Rule |
|---|---|
| firstName, lastName | required, up to 50 characters |
| email | required, valid format, up to 100 characters, must be unique |
| department | required, up to 50 characters |
| jobTitle | required, up to 80 characters |
| salary | required, greater than 0, up to 10 digits and 2 decimals |
| joiningDate | required, format `yyyy-MM-dd`, not in the future |

## Error format

Every error uses the same JSON shape. Example for invalid input (HTTP 400):

```json
{
  "timestamp": "2026-11-06T11:30:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/employees",
  "fieldErrors": {
    "email": "Email must be a valid email address",
    "salary": "Salary must be greater than 0"
  }
}
```

| Situation | Status |
|---|---|
| Invalid or missing field, malformed JSON, bad id format | 400 |
| Employee id does not exist | 404 |
| Email already used by another employee | 409 |
| Unexpected server error | 500 |

## Possible improvements

Pagination and sorting, authentication with Spring Security, database migrations with Flyway, Docker Compose for MySQL, and integration tests with Testcontainers.
