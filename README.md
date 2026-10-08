# Meeting Scheduler & Coordinator Agent

An AI-assisted meeting scheduling application built with Spring Boot, MySQL, REST APIs, JavaScript and a natural-language scheduling layer.

## Features

- User CRUD
- Meeting CRUD
- Participant management
- Availability management
- Natural-language meeting requests
- Duration/date/time-period extraction
- Common availability detection
- Meeting booking
- Meeting cancellation
- Responsive web dashboard
- Demo data initialization
- Optional SMTP integration point

## 1. Create the MySQL database

```sql
CREATE DATABASE meeting_scheduler;
```

## 2. Configure MySQL

Open:

`src/main/resources/application.properties`

Change:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

## 3. Run

From the project folder:

```bash
mvn clean spring-boot:run
```

Then open:

`http://localhost:8080`

## 4. API examples

### Get users

```bash
curl http://localhost:8080/api/users
```

### Get meetings

```bash
curl http://localhost:8080/api/meetings
```

### AI scheduling request

```bash
curl -X POST http://localhost:8080/api/ai/schedule \
  -H "Content-Type: application/json" \
  -d '{"request":"Schedule a 30-minute project sync tomorrow afternoon","organizerId":1}'
```

### Create a meeting

```bash
curl -X POST http://localhost:8080/api/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Project Sync",
    "description":"Weekly project meeting",
    "meetingDate":"2026-09-24",
    "startTime":"14:00:00",
    "endTime":"14:30:00",
    "organizerId":1,
    "participantIds":[2,3]
  }'
```

## Project structure

```text
meeting-scheduler-agent/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/meetingscheduler/
    │   ├── MeetingSchedulerApplication.java
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── model/
    │   ├── repository/
    │   └── service/
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── style.css
            └── app.js
```

## Important

This version uses a local rule-based NLP scheduler so it runs without an external AI key. For a production/final-year enhanced version, replace `NaturalLanguageService` with an LLM integration and connect Google Calendar/Microsoft Graph for real calendar operations.
