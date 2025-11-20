### Hexlet tests and linter status:
[![Actions Status](https://github.com/titanmen1/devops-engineer-from-scratch-project-315/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/titanmen1/devops-engineer-from-scratch-project-315/actions)
# Project DevOps Deploy

Bulletin board service.

The default `dev` profile uses an in-memory H2 database and seeds 10 sample bulletins through `DataInitializer`, so the API works immediately after startup.

API documentation is available via Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

## Requirements

- JDK 21+.
- Gradle 9.2.1.
- PostgreSQL only if you run the `prod` profile with an external database.
- Make.
- NodeJS 20+

## Running

### Local profile
1. Start the application:
    ```bash

    make run
    ```
3. Explore the API:
    - All bulletins: `GET http://localhost:8080/api/bulletins`
    - Paginated + filtered: `GET http://localhost:8080/api/bulletins?page=1&perPage=9&sort=createdAt&order=DESC&state=PUBLISHED&search=laptop`
    - Swagger UI: `http://localhost:8080/swagger-ui/index.html`

`/api/bulletins` accepts pagination (`page`, `perPage`), sorting (`sort`, `order`) and filters (`state`, `search`). Filters are processed via JPA Specifications so the same contract is available to the React Admin frontend.

### Production

Prepare your database and export connection parameters:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bulletins
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

Build app:

    ```bash
    make build
    java -jar build/libs/project-devops-deploy-0.0.1-SNAPSHOT.jar
    ```

Options: `JAVA_OPTS` (ex. `JAVA_OPTS="-Xms256m -Xmx512m -Dspring.profiles.active=prod"`) can be setted via env vars.

### Useful commands

See [Makefile](./Makefile)

## Frontend

### Development

1. Install Node.js 24 LTS (or newer) and npm.
2. Install dependencies and start the Vite dev server:
   ```bash
   cd frontend
   make install
   make start
   ```
3. The dev server proxies `/api` requests to `http://localhost:8080`, so keep the backend running via `make run` (or `./gradlew bootRun`) in another terminal.

### Image upload flow

1. Upload files via `POST /api/files/upload` (multipart form field named `file`).
2. The response contains `key` and a temporary `url`. Persist the `key` in the `imageKey` field when creating or updating bulletins; the backend stores only that identifier.
3. When you need a fresh link, call `GET /api/files/view?key=...` to receive a new URL (the backend issues presigned links on demand).

### Build and serve from the Java app

1. Build the production bundle:
   ```bash
   cd frontend
   make install      # run once
   make build    # outputs to frontend/dist
   ```
2. Copy the compiled assets into Spring Boot’s static resources (served from `src/main/resources/static`):
   ```bash
   rm -rf src/main/resources/static
   mkdir -p src/main/resources/static
   cp -R frontend/dist/* src/main/resources/static/
   ```
3. Restart the backend (`make run`) and open `http://localhost:8080/` — the React app will now be served directly by the Java application.

### Running in Docker

Pass JVM flags via `JAVA_OPTS`:

```bash
docker run --rm -p 8080:8080 \
  -e JAVA_OPTS="-Xms256m -Xmx512m -Dspring.profiles.active=prod" \
  ...
```

Useful JVM options:
- `-Xms/-Xmx` — set memory limits inside the container.
- `-XX:+UseContainerSupport` / `-XX:ActiveProcessorCount` (these respect cgroup limits by default).
- `-Dspring.profiles.active=prod` — switch the profile without recompiling.
- `-Dlogging.level.root=INFO` or Spring environment variables (`SPRING_DATASOURCE_URL`, `STORAGE_S3_BUCKET`, etc.) — configure external services.

## Image Upload Checks

### Local (dev profile, H2 + temp storage)
1. Start backend: `make run` (uses in-memory H2 and local filesystem storage under `/tmp/bulletin-images`).
2. Start frontend dev server: `cd frontend && npm install && npm run dev`.
3. In React Admin:
   - Create a bulletin or edit an existing one.
   - Use the “Upload image” field; after save, the image preview should load via the generated `imageUrl`.
4. Verify backend log: look for `Stored image` entries or check `/tmp/bulletin-images` for a new file. Refresh the bulletin show page to ensure the presigned/local URL still renders.

### Production / S3
1. Export the required env vars before launching Spring Boot:
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   export STORAGE_S3_BUCKET=your-bucket
   export STORAGE_S3_REGION=eu-central-1
   export STORAGE_S3_ACCESSKEY=...
   export STORAGE_S3_SECRETKEY=...
   export STORAGE_S3_ENDPOINT=https://s3.eu-central-1.amazonaws.com   # optional
   export STORAGE_S3_CDNURL=https://cdn.example.com/bulletins          # optional
   ```
2. Deploy backend (e.g., `java -jar build/libs/project-devops-deploy-0.0.1-SNAPSHOT.jar`).
3. In the frontend (local or deployed), upload an image for a bulletin.
4. Confirm expected behavior:
   - Response from `/api/files/upload` contains a non-empty `key`.
   - Image shows up in bulletin show view (URL should either point to CDN or be a presigned S3 link).
   - Object exists in S3 bucket (check via AWS console or `aws s3 ls s3://your-bucket/bulletins/...`).
5. Optional: run `curl -I "$(curl -s .../api/files/view?key=... | jq -r .url)"` to ensure the presigned URL is valid from the production environment.
