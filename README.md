# 🚀 Feign Microservices Playground

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ff6f00?logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.x-6db33f?logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/OpenFeign-Spring%20Cloud-0a9edc?logo=spring&logoColor=white" alt="OpenFeign" />
  <img src="https://img.shields.io/badge/H2-Database-5b95d1?logo=hive&logoColor=white" alt="H2" />
  <img src="https://img.shields.io/badge/Maven-Multi--Module-c71a36?logo=apachemaven&logoColor=white" alt="Maven" />
</p>

> A compact microservices playground where a product catalog service talks to a stock service via Spring Cloud OpenFeign. Ideal for demos, workshops, and experimenting with synchronous service-to-service calls.

---

## 🧭 Table of Contents
- [Architecture](#-architecture)
- [Highlights](#-highlights)
- [Module Breakdown](#-module-breakdown)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [API Reference](#-api-reference)
- [Observability](#-observability)
- [Testing Guide](#-testing-guide)
- [Roadmap](#-roadmap)

---

## 🏗 Architecture
```mermaid
graph LR
    A[REST Client
    (Postman / cURL / UI)] -->|POST /api/product| B(Product Service)
    B -->|Persist| H[(H2 Database)]
    B -. Feign .-> C(Stock Service)
    C -->|Ack| B
    C -->|"Received product"| A
```
- **Product Service** (port `8097`) exposes REST endpoints, persists data, and acts as a Feign client.
- **Stock Service** (port `8098`) exposes a simple REST endpoint that acknowledges product notifications.
- **Common DTO Module** keeps the payloads in sync between both services.

---

## ✨ Highlights
- ⚡ **Rapid feedback loop:** In-memory H2 keeps iteration instant.
- 🔄 **Service-to-service call:** Spring Cloud OpenFeign handles the client plumbing.
- 🧩 **Shared contracts:** Common DTO module avoids copy-paste models.
- 🧪 **Developer friendly:** Built-in H2 console, curl scripts, and a detailed testing playbook.

---

## 📦 Module Breakdown
| Module | Packaging | Purpose |
| ------ | --------- | ------- |
| `common-dto` | `jar` | Houses `ProductDTO` record shared by both services. |
| `product-microservice` | `jar` | REST API for managing products, uses JPA + H2, calls stock service. |
| `stock-microservice` | `jar` | Receives product notifications and responds with acknowledgements. |
| Root | `pom` | Aggregator, dependency management, shared build plugins. |

---

## 🛠 Tech Stack
| Layer | Technology |
| ----- | ---------- |
| Language | ☕ **Java 21** |
| Framework | 🍃 **Spring Boot 3.5.x** |
| Persistence | 🗄 **Spring Data JPA + H2** |
| HTTP Client | 🔗 **Spring Cloud OpenFeign** |
| Build | 🧱 **Maven mono-repo** with wrapper |
| Testing | ✅ **Spring Boot Starter Test** |

---

## ⚙️ Quick Start
```bash
# Clone & build
git clone <repo-url>
cd feign-microservices
./mvnw clean install

# Terminal A - start stock service first
cd stock-microservice
../mvnw spring-boot:run

# Terminal B - start product service
cd product-microservice
../mvnw spring-boot:run
```
> Ports: Product Service → `8097`, Stock Service → `8098`

📝 **Config switches** live under each service's `src/main/resources/application.properties`. The product service already points its Feign client at `http://localhost:8098`.

---

## 🔌 API Reference
### Product Service (`http://localhost:8097`)
| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| `GET` | `/api/product` | List all products (`ProductDTO[]`). |
| `POST` | `/api/product` | Create product → persists to H2 → notifies stock service → returns saved DTO with `id`. |

**Sample request:**
```json
{
  "name": "Vintage Camera",
  "description": "Collectible 35mm camera",
  "imageURL": "https://example.com/camera.jpg"
}
```

### Stock Service (`http://localhost:8098`)
| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| `POST` | `/api/stock` | Receives `ProductDTO`, responds with `"Received product: <name>"`. |

---

## 🔍 Observability
- 🖥 **H2 Console:** `http://localhost:8097/h2-console` (JDBC URL `jdbc:h2:mem:testdb`, user `sa` / empty password).
- 📜 **Logs:** Stock service prints every Feign request; product service logs the Feign call lifecycle.
- 🪵 **Feign Debugging:** Toggle `logging.level.com.feign.product_microservice.client=DEBUG` for wire-level insight.

---

## ✅ Testing Guide
A full walkthrough (curl scripts, expected responses, troubleshooting tips) lives in [`TESTING_INSTRUCTIONS.md`](TESTING_INSTRUCTIONS.md). Quick cheatsheet:
1. `POST /api/product` → expect saved product with generated `id` and stock service log entry.
2. `GET /api/product` → verify persistence in memory.
3. `POST /api/stock` directly → ensure stock service responds even without the product service.

---

## 🛣 Roadmap
- 🗃 Persist stock data (JPA or reactive store).
- 🛡 Add validation + exception handling for cleaner API responses.
- 📨 Add async messaging (Kafka / RabbitMQ) alongside Feign for comparison.
- 🔐 Secure endpoints with OAuth2 / Keycloak.
- 📦 Docker Compose orchestration for one-command spin-up.

---

> 💡 Have an idea to extend the playground? Open an issue or drop a PR! This repo is meant to be hackable, so feel free to experiment.
