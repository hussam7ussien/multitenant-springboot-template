# 📌 Multi-Tenant Template

This project is a **Spring Boot 3** starter template designed with **API-First development** and **multi-tenancy support**, integrating both **PostgreSQL** and **MongoDB (NoSQL)** for persistence, with built-in **logging**.

---

## 🚀 Features

- **API First Development**
  - OpenAPI/Swagger contracts are the source of truth.
  - Controllers are generated from the API specification.

- **Multi-Tenancy**
  - Tenant resolution via `X-Tenant-ID` HTTP header.
  - Each tenant can have its own PostgreSQL & MongoDB connections.
  - Context is managed via a `TenantContext` and Spring `Filter`.

- **Persistence**
  - **PostgreSQL**: relational data storage (per-tenant DBs).
  - **MongoDB**: NoSQL for logs and unstructured data.

- **Logging**
  - All tenant actions are logged into MongoDB collections.
  - Built-in `LogService` to track domain events.

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA (Postgres)**
- **Spring Data MongoDB**
- **OpenAPI Generator**
- **Lombok**
- **Docker (for local Postgres/Mongo)**

---

## ⚙️ Project Structure

```
src/main/java/com/multitenant/template
│
├── config/                # Spring configuration (DBs, filters, beans)
├── controller/            # REST controllers (API First)
├── entity/                # JPA & Mongo entities
├── filter/                # Tenant filter (resolve tenant per request)
├── repository/            # Repositories for Postgres & Mongo
├── services/              # Business logic & logging
└── tenant/                # Tenant context & tenant model
```

---

## 🔑 Multi-Tenant Flow

```mermaid
sequenceDiagram
    participant Client
    participant Filter
    participant TenantContext
    participant Postgres
    participant MongoDB

    Client->>Filter: Request + X-Tenant-ID
    Filter->>TenantContext: Resolve Tenant
    TenantContext->>Postgres: Connect to tenant DB
    TenantContext->>MongoDB: Connect to tenant logs DB
    Postgres-->>Client: Response Data
    MongoDB-->>Client: Log Entry Created
```

---

## 🗄️ Example Configuration

```yaml
tenants:
  tenant1:
    postgresConnection:
      url: jdbc:postgresql://localhost:5432/tenant1
      username: tenant1_user
      password: secret
    mongoConnection:
      uri: mongodb://tenant1:pass1@localhost:27017/tenant1?authSource=tenant1

  tenant2:
    postgresConnection:
      url: jdbc:postgresql://localhost:5432/tenant2
      username: tenant2_user
      password: secret
    mongoConnection:
      uri: mongodb://tenant2:pass2@localhost:27017/tenant2?authSource=tenant2
```

---

## 📖 API Example

### Register User
**Request**
```http
POST /api/v1/users
X-Tenant-ID: tenant1
Content-Type: application/json

{
  "name": "John Doe"
}
```

**Response**
```json
{
  "id": 1,
  "name": "John Doe"
}
```

👉 A log entry is automatically written to MongoDB for this action.

---

## 📝 Development Notes

- Use `X-Tenant-ID` in **every request**.
- Logs are separated per tenant in MongoDB.
- Extend `LogService` to log domain-specific events.
- Extend tenant configurations via `application.yml`.

---

## 🐳 Running with Docker

- **Postgres**
```bash
docker run --name postgres -e POSTGRES_PASSWORD=secret -p 5432:5432 -d postgres:15
```

- **MongoDB**
```bash
docker run --name mongo   -e MONGO_INITDB_ROOT_USERNAME=admin   -e MONGO_INITDB_ROOT_PASSWORD=admin   -p 27017:27017 -d mongo:8
```

---

## 🗂️ Multi-Tenant DB Mapping

```mermaid
graph TD
    A[Client Request] -->|X-Tenant-ID=tenant1| B[Postgres tenant1 DB]
    A -->|X-Tenant-ID=tenant1| C[Mongo tenant1 Logs]
    A -->|X-Tenant-ID=tenant2| D[Postgres tenant2 DB]
    A -->|X-Tenant-ID=tenant2| E[Mongo tenant2 Logs]
```

---

## ✅ Roadmap

- [ ] Add Flyway migrations per tenant (Postgres).
- [ ] Extend API contracts with user authentication.
- [ ] Implement centralized logging and monitoring.
- [ ] Add testcontainers for integration testing.
