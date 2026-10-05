# Real-time Retail Streaming Platform

Simple Spring MVC implementation of the Insights API for the real-time retail streaming platform case study.

The local application uses an embedded, file-backed H2 database. No Docker, Kafka, AWS account, or external database is needed to run this API.

## Architecture

```text
Client
  |
  v
Spring MVC Controller
  |
  v
Service -> Spring Data JPA -> H2 database
```

Production target:

```text
API Gateway
    |
    v
Spring MVC API
    |
    v
Amazon DynamoDB
```

## Technology Used

- Java 17
- Spring Boot 3.4
- Spring MVC / REST
- Spring Data JPA
- H2 database (local development)
- Maven
- JUnit 5

## API

`GET /ad/{campaignId}/clicks`

Example:

```bash
curl http://localhost:8080/ad/C123/clicks
```

Response:

```json
{
  "campaignId": "C123",
  "clicks": 50234
}
```

## Project Structure

```text
real-time-retail-streaming-platform/
├── .gitignore
├── pom.xml
├── README.md
└── src/
  ├── main/
  │   ├── java/com/retail/streaming/
  │   │   ├── RetailStreamingApplication.java
  │   │   ├── controller/
  │   │   │   └── AdController.java
  │   │   ├── model/
  │   │   │   ├── AdCampaignMetrics.java
  │   │   │   └── AdClicksResponse.java
  │   │   ├── repository/
  │   │   │   └── AdCampaignMetricsRepository.java
  │   │   └── service/
  │   │       └── AdService.java
  │   └── resources/
  │       ├── application.properties
  │       └── data.sql
  └── test/java/com/retail/streaming/service/
    └── AdServiceTest.java
```

## Run

From the Spring application directory (`real-time-retail-streaming-platform`), with Java 17 or newer and Maven installed:

```powershell
java -version
mvn -version
mvn clean test
mvn spring-boot:run
```

On Windows, install a compatible JDK and Maven with WinGet if needed:

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
winget install Apache.Maven
```

The first run downloads dependencies and creates `data/retaildb.mv.db`. Seed campaigns `C123` and `C456` are inserted on startup; their rows can be inspected at `http://localhost:8080/h2-console` using JDBC URL `jdbc:h2:file:./data/retaildb`, user `sa`, and a blank password. Stop the app with `Ctrl+C`.

Try the API from another terminal:

```bash
curl.exe http://localhost:8080/ad/C123/clicks
```

Expected response:

```json
{"campaignId":"C123","clicks":50234}
```

## Test

```bash
mvn test
```

## Local Data Flow

```text
AdController
     |
     v
AdService
     |
  v
AdCampaignMetricsRepository
  |
  v
H2 file database
```
