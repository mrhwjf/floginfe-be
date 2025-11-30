# Repository Overview

This repository contains a React frontend (Vite) and a Spring Boot backend (Maven). The `/.github/workflows/login.ci.yml` file runs backend unit/integration tests with JaCoCo coverage, frontend Jest tests, and a Cypress E2E login spec and product management spec.

It implements simple login functionality with user authentication and product management features (CRUD).

This repository is created to learn about Test Driven Development (TDD) and Continuous Integration (CI) practices.

## CI Overview
- `backend-tests`: starts a MySQL service, runs `mvn test` and generates JaCoCo report. Artifacts: `target/site/jacoco`, `target/jacoco.exec`, surefire/failsafe reports.
- `frontend-tests`: installs deps and runs `npm test -- --coverage`, uploads `frontend/coverage`.
- `e2e-login`: builds frontend, starts backend and serves preview, runs Cypress spec `cypress/e2e/login.e2e.spec.js` (or `cypress/e2e/product.e2e.spec.js`). Artifacts: Cypress screenshots and logs.

## Run locally (quick steps)

Prerequisites:
- Java 21+ and Maven
- Node.js 18+ (the project uses Node 22 in CI but Node 18+ should work)
- MySQL running locally with database `FLogin` and user `root` / password `root` (or update `backend/src/main/resources/application.properties` / a `.env` file if you use different creds)

Recommended `.env` next to `backend/pom.xml` (picked up by `spring.config.import`):

```properties
DB_URL=jdbc:mysql://localhost:3306/FLogin
DB_USERNAME=root
DB_PASSWORD=root
```

Backend:
```powershell
cd backend
# run tests (requires MySQL up if integration tests touch DB)
.\mvnw -B clean test
# run app
.\mvnw spring-boot:run
```

Frontend:
```powershell
cd frontend
npm install
npm test
npm run build
# start dev server
npm run dev
```

E2E (local):
1. Start backend: `spring-boot:run` (port 8080)
2. Start frontend dev (`npm run dev`) or preview build (`npm run build && npx vite preview --port 5173`)
3. Run Cypress:
```powershell
cd frontend
npx cypress open # for interactive
# or headless
npx cypress run --spec "cypress/e2e/login.e2e.spec.js" --env CYPRESS_BACKEND_URL=http://localhost:8080,CYPRESS_E2E_USER=admin,CYPRESS_E2E_PASS=abc123 --config baseUrl=http://localhost:5173
```

## Notes
- CI uses MySQL service container; ensure tests are compatible with MySQL schema.
- Workflow uploads test artifacts to the run for debugging failures.
