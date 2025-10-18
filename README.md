# Fantastic Lamp

A Kotlin/Ktor backend with MySQL, containerized with Docker. This repository contains:
- A Ktor HTTP API (Kotlin/JVM 17) using Exposed ORM and HikariCP
- MySQL 8 with schema and seed data via db-init/init.sql
- Docker Compose configurations for local, acceptance, and production

If you are new to the project, start with Quick Start.

---

## Tech stack
- Language: Kotlin (JVM), Kotlinx Serialization
- Web framework: Ktor 2.3.x (Netty)
- ORM/DB: Exposed (core/dao/jdbc), HikariCP, MySQL 8
- Build & package: Gradle (Kotlin DSL), Shadow plugin → backend-fat.jar
- Containers: Docker, Docker Compose

---

## Requirements
- JDK 17+
- Docker Desktop (or Docker Engine) and Docker Compose
- Git

---

## Quick start (local)

Create a .env.local in the project root:

```env
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=testdb
# normal user/pass for local development
MYSQL_USER=local_user
MYSQL_PASSWORD=local_pass
```

Build the backend fat JAR and start the stack:

- Linux/macOS
  - ./gradlew :backend:build
  - docker compose -f docker-compose.local.yml up --build

- Windows (PowerShell)
  - .\gradlew.bat :backend:build
  - docker compose -f docker-compose.local.yml up --build

Services (local compose):
- Backend → http://localhost:8081
- phpMyAdmin → http://localhost:8083
- MySQL (host port) → 3306

Notes
- docker-compose.local.yml loads .env.local via env_file.
- Files in db-init are executed automatically on first start of the MySQL volume.
- SQL files do not expand environment variables. Use literal values or a shell wrapper if you need substitution.

---

## How it runs
- Application entry point (JVM): leafcar.backend.ApplicationKt.main
  - Binds Netty server on 0.0.0.0:8080 inside the container.
- Container entrypoint: java -jar app.jar (see backend/Dockerfile)
- Compose port mapping (local/acc): 8081:8080, (prod): 8080:8080

Endpoints (current)
- GET / → simple HTML landing page
- GET /cars → returns cars (query parameters supported as filters, e.g. /cars?brand=BMW)
- GET /users → returns all users

---

## Environment variables
The backend reads these variables (with defaults for local):
- MYSQL_DATABASE → used in JDBC URL (default: local_db)
- MYSQL_USER → database username (default: local_user)
- MYSQL_PASSWORD → database password (default: local_pass)

MySQL container uses in addition:
- MYSQL_ROOT_PASSWORD → root password (required by MySQL image)

JDBC URL format used by the app: jdbc:mysql://db:3306/${MYSQL_DATABASE}

---

## Development workflows
Common commands
- Build fat jar: ./gradlew :backend:build
- Run unit tests: ./gradlew :backend:test
- Tail backend logs: docker compose -f docker-compose.local.yml logs -f backend
- Recreate stack: docker compose -f docker-compose.local.yml up --build -d
- Reset DB (drop volume):
  - docker compose -f docker-compose.local.yml down
  - docker volume rm local_db_data
  - docker compose -f docker-compose.local.yml up --build

Helper script (Windows)
- rebuild-fatjar-docker.ps1 — purpose and usage
  - What it does:
    - Runs a clean Gradle build to produce backend/build/libs/backend-fat.jar.
    - Brings the local Docker Compose stack down and removes volumes (down -v), wiping the MySQL data volume.
    - Brings the stack back up and rebuilds images (up --build -d).
  - When to use it:
    - After changing backend code or dependencies and you want a fresh image and a clean database.
    - When local MySQL data got into a bad state and you want to reset it using db-init/init.sql.
  - When not to use it:
    - If you want to keep your current DB data. Use docker compose -f docker-compose.local.yml up --build instead.
  - Prerequisites:
    - Docker Desktop running, and a .env.local file in the project root.
  - Usage examples (PowerShell):
    - .\rebuild-fatjar-docker.ps1
    - .\gradlew.bat clean :backend:build; docker compose -f docker-compose.local.yml down -v; docker compose -f docker-compose.local.yml up --build -d  (equivalent manual commands)
  - Notes:
    - The script defines a -SkipTests switch, but the current implementation always runs tests as part of the Gradle build. Update the script if you want to actually skip tests.

---

## Tests
- Gradle: ./gradlew :backend:test (or .\\gradlew.bat :backend:test on Windows)
- Current test files: backend/src/test (note: CarControllerTest.kt is a stub and commented out)
- HTTP request examples for manual testing: backend/src/test/http-requests/CarRequests.http
- TODO: Add Ktor testApplication-based tests and enable CI to run them.

---

## Project structure
- backend/
  - build.gradle.kts (module build)
  - Dockerfile (copies build/libs/backend-fat.jar, exposes 8080)
  - src/main/kotlin/
    - leafcar/backend/Application.kt (Ktor module and main)
    - leafcar/backend/controller/ (CarController.kt, UserController.kt)
    - leafcar/backend/repository/ (repositories)
    - leafcar/backend/dao/ (Exposed tables/entities)
    - leafcar/backend/domain/ (domain models/enums)
  - src/test/kotlin/ (tests)
  - src/test/http-requests/ (HTTP examples)
- db-init/
  - init.sql (schema and seed data; includes FKs)
- docker-compose.*.yml (local, acc, prod)
- gradlew / gradlew.bat, gradle/ (wrapper)

---

## Deployment & environments
- Local → docker-compose.local.yml (ports: backend 8081, phpMyAdmin 8083)
- Acceptance → docker-compose.acc.yml (image tag: acc, ports: backend 8081, phpMyAdmin 8083)
- Production → docker-compose.prod.yml (image tag: prod, ports: backend 8080, phpMyAdmin 8084)

CI/CD
- TODO: Workflows are not present in this repository snapshot. Document or add .github/workflows for build, test, image build/push, and remote deploy if applicable.

Database init in non-local envs
- db-init/init.sql is mounted by compose files. Ensure volumes and permissions are correct on the target host.
- TODO: Confirm whether manual init is still required on VPS and document the exact procedure if so.

---

## License
MIT (if unsure, confirm with the maintainers). TODO: Verify license and update if different.
