# 🚀 Golden Path Project Template (Java & Spring Boot)

Welcome to the **Golden Path Project Template** (`ms-template-java`). This repository serves as the official technical baseline and architectural blueprint for developing new microservices across the organization. It embodies Clean Architecture best practices, a modern Gradle build system (Kotlin DSL, Composite Builds), security by design, and resilience.

## 📋 Prerequisites

To run and develop in this project, the following tools are required in your environment:

- **[Java 26](https://adoptium.net/)** (recommended via SDKMAN!)
- **[Docker](https://docs.docker.com/get-docker/)** and **Docker Compose**
- **[Task](https://taskfile.dev/)** (Modern task runner, replacement for `Make`)
- **[Lefthook](https://github.com/evilmartians/lefthook)** (For Git hooks, installed globally via NPM/Homebrew)
- *Optional, but recommended:* `trivy`, `semgrep`, `gitleaks`, `k6`, `newman`. (The `Taskfile` automatically falls back to Docker if they are not installed natively).

## 🏗️ Architecture & Project Structure

This template adheres to **Clean Architecture** (Hexagonal Architecture / Ports and Adapters) and **Screaming Architecture**, grouping packages by business capabilities rather than technical concerns.

```text
app/src/main/java/com/example/templatejava/
├── common/                  # Shared infrastructure (e.g., Exception Handlers, scheduling configs)
├── customer/                # Business Capability: Customer
│   ├── domain/              # Core logic, 100% pure (no Spring/Mongo annotations)
│   ├── application/         # Use Cases / Interactors
│   └── infrastructure/      # Adapters (REST, Mongo, Feign, Jobs)
└── order/                   # Business Capability: Order
```

### Golden Rules
1. **Pure Domain**: The `domain` package must have no knowledge of infrastructure, web, or persistence classes.
2. **Value Objects**: Avoid *Primitive Obsession* — use *records* to model immutable concepts.
3. **In-Memory Fakes**: Prefer using Fakes (e.g., `InMemoryCustomerRepository`) in `application` tests for isolation and superior execution speed compared to mocking frameworks.

## 🚀 Running Locally

We use **Task** to standardize all common commands throughout the project lifecycle. To view the complete list of available tasks, run:

```bash
task
```

### Execution Profiles (`api` vs `scheduling`)

The application is designed to run in isolated runtime contexts for optimized horizontal scalability:
- **`api`**: Boots the web server and exposes HTTP and REST endpoints. (Spring Profile: `api`)
- **`scheduling`**: Executes asynchronous jobs and background workers (e.g., customer synchronization). (Spring Profile: `scheduling`)

### Starting the Application

Spin up the base infrastructure (MongoDB and core dependencies), followed by the application.

```bash
# Start database and core dependencies
task infra:core

# Run the web module (API)
task run:api

# Run the workers (Scheduling)
task run:scheduling
```

Or, via Docker Compose:
```bash
# Spin up both API and Scheduling containers
task docker:app:up
```

## 🛠️ Taskfile Commands

Here are the primary commands available in `Taskfile.yaml`:

### Build & Tests
- `task check`: Runs the full verification suite (unit tests, integration tests with Testcontainers, JaCoCo coverage, Spotless formatting check, and ArchUnit).
- `task build`: Compiles the application and packages the JAR.
- `task test:unit`: Executes fast unit tests.
- `task test:integration`: Executes integration tests (spins up local database via Testcontainers).
- `task test:mutation`: Runs mutation tests with Pitest to ensure the resilience of domain validation logic.

### Security (SCA and SAST)
- `task security:gitleaks`: Scans the repository for leaked secrets and credentials.
- `task security:trivy`: Scans source code and container images for dependency vulnerabilities (CVEs).
- `task security:sast`: SAST security scan with Semgrep against the OWASP Top 10.
- `task security:sbom`: Generates the Software Bill of Materials (CycloneDX).

### Load & API Testing
- `task newman`: Executes automated API tests with Newman/Postman.
- `task k6:smoke` / `k6:load` / `k6:stress`: Performance and load testing suite powered by k6.

### Database Migrations (migrate-mongo)
- `task migrate:status`: Displays migration status (applied vs pending).
- `task migrate:up`: Applies all pending migrations to MongoDB.
- `task migrate:down`: Rolls back the last applied migration.
- `task migrate:create -- <name>`: Creates a new timestamped migration script in `migrate-mongo/migrations/`.
- For more details and Docker execution options, see [`migrate-mongo/README.md`](migrate-mongo/README.md).

## 🤝 Submitting PRs & Contributing

Quality is enforced starting at commit time using **Lefthook** and **Commitlint**.

1. **Set up the repository for the first time**:
   ```bash
   task setup:hooks
   ```
2. Create a new branch from `main`.
3. Ensure code conforms to *Spotless Format* and architecture rules pass by running `task check`.
4. Commit messages must follow the **Conventional Commits** standard (e.g., `feat: add order creation endpoint`, `fix: validate email formatting`).
5. Lefthook Git hooks will lint commit messages, analyze potential secret leaks, and verify formatting before a commit can be created successfully.

---

*Note: An official Scaffolding mechanism to rename this baseline repository for your new microservice will be available soon. Follow the instructions in this README for local development.*
