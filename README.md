# Automated Test Execution Framework

## Overview
A Spring Boot-based framework for automated test execution using TestNG, Selenium, and Rest-Assured. Supports database integration, REST APIs, parallel execution, scheduling, reporting, and artifact management.

## Milestone 1: DB Integration & Basic APIs
- Entities: TestCase, TestResult
- Repositories: TestCaseRepository, TestResultRepository
- Services: TestCaseService, TestResultService
- REST APIs:
  - POST /tests → Create new test case
  - GET /tests/{id} → Fetch test case by ID
  - GET /tests → Fetch all test cases
  - POST /results → Create new test result
  - GET /results → Fetch all test results
- Database: MySQL (auto-created tables, Flyway migration)

## Setup Instructions
1. Install MySQL and create a database named `test_framework_db`.
2. Update `src/main/resources/application.properties` with your MySQL username and password.
3. Build the project:
   ```shell
   mvn clean install
   ```
4. Run the application:
   ```shell
   mvn spring-boot:run
   ```
5. Test APIs using Postman or curl:
   - Create test case:
     ```http
     POST http://localhost:8080/tests
     Content-Type: application/json
     {
       "name": "Login Test",
       "type": "UI",
       "description": "Tests login functionality",
       "status": "ACTIVE"
     }
     ```
   - Get all test cases:
     ```http
     GET http://localhost:8080/tests
     ```
   - Create test result:
     ```http
     POST http://localhost:8080/results
     Content-Type: application/json
     {
       "testCase": { "id": 1 },
       "status": "PASSED",
       "executedAt": "2025-09-18T10:00:00"
     }
     ```
   - Get all test results:
     ```http
     GET http://localhost:8080/results
     ```

## Milestone 2: Parallel Execution & Automated Tests

### BlazeDemo UI Tests (Selenium, TestNG)
- Location: `src/test/java/com/example/ui/BlazeDemoUITest.java`
- 10 UI test methods: homepage load, dropdowns, flight search, booking flows, invalid booking, etc.
- Screenshots saved to `/screenshots` for each test.
- Thread-local WebDriver for thread safety.

### ReqRes API Tests (Rest-Assured, TestNG)
- Location: `src/test/java/com/example/api/ReqResAPITest.java`
- 10 API test methods: GET users, single user valid/invalid, POST create user, PUT/PATCH update user, DELETE user, register valid/invalid, login valid.
- Console logs for evidence and traceability.

### Parallel Execution
- TestNG suite: `src/test/resources/testng.xml` (parallel="methods", thread-count=10)
- Individual smoke suites: `blaze_smoke.xml`, `reqres_smoke.xml`

### How to Run Tests
1. Ensure ChromeDriver is installed and available in your PATH.
2. Run all tests in parallel:
  ```shell
  mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml
  ```
3. Run BlazeDemo UI smoke suite:
  ```shell
  mvn test -Dsurefire.suiteXmlFiles=src/test/resources/blaze_smoke.xml
  ```
4. Run ReqRes API smoke suite:
  ```shell
  mvn test -Dsurefire.suiteXmlFiles=src/test/resources/reqres_smoke.xml
  ```

### Evidence Collection
- Screenshots: `/screenshots` (UI tests)
- Logs: `/logs/run_parallel.log` (all tests)
- Console output includes thread IDs for traceability.
- TestNG HTML and XML reports generated in `/target/surefire-reports`

### Traceability & Analysis
- Each test logs thread ID, status, and evidence.
- Results can be exported from TestNG reports for further analysis.

## Milestone 3: Final Deliverables

### Executions
- BlazeDemo and ReqRes suites can be executed via REST API or Maven/TestNG.
- Execution status tracked via `/api/executions/{id}/status`.

### Reports
- After each suite execution, reports are generated in `/reports/{executionId}/`:
  - `report.html` (human-readable, clickable artifact links)
  - `report.csv` (Excel/data analysis)
  - `junit-report.xml` (CI/CD integration)
- Download via `/api/reports/{executionId}/download?type=html|csv|junit`.

### Artifacts
- Screenshots (UI) and API logs (request/response JSON) saved in `/artifacts/{executionId}/{testCaseId}/`.
- Download via `/api/artifacts/{artifactId}` (where `artifactId` is relative path under `/artifacts`).

### Database Schema
- JPA entities: `Execution`, `TestResult`, `Artifact`, `Report` (see `src/main/java/com/example/model/`).
- Tables auto-created in MySQL via Flyway/JPA.

### How to Use Scheduler Service
1. **Start Scheduler Service**
   ```shell
   mvn spring-boot:run
   ```
2. **Schedule Runs (Now/Later)**
   - POST `/api/schedule/run` with JSON body `{ "suite": "blazedemo" }` or `{ "suite": "reqres" }`
   - Example:
     ```http
     POST http://localhost:8080/api/schedule/run
     Content-Type: application/json
     {
       "suite": "blazedemo"
     }
     ```
3. **Check Execution Status**
   - GET `/api/executions/{executionId}/status`
   - Example:
     ```http
     GET http://localhost:8080/api/executions/1695472000000/status
     ```
4. **Download Reports/Artifacts**
   - GET `/api/reports/{executionId}/download?type=html|csv|junit`
   - GET `/api/artifacts/{artifactId}`
   - Example:
     ```http
     GET http://localhost:8080/api/reports/1695472000000/download?type=html
     GET http://localhost:8080/api/artifacts/1695472000000/testHomePageLoad_TC_UI_01/screenshot.png
     ```

## Project Checklist (Step 11)

- [x] Suites created (`blaze_smoke.xml`, `reqres_smoke.xml`)
- [x] Scheduler works (run now/later via API)
- [x] Execution states tracked (QUEUED/RUNNING/COMPLETED/FAILED)
- [x] Parallel execution enabled (UI=4 threads, API=8 threads)
- [x] Artifacts saved correctly (`/artifacts/{executionId}/{testCaseId}/`)
- [x] Reports generated (HTML, CSV, JUnit in `/reports/{executionId}/`)
- [x] APIs available (control, report, artifact)
- [x] Validation runs successful (test case evidence logic)
- [x] README, reports, artifacts included

All milestone requirements are met and deliverables are included for final review.

---

For questions or issues, contact the project maintainer.
