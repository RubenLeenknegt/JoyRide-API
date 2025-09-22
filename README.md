# Fantastic Lamp

A Kotlin/Java backend with **MySQL**, containerized via **Docker**.  
CI/CD builds, tests, packages, and deploys images to a VPS via **Docker Hub**.

---

## 🚀 Tech Stack
- **Kotlin/Java** (Gradle, Shadow JAR)
- **MySQL 8**
- **Docker & Docker Compose**
- **GitHub Actions (CI/CD)**

---

## ⚙️ Prerequisites
- **JDK 17+**
- **Docker & Docker Compose**
- **Git**

---

## ▶️ Quick Start (Local)

### Linux/macOS
```bash
./gradlew :backend:build
docker-compose -f docker-compose.local.yml up --build
```

### Windows
```powershell
.\gradlew.bat :backend:build
docker-compose -f docker-compose.local.yml up --build
```

### Services
- **Backend** → [http://localhost:8081](http://localhost:8081)
- **PHPMyAdmin** → [http://localhost:8083](http://localhost:8083)

---

## 🌱 Environment Variables
Create a `.env.local` in the project root:

```env
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=testdb
#Normal user/pass for local development
MYSQL_USER=local_user
MYSQL_PASSWORD=local_pass
```

**Notes**
- `docker-compose.local.yml` reads `.env.local` via `env_file`.
- Files in `db-init` are executed by the MySQL image on first start of the data volume.
- SQL files do **not** expand environment variables. Use `MYSQL_*` vars directly, or wrap with a shell script if substitution is needed.

---

## 🛠 Local Development

### Common commands

Build fat jar:
```bash
./gradlew :backend:build
```

Start stack:
```bash
docker-compose -f docker-compose.local.yml up --build
```

Follow backend logs:
```bash
docker-compose -f docker-compose.local.yml logs -f backend
```

Reset DB (drops volume):
```bash
docker-compose -f docker-compose.local.yml down
docker volume rm local_db_data
docker-compose -f docker-compose.local.yml up --build
```

---

## 📂 Project Files of Interest
- `backend/Dockerfile`
- `backend/src/main/kotlin/`
- `db-init/init.sql`
- `docker-compose.local.yml`
- `docker-compose.acc.yml`
- `docker-compose.prod.yml`

---

## 🌿 Branching & Workflow
- Work on **weekly staging branches**
- **develop** accepts PRs only from staging branches
- **main** accepts PRs only from develop
- Enforced via `enforce-branch-sources.yml` & GitHub Rulesets

---

## 🔄 CI/CD & Environments

### Branches
- **develop** → acceptance image
- **main** → production image

### Pipeline steps
1. Checkout
2. Setup Java 17
3. Run tests (`backend/src/test/kotlin`, `backend/src/test/java`)
4. Build Shadow JAR
5. Build & push Docker image
6. SSH deploy on VPS (pull images, restart stack)

### Environments
- **Local** → `docker-compose.local.yml`
- **Acceptance** → `docker-compose.acc.yml`
- **Production** → `docker-compose.prod.yml`

---

## 🗃 Database Init
- Bootstrap schema/data in `db-init/init.sql`.
- Avoid invalid statements like `SELECT testdb;` → use `USE testdb;`.
- For env substitution, use an init `*.sh` wrapper that executes SQL with `mysql`.

---

## 📡 Ports
- **Backend** → 8081
- **PHPMyAdmin** → 8083
- **MySQL (internal)** → 3306

---

## 🩺 Troubleshooting

**Access denied for user local_user:**
- Ensure `.env.local` `MYSQL_*` matches backend `DB_*`.
- Recreate containers:
  ```bash
  docker-compose -f docker-compose.local.yml up -d --force-recreate
  ```

**SQL syntax errors on init:**
- Remove invalid lines like `SELECT testdb;`.
- Use valid MySQL syntax with `USE testdb;`.

---

## 📜 Logging & Resources
- **Docker Hub:** `fantastic-lamp-backend`
- **GitHub Actions:** CI/CD pipeline
- **Acceptance:** `http://<server-ip>:8081`

SCP example:
```bash
scp <filename> root@<server-ip>:~/fantastic-lamp/
```

---

## 📄 License
**MIT** (update if applicable).
