# Real-time Retail Streaming Platform

<img width="1547" height="1017" alt="5b59d942-2283-471d-ad68-489f21ad0971" src="https://github.com/user-attachments/assets/a7994365-00d8-4768-94cb-eda1530fd29c" />

Scalable real-time retail streaming platform for event processing,
analytics, attribution, and insights APIs.

## Table of Contents

1. [Technologies Used](#technologies-used)
2. [Functional Requirements](#functional-requirements)
3. [Non-Functional Requirements](#non-functional-requirements)
4. [Architecture Overview](#architecture-overview)
5. [Project Structure](#project-structure)
6. [Running Locally](#running-locally)
7. [Deployment](#deployment)

---

## 1. Technologies Used

| Technology | Purpose |
|---|---|
| **Java** | Application and stream-processing development |
| **Apache Kafka** | Durable event streaming and ingestion |
| **Kafka Streams** | Stateful processing, sessionization, windowing and aggregation |
| **Amazon DynamoDB** | Low-latency serving database |
| **Spring Boot** | Insights API |
| **Docker** | Local containerization |
| **Maven** | Build and dependency management |
| **JUnit / Mockito** | Unit testing |
| **REST APIs** | Analytics and insights |

Production deployment can additionally include monitoring, centralized logging, CI/CD and other operational capabilities.

---

## 2. Functional Requirements

### Event Ingestion
- Capture real-time events from retailer websites and applications
- Support ad impressions, ad clicks, product views, searches, add-to-cart and purchase events
- Validate and normalize incoming events
- Support multiple retailers / tenants

### Real-time Processing
- Process events with low latency
- Enrich events with campaign, product and retailer information
- Track user sessions
- Handle duplicate and late events

### Ad Attribution
- Link ad clicks to subsequent actions such as add-to-cart
- Support configurable attribution windows

### Real-time Aggregation
Calculate:
- Impressions
- Clicks
- CTR
- Add-to-cart
- Click-to-basket
- Conversions

Metrics can be aggregated by campaign, product, retailer and time window.

### Data Storage
- Store raw events for replay and audit
- Store processed and aggregated data for fast querying
- Support historical analytics and retention policies

### Insights APIs

```text
GET /ad/{campaignId}/clicks
GET /ad/{campaignId}/impressions
GET /ad/{campaignId}/clickToBasket
```

### Multi-tenancy
- Support multiple retailers
- Ensure tenant data isolation
- Support tenant-level access control and configuration

### Replay & Recovery
- Replay historical events when required
- Reprocess events after business logic changes
- Recover processing state after failures

---

## 3. Non-Functional Requirements

### Scalability
- Horizontally scale ingestion, processing and APIs
- Handle traffic spikes and seasonal peaks

### Latency
Target real-time metric availability within **5–10 seconds** of event ingestion.

### Throughput
The platform should be capable of scaling to **millions of events per second** during peak traffic.

### Availability
Target **99.9%+ availability** for customer-facing APIs.

### Reliability
- Durable event processing
- Retry failed events
- Handle duplicate and out-of-order events
- Avoid permanent event loss

### Consistency & Accuracy
- Use idempotent processing to prevent double counting
- Define acceptable delays for late events
- Maintain consistent aggregation rules

### Security
- Authentication and authorization
- Encryption in transit and at rest
- Tenant-level data isolation

### Observability
- Application and infrastructure metrics
- Logging and alerting
- Kafka consumer lag monitoring

### Disaster Recovery
- Multi-AZ deployment
- Backup and replication
- Defined RPO/RTO

---

## 4. Architecture Overview

```text
Web / Mobile Applications
          |
          v
     API Gateway
          |
          v
    Event Collector
          |
          v
        Kafka
          |
          v
    Kafka Streams
          |
          +---- Stateful Processing
          +---- Sessionization
          +---- Deduplication
          +---- Windowed Aggregations
          +---- Ad Attribution
          |
          v
     Amazon DynamoDB
       Serving DB
          |
          v
      Insights API
          |
          v
       Consumers
```

### End-to-end Flow

**Capture → Ingest → Process → Enrich → Aggregate → Store → API → Insights**

1. Retailer applications generate user and advertising events.
2. API Gateway provides authentication, routing and rate limiting.
3. Event Collector validates, normalizes and enriches incoming events.
4. Kafka provides the durable event backbone.
5. Kafka Streams performs stateful processing and aggregation.
6. DynamoDB stores pre-aggregated metrics for low-latency reads.
7. Insights API exposes campaign-level analytics.

### State Management

Kafka Streams maintains local state for operations such as sessionization, deduplication and attribution.

```text
Kafka
  |
  v
Kafka Streams
  |
  +--> Local State Store (RocksDB)
  |
  v
Aggregated Metrics
  |
  v
DynamoDB
```

---

## 5. Project Structure

```text
real-time-retail-streaming-platform/
├── README.md
└── real-time-retail-streaming-platform/
  ├── .gitignore
  ├── pom.xml
  ├── README.md
  └── src/
    ├── main/
    │   ├── java/com/retail/streaming/
    │   │   ├── RetailStreamingApplication.java
    │   │   ├── controller/AdController.java
    │   │   ├── model/
    │   │   │   ├── AdCampaignMetrics.java
    │   │   │   └── AdClicksResponse.java
    │   │   ├── repository/AdCampaignMetricsRepository.java
    │   │   └── service/AdService.java
    │   └── resources/
    │       ├── application.properties
    │       └── data.sql
    └── test/java/com/retail/streaming/service/
      └── AdServiceTest.java
```

The H2 database files are created under the application directory's `data/` folder at runtime and are excluded from version control.

The Spring Boot application demonstrates:

```text
GET /ad/{campaignId}/clicks
```

using:

```text
Controller → Service → Repository
```

---

## 6. Running Locally

The current Spring API uses an embedded, file-backed H2 database, so no Docker, Kafka, or external database is required for this local demo. Ensure Java 17 or newer and Maven are installed, then run these commands from the repository root:

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
winget install Apache.Maven
cd real-time-retail-streaming-platform
java -version
mvn -version
mvn clean test
mvn spring-boot:run
```

The app listens on port 8080 and persists local data in `real-time-retail-streaming-platform/data/retaildb.mv.db`. It seeds campaigns `C123` and `C456` on startup. Query a sample campaign from another terminal:

```powershell
curl.exe http://localhost:8080/ad/C123/clicks
```

Example:

```text
GET /ad/C123/clicks
```

Response:

```json
{
  "campaignId": "C123",
  "clicks": 50234
}
```

---

## 7. Deployment

The production solution is designed for deployment on AWS.

```text
                         AWS
                          |
        +-----------------+------------------+
        |                 |                  |
   API Gateway         MSK / Kafka       DynamoDB
        |                 |                  |
        v                 v                  |
   Event Collector → Kafka Streams ----------+
        |                 |
        +-------- ECS / EKS ---------------+
                          |
                    Insights API
```

- **API Gateway** — API entry point, authentication and rate limiting
- **MSK / Kafka** — managed event streaming
- **ECS/EKS** — deploy Event Collector, Kafka Streams and Insights API
- **DynamoDB** — scalable serving database
- **Multi-AZ** — high availability and resilience
- **CloudWatch** — logs, metrics and alerts

---

## Summary

The platform follows an event-driven architecture:

```text
Events
  ↓
API Gateway
  ↓
Event Collector
  ↓
Kafka
  ↓
Kafka Streams
  ↓
DynamoDB
  ↓
Insights API
```

**Core principle:**

> Capture events once, process them continuously, maintain state where required, pre-aggregate metrics, and expose low-latency insights through APIs.


``` bash
mvn test
```

------------------------------------------------------------------------
