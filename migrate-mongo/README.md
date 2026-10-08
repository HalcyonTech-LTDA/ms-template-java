# Migrate-Mongo - Database Migrations

This directory contains the database migration scripts, configuration, and Docker infrastructure using [**migrate-mongo**](https://www.npmjs.com/package/migrate-mongo) to manage schema evolution, indexes, and data transformations for MongoDB in the application.

In production and cloud environments, Spring Data MongoDB's automatic index creation is disabled (`SPRING_MONGODB_AUTO_INDEX_CREATION=false`) in favor of declarative, versioned migration scripts that run deterministically before application startup.

---

## 1. Directory Structure

```text
migrate-mongo/
├── .dockerignore             # Excludes node_modules from container build context
├── Dockerfile.migrate-mongo  # Dockerfile to build an immutable, standalone migration image
├── README.md                 # Documentation and usage guide
├── migrate-mongo-config.js   # Central migrate-mongo configuration (URIs, DB names, locks)
├── migrations/               # Versioned migration scripts (up and down functions)
│   └── 20261007180928-add_indexes.js
├── package-lock.json         # Pinned npm dependency tree
└── package.json              # Node.js manifest and migration lifecycle scripts
```

---

## 2. Managed Migrations & Indexes

Migrations are executed in sequential timestamp order. Each script exports an `up(db, client)` and a `down(db, client)` asynchronous function.

### Active Migrations

| Migration Script | Description | Collections Affected | Indexes / Changes |
| :--- | :--- | :--- | :--- |
| [`20261007180928-add_indexes.js`](./migrations/20261007180928-add_indexes.js) | Initial production indexes for domain entities | `customers`, `orders` | • `customers`: `{ email: 1 }` (unique)<br>• `orders`: `{ status: 1, createdAt: 1 }` (compound) |

### Changelog & Locking

- **Changelog Collection (`changelog`)**: Records every migration applied, its execution timestamp, and batch block.
- **Lock Collection (`changelog_lock`)**: Guarantees distributed mutual exclusion so concurrent migration jobs or replica restarts do not run migrations simultaneously.

---

## 3. How to Run

### Option A: Via Taskfile (Recommended)

The project [`Taskfile.yaml`](../Taskfile.yaml) provides automated tasks that run natively if Node.js is present or fallback to Docker Compose:

```bash
# Check status of applied vs pending migrations
task migrate:status

# Execute all pending migrations
task migrate:up

# Rollback the last applied migration
task migrate:down

# Create a new migration file with timestamp
task migrate:create -- add_order_indexes
```

### Option B: Local Development via Docker Compose

The `migrate-mongo` service is pre-configured in [`docker-compose.yaml`](../docker-compose.yaml) with live volume mounts:

```bash
# Check migration status
docker compose run --rm migrate-mongo status

# Apply pending migrations
docker compose run --rm migrate-mongo up

# Rollback last migration
docker compose run --rm migrate-mongo down

# Create a new migration script (synced directly to host migrations/ directory)
docker compose run --rm migrate-mongo create add_product_indexes
```

Or run dedicated Taskfile Docker shortcuts:

```bash
task docker:migrate:status
task docker:migrate
task docker:migrate:down
```

### Option C: Build and Run Standalone Container (Immutable Image for CI/CD)

Use [`Dockerfile.migrate-mongo`](./Dockerfile.migrate-mongo) to build a standalone, self-contained container image bundling the configuration and migration scripts:

```bash
# Build the migration image from the project root:
docker build -f migrate-mongo/Dockerfile.migrate-mongo -t ms-migrate-mongo:latest migrate-mongo

# Check migration status against running MongoDB:
docker run --rm --network template-java-network \
  -e SPRING_MONGODB_URI="mongodb://mongodb:27017/example_db" \
  ms-migrate-mongo:latest status

# Execute pending migrations (default container command is 'up'):
docker run --rm --network template-java-network \
  -e SPRING_MONGODB_URI="mongodb://mongodb:27017/example_db" \
  ms-migrate-mongo:latest
```

### Option D: Direct Node.js / npm Execution

From within the `migrate-mongo/` directory:

```bash
cd migrate-mongo

# Install dependencies
npm ci

# Run migration operations
npm run migrate:status
npm run migrate:up
npm run migrate:down
npm run migrate:create -- <migration_name>
```

---

## 4. Environment Variables Reference

The configuration in [`migrate-mongo-config.js`](./migrate-mongo-config.js) dynamically resolves MongoDB connection settings with sensible defaults:

| Variable | Description | Default Fallback |
| :--- | :--- | :--- |
| `MIGRATE_MONGO_URI` | Explicit MongoDB connection string for migrations | `SPRING_DOCKER_MONGODB_URI` / `SPRING_MONGODB_URI` / `mongodb://localhost:27017` |
| `SPRING_DOCKER_MONGODB_URI` | Container-to-container connection URI in Docker Compose | `mongodb://mongodb:27017/example_db` |
| `SPRING_MONGODB_URI` | Standard Spring Boot MongoDB connection URI from `.env` | `mongodb://localhost:27017/example_db` |
| `MIGRATE_MONGO_DATABASE` | Target database name | Parsed from URI path, or `SPRING_MONGODB_DATABASE`, or `example_db` |
| `SPRING_MONGODB_DATABASE` | Target database name defined in Spring Boot configuration | `example_db` |

---

## 5. Verification with `mongosh`

You can verify the applied indexes and migration records directly inside MongoDB:

```bash
# 1. Connect to MongoDB container with mongosh
docker exec -it mongodb mongosh example_db

# 2. View applied migration history
db.changelog.find().pretty()

# 3. Verify indexes on customers collection
db.customers.getIndexes()

# 4. Verify indexes on orders collection
db.orders.getIndexes()
```

---

## 6. Creating New Migrations

To add a new migration:

1. Run:
   ```bash
   task migrate:create -- <description>
   ```
   This generates a new file in `migrations/<timestamp>-<description>.js`.

2. Implement the `up` and `down` methods:
   ```javascript
   module.exports = {
     async up(db, client) {
       await db.collection('customers').createIndex({ documentNumber: 1 }, { unique: true });
     },

     async down(db, client) {
       await db.collection('customers').dropIndex("documentNumber_1");
     }
   };
   ```

3. Validate the migration locally:
   ```bash
   task migrate:status
   task migrate:up
   task migrate:status
   ```
