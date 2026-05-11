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

or:

```bash
docker-compose up -d --build
```

Creates the network `stg-track-my-case-mock` on first run (`MOCK_HOST_PORT` / `SERVER_PORT` behave like the main service’s host vs container port split).

If `./gradlew` inside `docker build` cannot reach `services.gradle.org`, fix Docker DNS/network or try `docker build --network=host .` on Linux (same class of issue as the main service).

## With stg-track-my-case-service

From `stg-track-my-case-service`, `docker compose up` expects this mock to be running and attached to the same network (see that repo’s `docker-compose.yml`).

## Endpoints mocked

- `GET /courthouses/{court_id}`
- `GET /courthouses/{court_id}/courtrooms/{court_room_id}`
- `GET /pcd/cases/{case_urn}`
- `GET /case/{case_urn}/courtschedule`
- `POST /{tenant_id}/oauth2/v2.0/token` (when `TMC_TOKEN_URL` points here)

### Court schedule mock URN format

The `case_urn` supports dynamic values:

- Prefix `TMC`
- Hearing type: `TR` (Trial) or `SE` (Sentence)
- Optional offset: `{n}M` / `N{n}M` months, `{n}D` / `N{n}D` days
- Optional trailing digits (≥ 2) for number of sittings

Examples: `TMCTR0D`, `TMCTRN1D2`, `TMCSE2M3D5`.
