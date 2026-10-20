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

### Error responses

All errors use a common JSON body (`status`, `error`, `message`, `timestamp`). Internal details such as SQL, paths or stack traces are never returned; they are logged server-side only.

- `400 Bad Request` - `campaignId` is not 1-64 characters of letters, digits, `-` or `_`
- `404 Not Found` - no metrics exist for the campaign
- `405 Method Not Allowed` - any method other than `GET`
- `503 Service Unavailable` - database access failure
- `500 Internal Server Error` - any other unexpected error

Example:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Campaign not found: C999",
  "timestamp": "2026-10-20T10:15:30.123Z"
}
```

## Project Structure

```text
real-time-retail-streaming-platform/
├── .dockerignore
├── .gitignore
├── Dockerfile
├── pom.xml
├── README.md
└── src/
  ├── main/
  │   ├── java/com/retail/streaming/
  │   │   ├── RetailStreamingApplication.java
  │   │   ├── controller/
  │   │   │   └── AdController.java
  │   │   ├── exception/
  │   │   │   ├── CampaignNotFoundException.java
  │   │   │   ├── ErrorResponse.java
  │   │   │   ├── GlobalExceptionHandler.java
  │   │   │   └── InvalidCampaignIdException.java
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
  └── test/java/com/retail/streaming/
    ├── controller/AdControllerTest.java
    └── service/AdServiceTest.java
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

## Run with Docker

Only Docker is required (no local JDK or Maven). The multi-stage `Dockerfile` builds the jar with Maven and runs it on a Java 17 JRE as a non-root user.

```bash
docker build -t retail-streaming-api .
docker run --rm -p 8080:8080 -v retail-data:/app/data retail-streaming-api
curl http://localhost:8080/ad/C123/clicks
```

The named volume `retail-data` persists the H2 database (`/app/data`) between container runs.

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
