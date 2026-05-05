# stg-track-my-case-service-mock

Standalone WireMock server that mocks downstream AMP endpoints for local development of [stg-track-my-case-service](../stg-track-my-case-service).

## Run locally

```bash
WIREMOCK_PORT=8089 ./gradlew run
```

## Docker

Creates a user-defined network `stg-track-my-case-mock` so the main app’s Docker Compose can resolve the hostname `wiremock-service`.

Start the mock **before** starting `stg-track-my-case-service` compose:

```bash
docker compose up -d --build
```

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
