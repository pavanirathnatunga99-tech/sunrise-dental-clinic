# Open and run in Visual Studio Code

## Open the workspace

1. Install Visual Studio Code and Node.js 20 or newer.
2. Extract the export ZIP.
3. Double-click `sunrise-dental-clinic.code-workspace`.
4. Accept the recommended extensions when VS Code offers them.

## Run the demonstration version

1. Open **Terminal → Run Task**.
2. Run **Frontend: Install dependencies** once.
3. Run **Run Sunrise Clinic (Demo)**.
4. Open <http://localhost:5173>.

Use `admin` / `Admin@123` to sign in. The demo API stores local data in `dev-data.json` and does not require Maven or MySQL.

### Windows one-click start

After extracting the project, double-click `START-SUNRISE-WINDOWS.cmd`. It checks Node.js, installs missing frontend dependencies, starts both services, and opens the website. Keep the two terminal windows open while using the system.

## Run the Spring Boot and MySQL version

Install Java 17, Maven 3.9+, Node.js 20+, and MySQL 8. Configure the database using `database/schema.sql`, then:

1. Run **Backend: Run Spring Boot** from **Terminal → Run Task**.
2. Run **Frontend: Start React**.
3. Open <http://localhost:5173>.

The Spring Boot backend can also be started with **Run and Debug → Debug Spring Boot Backend**.

## Project locations

- `frontend/` — React and Bootstrap interface
- `backend/` — Spring Boot REST service
- `database/` — MySQL provisioning
- `dev-api.mjs` — dependency-free local demonstration API
- `README.md` — complete architecture and deployment guide
