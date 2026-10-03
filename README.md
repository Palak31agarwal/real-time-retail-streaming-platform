# Real-time Retail Streaming Platform

`<img width="1547" height="1017" alt="Real-time Retail Streaming Platform Architecture" src="https://github.com/user-attachments/assets/47b6794c-b364-4e47-b8b2-296c7fadb3bf" />`{=html}

Scalable real-time retail streaming platform for event processing,
analytics, attribution, and insights APIs.

## Table of Contents

1.  [Technologies Used](#technologies-used)
2.  [Functional Requirements](#functional-requirements)
3.  [Non-Functional Requirements](#non-functional-requirements)
4.  [Architecture Overview](#architecture-overview)
5.  [Event Processing](#event-processing)
6.  [Kafka Topics & Partitioning](#kafka-topics--partitioning)
7.  [State Management](#state-management)
8.  [Ad Attribution](#ad-attribution)
9.  [Data Storage](#data-storage)
10. [Insights APIs](#insights-apis)
11. [Scalability & Resilience](#scalability--resilience)
12. [Security](#security)
13. [Observability & Monitoring](#observability--monitoring)
14. [Trade-offs & Design Decisions](#trade-offs--design-decisions)
15. [Project Structure](#project-structure)
16. [Running Locally](#running-locally)
17. [Future Enhancements](#future-enhancements)

------------------------------------------------------------------------

## 1. Technologies Used

  -----------------------------------------------------------------------
  Technology                          Purpose
  ----------------------------------- -----------------------------------
  **Java**                            Application and stream-processing
                                      development

  **Apache Kafka**                    Durable, distributed event
                                      streaming and event ingestion

  **Kafka Streams**                   Stateful stream processing,
                                      sessionization, windowing,
                                      aggregation and attribution

  **RocksDB**                         Local persistent state store used
                                      by Kafka Streams

  **Apache Cassandra**                Horizontally scalable, low-latency
                                      serving database

  **Docker**                          Containerization

  **Docker Compose**                  Local development and
                                      infrastructure dependencies

  **Maven**                           Build and dependency management

  **JUnit**                           Unit and integration testing

  **REST APIs**                       Real-time analytics and insights

  **OpenAPI / Swagger**               API documentation

  **Prometheus**                      Metrics collection

  **Grafana**                         Monitoring and visualization

  **GitHub Actions**                  CI/CD automation
  -----------------------------------------------------------------------

> Production concerns such as Prometheus/Grafana, multi-region
> deployment and automated CI/CD may be architectural considerations
> even when not fully implemented in the local demo.

------------------------------------------------------------------------

## 2. Functional Requirements

### 2.1 Event Ingestion

-   Capture real-time events from retailer websites and applications
-   Support ad impression, ad click, product view, search, add-to-cart
    and purchase events
-   Validate and normalize incoming events
-   Support multiple retailers / tenants

### 2.2 Real-time Event Processing

-   Process events with low latency
-   Enrich events with campaign, product, user and retailer information
-   Track user sessions across multiple events
-   Handle duplicate, late and out-of-order events

### 2.3 Ad Attribution

-   Link ad clicks to subsequent actions such as add-to-cart
-   Support configurable attribution windows
-   Support different attribution models where required

### 2.4 Real-time Aggregation

Calculate:

-   Impressions
-   Clicks
-   CTR
-   Add-to-cart
-   Click-to-basket
-   Conversions

Aggregate metrics by campaign, product, retailer / tenant and time
window.

### 2.5 Data Storage

-   Store raw events for audit and replay
-   Store processed and aggregated data for fast querying
-   Support historical analytics
-   Apply retention and archival policies

### 2.6 Insights APIs

-   `GET /ad/{campaignId}/clicks`
-   `GET /ad/{campaignId}/impressions`
-   `GET /ad/{campaignId}/clickToBasket`
-   Support time-range filtering
-   Support real-time and historical data
-   Support pagination and filtering
-   Provide high-performance and reliable responses

### 2.7 Multi-tenancy

-   Support multiple retailers on the same platform
-   Ensure tenant data isolation
-   Support tenant-specific configuration
-   Provide tenant-level access control and security

### 2.8 Replay & Recovery

-   Allow historical event replay
-   Reprocess events when business logic changes
-   Recover processing state after failures

### 2.9 Monitoring & Configuration

-   Expose platform health and business metrics
-   Generate alerts for failures, high latency and consumer lag
-   Support configuration of attribution windows, retention and other
    business rules

------------------------------------------------------------------------

## 3. Non-Functional Requirements

### 3.1 Scalability

-   Horizontally scale ingestion, processing and APIs
-   Handle high traffic spikes and seasonal peaks

### 3.2 Latency

-   Target real-time metric availability within **5--10 seconds** from
    event ingestion

### 3.3 Throughput

-   Support high-volume event streams
-   Target the ability to scale to millions of events per second during
    peak periods

### 3.4 Availability

-   Target **99.9%+ availability** for customer-facing APIs

### 3.5 Reliability

-   Avoid permanent event loss
-   Support retries, durable queues and recovery
-   Handle duplicate and out-of-order events

### 3.6 Durability

-   Store raw events durably
-   Support replay for the defined retention period

### 3.7 Consistency & Accuracy

-   Prevent double counting through idempotent processing
-   Define acceptable consistency between real-time and historical data

### 3.8 Fault Tolerance & Resilience

-   Continue operating during component failures
-   Support automatic recovery
-   Handle backpressure
-   Support graceful degradation

### 3.9 Security

-   Authentication and authorization
-   Encryption in transit and at rest
-   Secure APIs and data access

### 3.10 Data Isolation

-   Strict tenant data isolation
-   Prevent cross-tenant access
-   Support tenant-level resource allocation and quotas

### 3.11 Privacy & Compliance

-   Minimize and secure customer identifiers
-   Comply with applicable data privacy regulations

### 3.12 Observability

-   Centralized logging, metrics and tracing
-   Monitoring and alerting
-   Distributed tracing for end-to-end event flow

### 3.13 Maintainability

-   Modular, loosely coupled components
-   Versioned APIs and event schemas
-   Easy addition of new event types and metrics

### 3.14 Extensibility

-   Support new event types, metrics and retailers
-   Support configurable business rules

### 3.15 Cost Efficiency

-   Autoscaling where possible
-   Tiered storage and retention policies
-   Monitor and optimize infrastructure costs

### 3.16 Disaster Recovery

-   Multi-AZ / multi-region deployment where required
-   Defined RPO and RTO
-   Backup and replication

------------------------------------------------------------------------

## 4. Architecture Overview

``` text
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
       Cassandra
      Serving DB
          |
          v
      Insights API
          |
          v
       Consumers
```

**End-to-end flow:**

**Capture → Ingest → Process → Enrich → Aggregate → Store → API →
Insights**

1.  Retailer applications generate user and advertising events.
2.  API Gateway provides the entry point for authentication, routing and
    rate limiting.
3.  Event Collector validates, normalizes and enriches incoming events.
4.  Kafka provides the durable event backbone.
5.  Kafka Streams performs stateful processing and aggregation.
6.  Cassandra stores pre-aggregated metrics for low-latency reads.
7.  Insights API exposes campaign-level analytics.

------------------------------------------------------------------------

## 5. Event Processing

A typical event contains:

``` json
{
  "eventId": "evt-12345",
  "eventType": "AD_CLICK",
  "tenantId": "retailer-001",
  "userId": "user-123",
  "sessionId": "session-456",
  "campaignId": "campaign-789",
  "productId": "product-101",
  "eventTimestamp": "2026-10-03T12:00:00Z"
}
```

Processing stages:

1.  Validation
2.  Schema validation
3.  Normalization
4.  Deduplication
5.  Enrichment
6.  Event-time processing
7.  Stateful processing
8.  Aggregation
9.  Attribution
10. Persistence

------------------------------------------------------------------------

## 6. Kafka Topics & Partitioning

Example topics:

``` text
retail-events
ad-events
product-events
purchase-events
processed-events
```

For user-level stateful processing, events can be keyed using `userId`.

``` text
userId = U123
        |
        v
Kafka partition
        |
        v
Kafka Streams task
        |
        v
Local state store
```

Using the same key helps route events for the same user consistently,
maintaining ordering and local state locality.

Partitioning can also be designed around `tenantId`, `campaignId` or
`sessionId` depending on ordering and state requirements.

------------------------------------------------------------------------

## 7. State Management

Kafka Streams requires local state for:

-   Sessionization
-   Deduplication
-   Click-to-basket attribution
-   Windowed aggregations

Kafka Streams can use **RocksDB** as a local persistent state store.

Example:

``` text
User: U123

lastAdClick:
  campaignId: C456
  productId: P789
  timestamp: 10:05

sessionId: S1001
```

When an `ADD_TO_CART` event arrives for the same user and product, the
stream processor can look up the recent click state and determine
whether it qualifies for attribution.

State can be backed by Kafka changelog topics so it can be reconstructed
after a failure.

> RocksDB is processing state, not the permanent customer database.

------------------------------------------------------------------------

## 8. Ad Attribution

Example:

``` text
10:05  AD_CLICK
       |
       | campaign = C456
       | product  = P789
       |
       v
10:12  ADD_TO_CART
       |
       v
Click-to-Basket Attribution
```

A basic attribution rule can be:

``` text
Same user
+ Same product
+ Valid attribution window
= Attributed add-to-cart
```

Example:

``` text
Attribution Window = 30 minutes
```

The platform should define behavior for multiple clicks, multiple
products, cross-device identity, duplicate events, late events and
out-of-order events.

------------------------------------------------------------------------

## 9. Data Storage

  Storage                 Purpose
  ----------------------- ------------------------------------------------
  Kafka                   Durable event stream and replay
  RocksDB                 Local Kafka Streams processing state
  Cassandra               Low-latency serving and pre-aggregated metrics
  Data Lake / Warehouse   Long-term historical analytics

### Serving Database

Cassandra is used for predictable, low-latency access patterns.

Example logical record:

``` text
tenantId       = retailer-001
campaignId     = C123
timeBucket     = 2026-10-03-18:00

impressions    = 245000
clicks         = 12450
clickToBasket  = 1230
```

The Insights API reads pre-aggregated values rather than processing the
raw event stream for every request.

------------------------------------------------------------------------

## 10. Insights APIs

### Get campaign clicks

``` http
GET /ad/{campaignId}/clicks
```

### Get campaign impressions

``` http
GET /ad/{campaignId}/impressions
```

### Get campaign click-to-basket

``` http
GET /ad/{campaignId}/clickToBasket
```

Example response:

``` json
{
  "campaignId": "C123",
  "clicks": 50000
}
```

The API layer can support time-range filtering, pagination, tenant-aware
authorization, real-time and historical query routing, rate limiting and
request validation.

------------------------------------------------------------------------

## 11. Scalability & Resilience

### Kafka

-   Increase partitions to scale event processing
-   Replicate topics across brokers
-   Use consumer groups for parallel processing
-   Monitor consumer lag

### Kafka Streams

-   Scale application instances horizontally
-   Increase stream partitions and tasks
-   Use local state stores
-   Recover state through Kafka changelog topics

### Cassandra

-   Scale nodes horizontally
-   Replicate data across availability zones
-   Use appropriate partition keys and data modeling

### Failure handling

``` text
Producer
   |
   v
Kafka
   |
   v
Kafka Streams
   |
   +---- Failure
   |
   v
Changelog / Kafka
   |
   v
State Recovery
```

The system should support retries, backpressure, graceful degradation
and recovery without permanently losing events.

------------------------------------------------------------------------

## 12. Security

Security controls include:

-   TLS for data in transit
-   Encryption at rest
-   Authentication and authorization
-   OAuth2 / JWT where applicable
-   Role-based access control
-   Kafka ACLs
-   Tenant-level authorization
-   API rate limiting
-   Secrets management
-   PII minimization

Tenant identity should be propagated through the request and
event-processing pipeline so that data access can be enforced
consistently.

------------------------------------------------------------------------

## 13. Observability & Monitoring

### Platform metrics

-   Event ingestion rate
-   Kafka consumer lag
-   Processing latency
-   API latency
-   API error rate
-   Cassandra read/write latency
-   CPU and memory utilization

### Business metrics

-   Impressions
-   Clicks
-   CTR
-   Add-to-cart
-   Click-to-basket
-   Conversion rate

### Observability stack

``` text
Applications
     |
     +---- Logs
     +---- Metrics
     +---- Traces
             |
             v
       Observability
       Prometheus
       Grafana
```

Distributed tracing can follow an event from ingestion through
processing and persistence.

------------------------------------------------------------------------

## 14. Trade-offs & Design Decisions

### Kafka Streams vs Apache Flink

Kafka Streams is chosen because the platform is Kafka-centric and the
processing requirements are primarily stateful processing,
sessionization, windowing, deduplication, aggregation and attribution.

Flink would become more attractive if the platform evolved toward
significantly more complex event processing or heterogeneous streaming
sources.

### Cassandra vs DynamoDB

Cassandra is selected to keep the core architecture cloud-neutral while
providing horizontal scalability, high write throughput, low-latency
reads, distributed availability and predictable query patterns.

DynamoDB would be a valid alternative for an AWS-specific deployment.

### Pre-aggregation

Pre-aggregating metrics improves API latency and reduces query cost, but
reduces flexibility for arbitrary analytical queries.

### Event-time processing

Processing based on event timestamps improves correctness when events
arrive late or out of order, at the cost of additional state and
processing complexity.

### Exactly-once processing

Exactly-once semantics can reduce duplicate results, but introduce
additional complexity and should be considered together with the
transactional guarantees of downstream systems.

### Multi-region deployment

Multi-region deployment improves disaster recovery but increases cost
and operational complexity. The initial architecture can use multi-AZ
deployment and introduce multi-region DR based on business RPO/RTO
requirements.

------------------------------------------------------------------------

## 15. Project Structure

``` text
real-time-retail-streaming-platform/
|
+-- README.md
|
+-- architecture/
|   +-- hld.drawio
|   +-- architecture.md
|
+-- src/
|   +-- main/
|   +-- test/
|
+-- api/
|   +-- openapi.yaml
|
+-- docker/
|   +-- docker-compose.yml
|
+-- docs/
|
+-- pom.xml
|
+-- .github/
    +-- workflows/
```

------------------------------------------------------------------------

## 16. Running Locally

Start the local infrastructure:

``` bash
docker compose up
```

Expected infrastructure:

``` text
Kafka
Cassandra
Kafka Streams Application
Insights API
```

Build the Java application:

``` bash
mvn clean install
```

Run tests:

``` bash
mvn test
```

------------------------------------------------------------------------

## 17. Future Enhancements

-   Historical query routing through a data warehouse
-   Data lake integration
-   Multi-region active-active deployment
-   Advanced attribution models
-   Schema Registry and automated schema compatibility checks
-   Automated load testing
-   Distributed tracing
-   Advanced tenant-level quotas
-   Autoscaling based on Kafka consumer lag
-   Data retention and tiered storage
-   Real-time anomaly detection

------------------------------------------------------------------------

## Summary

The platform provides a scalable event-driven architecture for real-time
retail and advertising analytics.

``` text
Events
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
  +--> Stateful Processing / RocksDB
  |
  v
Cassandra
  |
  v
Insights APIs
  |
  v
Real-time Analytics
```

**Core design principle:**

> Capture events once, process them continuously, maintain state where
> required, pre-aggregate metrics, and expose low-latency insights
> through APIs.
