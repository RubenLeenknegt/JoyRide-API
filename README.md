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
- Coverage: Kover (org.jetbrains.kotlinx.kover)
- Containers: Docker, Docker Compose

---

## Requirements

- JDK 17+
- Docker Desktop (or Docker Engine) and Docker Compose
- Git

---

## Quick start (local)

Create a .env in the project root: \
If you are running tests, then also create a .env file in src/.env otherwise tests that use a .env will not work.

```env
# JWT (required by the app). Choose your own secrets/values
JWT_SECRET=dev-secret
JWT_ISSUER=joyride-backend
JWT_AUDIENCE=joyride-clients
JWT_BACKEND_REALM=joyride Backend
JWT_BACKEND_AUTH_NAME=auth-jwt
```

Also create a .env.local in the project root:

```# MySQL
# Required for docker builds
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=testdb
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
- Windows (PowerShell Script)
  - .\rebuild-fatjar-docker.ps1

Services (local compose):

- Backend → http://localhost:8081
- phpMyAdmin → http://localhost:8083
- MySQL (host port) → 3306

Notes

- docker-compose.local.yml loads .env.local via env_file, and mounts project .env into container at /app/.env.
- Files in db-init are executed automatically on first start of the MySQL volume.
- SQL files do not expand environment variables. Use literal values or a shell wrapper if you need substitution.

---

## How it runs

- Application entry point (JVM): joyride.backend.ApplicationKt.main
    - Binds Netty server on 0.0.0.0:8080 inside the container.
- Container entrypoint: java -jar app.jar (see backend/Dockerfile)
- Compose port mapping (local/acc): 8081:8080, (prod): 8080:8080

Endpoints (current)

- GET / → simple HTML landing page
- GET /cars → returns cars (query parameters supported as filters, e.g. /cars?brand=BMW)
- GET /users → returns all users
- ...plus routes for reservations, availabilities, rides, photos, auth, and bonus points.

---

## Environment variables

The backend reads these variables (with defaults for local):

- MYSQL_DATABASE → used in JDBC URL (default: local_db)
- MYSQL_USER → database username (default: local_user)
- MYSQL_PASSWORD → database password (default: local_pass)

Auth/JWT variables read from .env (via dotenv):

- JWT_SECRET → HMAC secret for signing JWTs
- JWT_ISSUER → expected issuer
- JWT_AUDIENCE → expected audience
- JWT_BACKEND_REALM → authentication realm shown in challenges (note the missing "D" in code)
- JWT_BACKEND_AUTH_NAME → Ktor auth provider name

MySQL container uses in addition:

- MYSQL_ROOT_PASSWORD → root password (required by MySQL image)

JDBC URL format used by the app: jdbc:mysql://db:3306/${MYSQL_DATABASE}

---

## Development workflows

Common commands

- Build fat jar: ./gradlew :backend:build
- Run unit tests: ./gradlew :backend:test
- Generate coverage (Kover HTML): ./gradlew :backend:koverHtmlReport
- Generate coverage (XML): ./gradlew :backend:koverXmlReport
- Verify coverage (fails build if rules added): ./gradlew :backend:koverVerify
- Tail backend logs: docker compose -f docker-compose.local.yml logs -f backend
- Recreate stack: docker compose -f docker-compose.local.yml up --build -d
- Reset DB (drop volume):
    - docker compose -f docker-compose.local.yml down
    - docker volume rm local_db_data
    - docker compose -f docker-compose.local.yml up --build

Coverage reports

- HTML report: backend/build/reports/kover/html/index.html
- XML report (for CI): backend/build/reports/kover/report.xml

Helper scripts: Rebuild fat JAR and local Docker stack

- Purpose
  - Automate rebuilding the backend fat JAR, resetting the local Docker Compose stack, and rebuilding images.
  - Useful after code or dependency changes, or when you want to wipe and re-seed the local MySQL using db-init.
- WARNING
  - These scripts bring the stack down with -v (remove volumes). Your local MySQL data will be deleted and recreated.

Scripts and usage

- Linux/macOS (Bash)
  - ./rebuild-fatjar-docker.sh [--skip-tests]
- Windows (PowerShell)
  - .\rebuild-fatjar-docker.ps1 [-SkipTests]

What they do

- Run Gradle builds to produce backend/build/libs/backend-fat.jar.
- Tear down the Docker Compose stack with volumes: docker compose -f docker-compose.local.yml down -v.
- Bring the stack back up, rebuilding images: docker compose -f docker-compose.local.yml up --build -d.

Notes and differences

- Bash script (rebuild-fatjar-docker.sh)
  - Chooses ./gradlew if available, otherwise system gradle.
  - Supports --skip-tests to skip tests for both the root build and the backend build.
- PowerShell script (rebuild-fatjar-docker.ps1)
  - Chooses .\\gradlew.bat if available, otherwise system gradle.
  - Supports -SkipTests for the initial clean build; the dedicated backend build currently always runs tests. If you need to skip tests there as well, run the equivalent manual commands or adjust the script.

Prerequisites

- Docker Desktop (or Docker Engine) running.
- .env.local present in the project root (used by docker-compose.local.yml).

Examples

- Linux/macOS
  - ./rebuild-fatjar-docker.sh
  - ./rebuild-fatjar-docker.sh --skip-tests
- Windows (PowerShell)
  - .\rebuild-fatjar-docker.ps1
  - .\rebuild-fatjar-docker.ps1 -SkipTests

Equivalent manual commands

- ./gradlew :backend:build
- docker compose -f docker-compose.local.yml down -v
- docker compose -f docker-compose.local.yml up --build -d

---

## Tests

- Run tests: ./gradlew :backend:test (or .\\gradlew.bat :backend:test on Windows)
- Coverage: see Kover commands in Development workflows.
- HTTP request examples for manual testing: backend/src/test/http-requests/CarRequests.http

Unit tests and the test database

- Tests use an in-memory H2 database in MySQL compatibility mode.
    - JDBC URL: jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL
    - Driver: org.h2.Driver
    - Configured in: backend/src/test/kotlin/joyride/backend/TestDatabaseConfig.kt
- HikariCP test datasource is created in TestDatabaseConnection and used to connect Exposed.
- Before each test suite that calls EnvironmentSetup.setup(db), the schema is dropped and recreated using Exposed
  SchemaUtils (no migrations are run in tests):
    - See backend/src/test/kotlin/joyride/backend/EnvironmentSetup.kt
- Example usage in tests:
    - Database.connect(HikariDataSource(TestDatabaseConnection.getDataSource()))
    - EnvironmentSetup.setup(database)
- Production/local dev DB initialization is handled by db-init/init.sql via Docker Compose; tests do NOT execute db-init
  scripts.

TODOs for testing

- Add more Ktor testApplication-based tests.
- Consider Flyway/Liquibase for schema migrations and apply equivalent setup in tests if adopted.

---

## Project structure

- backend/
    - build.gradle.kts (module build with Kover configured)
    - Dockerfile (copies build/libs/backend-fat.jar, exposes 8080)
    - src/main/kotlin/
        - joyride/backend/Application.kt (Ktor module and main)
        - joyride/backend/controller/ (CarController.kt, UserController.kt, ...)
        - joyride/backend/repository/ (repositories)
        - joyride/backend/dao/ (Exposed tables/entities)
        - joyride/backend/domain/ (domain models/enums)
        - joyride/backend/services/ (business logic incl. AuthService)
    - src/test/kotlin/ (tests, H2 config, schema setup)
    - src/test/http-requests/ (HTTP examples)
    - photos/ (mounted into container at /app/photos)
- db-init/
    - init.sql (schema and seed data; includes FKs and sample data)
- docker-compose.*.yml (local, acc, prod)
- gradlew / gradlew.bat, gradle/ (Gradle wrapper)

---

## Deployment & environments

- Local → docker-compose.local.yml (ports: backend 8081, phpMyAdmin 8083)
- Acceptance → docker-compose.acc.yml (image tag: acc, ports: backend 8081, phpMyAdmin 8083)
- Production → docker-compose.prod.yml (image tag: prod, ports: backend 8080, phpMyAdmin 8084)

CI/CD

- TODO: Workflows are not present in this repository snapshot. Document or add .github/workflows for build, test, image
  build/push, and remote deploy if applicable.

Database init in non-local envs

- db-init/init.sql is mounted by compose files. Ensure volumes and permissions are correct on the target host.
- TODO: Confirm whether manual init is still required on VPS and document the exact procedure if so.

---

## License

MIT (if unsure, confirm with the maintainers). TODO: Verify license and update if different.
