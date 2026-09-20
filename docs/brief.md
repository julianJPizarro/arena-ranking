# Caso de Estudio: Arena Ranking — Plataforma de Seguimiento de Torneos eSports

## 1. Contexto y objetivos de aprendizaje

Este caso de estudio está diseñado para un curso universitario de desarrollo web, con **enfoque exclusivo en backend**. Los estudiantes construirán la API REST de **Arena Ranking**, una plataforma que permite a organizadores de torneos de eSports registrar equipos y llevar el historial de sus partidos, calculando estadísticas como el porcentaje de victorias (win rate).

El caso se mantiene deliberadamente acotado a **dos tablas relacionadas 1:N**, de modo que el esfuerzo del curso se concentre en dominar el flujo completo de desarrollo backend (modelado, persistencia, API REST, documentación, pruebas) en lugar de en la complejidad del dominio.

**Objetivos de aprendizaje:**

- Modelar una relación uno-a-muchos (1:N) en una base de datos relacional y versionar el esquema con migraciones.
- Implementar una API REST siguiendo buenas prácticas (capas, DTOs, validaciones, manejo de errores, paginación y filtros).
- Persistir datos con Spring Data JPA sobre PostgreSQL.
- Escribir pruebas de integración realistas usando una base de datos real en contenedor (Testcontainers).
- Documentar una API REST de forma estándar con OpenAPI.
- Practicar el flujo de trabajo profesional: control de versiones, estructura de proyecto, separación de responsabilidades.

## 2. Descripción del dominio y modelo entidad-relación

El dominio: un organizador de torneos gestiona **equipos** participantes y registra los **partidos** que cada equipo disputa a lo largo de distintos torneos.

**Relación:** Un equipo (1) puede tener muchos partidos (N). Cada partido pertenece a un único equipo (se registra desde la perspectiva de ese equipo, no como una tabla de enfrentamientos entre dos equipos registrados — esto evita introducir una relación M:N y mantiene el caso en 1:N).

### Tabla `equipos`

| Columna | Tipo | Notas |
| --- | --- | --- |
| id | BIGSERIAL PK | Identificador autogenerado |
| nombre | VARCHAR(100) | Único, no nulo |
| tag | VARCHAR(10) | Sigla corta del equipo (ej. "TLN") |
| region | VARCHAR(50) | Ej. "LATAM", "EU", "NA" |
| logo_url | VARCHAR(255) | Opcional |
| fecha_fundacion | DATE | Opcional |
| creado_en | TIMESTAMP | Default now() |

### Tabla `partidos`

| Columna | Tipo | Notas |
| --- | --- | --- |
| id | BIGSERIAL PK | Identificador autogenerado |
| equipo_id | BIGINT FK | Referencia a `equipos(id)`, no nulo |
| rival | VARCHAR(100) | Nombre del equipo rival (texto libre) |
| torneo | VARCHAR(100) | Nombre del torneo/copa |
| marcador_equipo | INT | Marcador del equipo propio |
| marcador_rival | INT | Marcador del rival |
| resultado | VARCHAR(10) | Calculado o enum: VICTORIA / DERROTA / EMPATE |
| fecha_partido | TIMESTAMP | No nula |
| creado_en | TIMESTAMP | Default now() |

**Relación 1:N:** `partidos.equipo_id → equipos.id`, con `ON DELETE CASCADE` (al eliminar un equipo se eliminan sus partidos) — decisión pedagógica útil para discutir integridad referencial en clase.

## 3. Requisitos funcionales

### Equipos

- Crear, listar, consultar por id, actualizar y eliminar equipos (CRUD completo).
- Validar que `nombre` y `tag` sean obligatorios y que `nombre` sea único.
- Listado paginado con filtro por `region` (`GET /equipos?region=LATAM&page=0&size=10`).

### Partidos

- Registrar un partido asociado a un equipo existente.
- Listar los partidos de un equipo específico, de forma paginada y con filtro por `torneo` (`GET /equipos/{id}/partidos?torneo=Copa2026&page=0&size=10`).
- Actualizar y eliminar un partido.
- Al crear/actualizar un partido, calcular automáticamente `resultado` a partir de los marcadores (regla de negocio en el servicio).

### Estadísticas (valor pedagógico de la relación 1:N)

- Endpoint `GET /equipos/{id}/estadisticas` que devuelva: total de partidos, victorias, derrotas, empates y win rate (%). Esto obliga a los estudiantes a trabajar con agregaciones sobre la colección de partidos de un equipo (vía JPQL/Query Methods o cálculo en el servicio).
- Endpoint `GET /equipos/{id}/racha` que devuelva la racha actual de victorias consecutivas del equipo (calculada a partir de los partidos más recientes ordenados por `fecha_partido`).
- (Opcional) Endpoint de ranking general `GET /equipos/ranking` ordenado por win rate.

### Documentación de la API

- Exponer la documentación interactiva de la API con OpenAPI (springdoc-openapi), incluyendo descripción de todos los endpoints, DTOs y códigos de respuesta.

## 4. Stack técnico y justificación

| Componente | Tecnología | Justificación pedagógica |
| --- | --- | --- |
| Lenguaje/Runtime | Java 21 | LTS reciente, permite mostrar features modernas (records, pattern matching) en DTOs y lógica de negocio |
| Framework backend | Spring Boot 4.1.1 | Estándar de la industria; introduce IoC, capas (Controller/Service/Repository) |
| Base de datos | PostgreSQL | Motor relacional real, gratuito, ampliamente usado en producción |
| Migraciones | Flyway | Enseña versionado de esquema como parte del ciclo de vida del código, no solo `ddl-auto` |
| Persistencia | Spring Data JPA | Abstrae SQL repetitivo, introduce el patrón Repository |
| Mapeo DTO/Entidad | MapStruct | Evita mapeo manual propenso a errores; buena práctica para no exponer entidades JPA directamente en la API |
| Reducción de boilerplate | Lombok | Getters/setters/constructores generados; permite centrar el código en la lógica, no en ceremonia |
| Pruebas | Testcontainers | Pruebas de integración contra un PostgreSQL real en Docker, evitando falsos positivos de bases en memoria (H2) |
| Documentación API | springdoc-openapi | Genera documentación OpenAPI/Swagger UI a partir del código, estándar de facto en APIs REST profesionales |
| Build | Maven | Estándar, buena integración con el resto del stack |

**Nota para el docente:** este stack requiere que los estudiantes tengan Docker instalado (para Testcontainers y, opcionalmente, para levantar PostgreSQL en desarrollo local). Es un buen punto para introducir `docker-compose` como parte del entorno de desarrollo.

## 5. Estructura de proyecto

```
ArenaRanking/
├── src/main/java/com/curso/arenaranking/
│   ├── ArenaRankingApplication.java
│   ├── team/
│   │   ├── Team.java                  (entidad JPA → tabla `teams`)
│   │   ├── TeamRepository.java
│   │   ├── TeamService.java           (incluye stats y racha)
│   │   ├── TeamController.java
│   │   ├── TeamMapper.java            (MapStruct)
│   │   └── dto/
│   │       ├── TeamRequest.java       (record)
│   │       ├── TeamResponse.java      (record)
│   │       ├── StatsResponse.java     (record)
│   │       └── StreakResponse.java    (record)
│   ├── match/
│   │   ├── Match.java                 (entidad JPA → tabla `matches`)
│   │   ├── MatchResult.java           (enum: WIN / LOSS / DRAW)
│   │   ├── MatchRepository.java
│   │   ├── MatchService.java
│   │   ├── MatchController.java       (anidado bajo /teams/{teamId}/matches)
│   │   ├── MatchMapper.java           (MapStruct)
│   │   └── dto/
│   │       ├── MatchRequest.java      (record)
│   │       └── MatchResponse.java     (record)
│   └── exceptions/
│       ├── ExceptionBase.java             (RuntimeException abstracta, padre común)
│       ├── ResourceNotFoundException.java (extends ExceptionBase → 404)
│       ├── DataConflictException.java     (extends ExceptionBase → 409)
│       ├── ErrorResponse.java             (record de error estándar)
│       └── GlobalExceptionHandler.java    (@RestControllerAdvice)
├── src/main/resources/
│   ├── db/migration/
│   │   ├── V1__create_teams_table.sql
│   │   └── V2__create_matches_table.sql
│   └── application.yml
├── src/test/java/com/curso/arenaranking/
│   ├── PostgresContainerSupport.java   (contenedor Postgres singleton reutilizable)
│   ├── IntegrationTestBase.java        (@SpringBootTest + @AutoConfigureMockMvc)
│   ├── team/
│   │   ├── TeamRepositoryIT.java       (pruebas de integración de repositorio)
│   │   ├── TeamServiceTest.java        (pruebas unitarias con Mockito)
│   │   └── TeamFlowIT.java             (flujo end-to-end vía MockMvc)
│   └── match/
│       ├── MatchRepositoryIT.java      (pruebas de integración de repositorio)
│       ├── MatchServiceTest.java       (pruebas unitarias con Mockito)
│       └── MatchStatsFlowIT.java       (flujo end-to-end: partidos → stats → racha)
└── pom.xml
```

**Notas sobre las pruebas:**

- Las clases `*Test` (`TeamServiceTest`, `MatchServiceTest`) son pruebas unitarias con Mockito, no requieren Docker y corren con `mvn test`.
- Las clases `*IT` (`TeamRepositoryIT`, `MatchRepositoryIT`, `TeamFlowIT`, `MatchStatsFlowIT`) son pruebas de integración con Testcontainers, requieren Docker y se ejecutan con `mvn test -Dtest="*IT"`.
- Todas las clases `*IT` comparten un único contenedor PostgreSQL reutilizable (patrón *Singleton Container* de Testcontainers, ver `PostgresContainerSupport`), con limpieza de datos en `@BeforeEach` para aislar cada prueba.

## 6. Criterios de evaluación / entregables

1. **Modelo de datos** — migraciones Flyway correctas, restricciones (unique, not null, FK) aplicadas.
2. **API REST** — CRUD completo de ambas entidades, códigos de estado HTTP correctos, DTOs (no exponer entidades JPA), paginación y filtros funcionando en los listados.
3. **Lógica de negocio** — cálculo correcto de `resultado`, de estadísticas/win rate y de la racha de victorias consecutivas.
4. **Pruebas** — al menos 2 pruebas de integración con Testcontainers cubriendo el flujo principal (crear equipo → registrar partidos → consultar estadísticas/racha).
5. **Documentación de la API** — documentación OpenAPI accesible y completa (Swagger UI), con todos los endpoints, DTOs y códigos de respuesta descritos.
6. **Calidad de código** — separación de capas, uso adecuado de Lombok/MapStruct, manejo de errores (404 al buscar un equipo inexistente, 400 en validaciones).
7. **Documentación mínima del proyecto** — README con instrucciones de ejecución (`docker-compose up`, `mvn spring-boot:run`).

## 7. Retos opcionales de extensión

Para estudiantes que terminen antes o busquen mayor desafío, sin romper el alcance de 2 tablas:

- Agregar caché simple (Spring Cache) al endpoint de estadísticas.
- Agregar ordenamiento configurable (por win rate, por número de partidos) al listado de equipos.
- Desplegar la aplicación en un servicio gratuito (Render, Railway) usando el `docker-compose` como base.
- Agregar autenticación básica (Spring Security) para proteger las operaciones de escritura.