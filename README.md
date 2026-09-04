# Sunrise Dental Clinic

A distributed clinic-management application with a React/Bootstrap browser client, a secured Spring Boot REST service, and MySQL persistence.

For Visual Studio Code, open `sunrise-dental-clinic.code-workspace` and follow `VSCODE-SETUP.md`.

## Included workflows

- JWT staff login and logout, with `ADMIN`, `RECEPTIONIST`, and `DENTIST` roles
- Patient registration, search, and editing
- Unique appointment numbers; daily schedule, lookup, editing, completion, and cancellation
- Dentist-duration overlap checks that prevent double-booking
- Admin-only dentist and treatment maintenance
- Billing from consultation fee plus selected treatment costs
- Paid/unpaid tracking and a print-optimized receipt
- Daily dashboard with appointment, patient, dentist, and revenue figures
- Bean validation, friendly field/API errors, layered service/repository architecture
- Swagger/OpenAPI REST documentation at `/swagger-ui.html`

## Architecture

```text
React + Bootstrap (port 5173)
          │ JSON/HTTPS + JWT
          ▼
Spring REST controllers (port 8080)
          ▼
Service layer (rules and transactions)
          ▼
Spring Data repositories / JPA / Hibernate
          ▼
MySQL 8 (port 3306)
```

This is distributed because the browser client, backend service, and database are independently deployable processes communicating across network boundaries. MVC responsibilities are separated between React views, REST controllers, domain/service logic, and JPA models.

## Fastest start: Docker Compose

Install Docker Desktop, then run from this directory:

```bash
docker compose up --build
```

Open <http://localhost:5173>. The first-run demo administrator is:

```text
Username: admin
Password: Admin@123
```

Change that password and the JWT/database secrets before a real deployment. Stop with `docker compose down`. Add `-v` only if you intentionally want to erase the MySQL volume.

## Run services manually

Requirements: Java 17, Maven 3.9+, Node 20+, npm, and MySQL 8.

1. Run `database/schema.sql` as a MySQL administrator, or create a database/user matching `backend/src/main/resources/application.yml`.
2. In `backend`, run `mvn spring-boot:run`.
3. In `frontend`, run `npm install` and then `npm run dev`.
4. Open <http://localhost:5173>.

### Local demo when Maven/MySQL are unavailable

The included dependency-free development API mirrors the UI's REST routes and stores demo records in `dev-data.json`:

```bash
node dev-api.mjs
```

Run it from the project root while the React development server is running. This local convenience service is for demonstrations only; production and assignment deployment should use the Spring Boot/MySQL backend.

For custom credentials, set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` before starting the backend. Set `VITE_API_URL` before building the frontend when the API is hosted elsewhere.

## Useful API routes

| Method | Route | Purpose |
|---|---|---|
| POST | `/api/auth/login` | Staff login |
| GET/POST | `/api/patients` | Search/register patients |
| GET/POST | `/api/appointments` | Daily list/create booking |
| GET/PUT | `/api/appointments/{number}` | Display/edit booking |
| PATCH | `/api/appointments/{number}/cancel` | Cancel booking |
| POST | `/api/bills/appointment/{number}` | Calculate or retrieve bill |
| PATCH | `/api/bills/{invoice}/pay` | Mark invoice paid |
| GET | `/api/dashboard` | Daily report summary |

All routes except login and API documentation require `Authorization: Bearer <token>`. Dentist and treatment writes require `ADMIN`.

## Verification

```bash
cd backend && mvn test
cd frontend && npm run build
```

The backend test profile uses in-memory H2 in MySQL compatibility mode, so tests do not alter clinic data.
