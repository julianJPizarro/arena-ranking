# Arena Ranking

REST API for tracking eSports teams and their match history. Backend case study — see `../brief.md` for the full pedagogical context.

## Stack

Java 21 · Spring Boot 4 · Spring Data JPA · PostgreSQL · Flyway · Lombok · MapStruct · springdoc-openapi · Testcontainers

## Requirements

- JDK 21+
- Maven 3.9+
- Docker (for local PostgreSQL and for Testcontainers-based integration tests)

## Running in development

1. Start the database:

   ```bash
   docker-compose up -d
   ```

2. Run the application:

   ```bash
   mvn spring-boot:run
   ```

3. The API is available at `http://localhost:8080`.

4. Interactive documentation (Swagger UI): `http://localhost:8080/swagger-ui.html`.

## Running tests

```bash
mvn test
```

- Repository integration tests (`*RepositoryIT`) and end-to-end flow tests (`*FlowIT`) automatically start a PostgreSQL container via Testcontainers (requires Docker running).
- Service unit tests (`*ServiceTest`) run with Mockito and don't require Docker or a Spring context.

## Main endpoints

| Method | Path | Description |
| --- | --- | --- |
| POST | `/teams` | Create team |
| GET | `/teams?region=&page=&size=` | List teams (paginated, filter by region) |
| GET | `/teams/{id}` | Get team |
| PUT | `/teams/{id}` | Update team |
| DELETE | `/teams/{id}` | Delete team |
| GET | `/teams/{id}/stats` | Team stats and win rate |
| GET | `/teams/{id}/streak` | Team's current winning streak |
| POST | `/teams/{id}/matches` | Register match |
| GET | `/teams/{id}/matches?tournament=` | List team's matches (paginated, filter) |
| PUT | `/teams/{id}/matches/{matchId}` | Update match |
| DELETE | `/teams/{id}/matches/{matchId}` | Delete match |
