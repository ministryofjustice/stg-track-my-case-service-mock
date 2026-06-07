# stg-track-my-case-service-mock

Spring Boot mock service for downstream AMP endpoints, used for local development of [stg-track-my-case-service](../stg-track-my-case-service).

## Run locally

```bash
SERVER_PORT=8089 ./gradlew run
```

## Docker

Same layout as [stg-track-my-case-service](../stg-track-my-case-service): `Dockerfile`, `docker-compose.yml`, `deploy.sh`, and `.env` / `.env.example`.

Copy `.env.example` to `.env`, then either:

```bash
./deploy.sh
```

or (build the JAR on the host, then Docker):

```bash
./gradlew prepareDocker
docker-compose up -d --build
```

Creates the network `stg-track-my-case-mock` on first run (`MOCK_HOST_PORT` / `SERVER_PORT` behave like the main service’s host vs container port split).

**Important:** `docker-compose build` does not run Gradle. You must run `./gradlew prepareDocker` first (creates `docker/stg-track-my-case-service-mock.jar`). `./deploy.sh` runs both steps. If you only run `docker-compose build`, you will see `docker/stg-track-my-case-service-mock.jar: not found`.

## With stg-track-my-case-service

From `stg-track-my-case-service`, `docker compose up` expects this mock to be running and attached to the same network (see that repo’s `docker-compose.yml`).

## Endpoints mocked

- `GET /courthouses/{court_id}`
- `GET /courthouses/{court_id}/courtrooms/{court_room_id}`
- `GET /pcd/cases/{case_urn}`
- `GET /case/{case_urn}/courtschedule`
- `POST /{tenant_id}/oauth2/v2.0/token` (when `TMC_TOKEN_URL` points here)

### Court schedule mock URN format

The `case_urn` is the hearing-type prefix followed by an optional date/sitting body.
Longest prefix wins (e.g. `TFTW` is matched before `TF` or `T`).

| Prefix | Hearing type |
|--------|--------------|
| `T`    | Trial |
| `TNW`  | Trial - no witnesses |
| `TB`   | Trial (Backer) |
| `TF`   | Trial (Floater) |
| `TFW`  | Trial (First Warning) |
| `TPH`  | Trial (Part Heard) |
| `TOPI` | Trial of Preliminary Issue |
| `TP`   | Trial (Priority) |
| `TPW`  | Trial (Previously Warned) |
| `TR`   | Trial (Reserve) |
| `TL`   | Trial Linked |
| `TFTW` | Trial (Fixed for this Week) |
| `S`    | Sentence |
| `SAAC` | Sentence (at another Court) |
| `SOTA` | Sentence (Officer to Attend) |
| `SPTA` | Sentence (Prosecution to Attend) |
| `SPOA` | Sentence (Prosecution and Officer to Attend) |
| `SPR`  | Sentence (Prosecution Released) |
| `CFS`  | Committal for Sentence |
| `CSPH` | Committal for Sentence (Part Heard) |
| `DS`   | Deferred Sentence |
| `DSRR` | Deferred Sentence (Respondent Released) |
| `DSPR` | Deferred Sentence - Prosecution Released |

Body (all parts optional):
- `{n}M` / `N{n}M` — months offset (positive / negative)
- `{n}D` / `N{n}D` — days offset (positive / negative)
- Trailing digits ≥ 2 — number of court sittings

Examples: `T0D`, `TN1D2`, `S2M3D5`, `TR`, `CFS99M`, `TFTW`, `DSRR`.
