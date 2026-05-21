# SPP Demo API

Spring Boot API project with OpenAPI/Swagger UI.
## Prerequisites

### Run locally (without Docker)
- Java 25
- No separate Maven install is required (this repo includes the Maven Wrapper)

### Run with Docker
- Docker Desktop (or Docker Engine)
- Docker Compose

## Run the API locally

From the repository root:

### Windows (PowerShell)
```powershell
.\mvnw.cmd spring-boot:run
```

### macOS/Linux
```bash
./mvnw spring-boot:run
```

The API will start on:
- http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## Run the API with Docker

From the repository root:

```bash
docker-compose up --build -d
```

Then open:
- http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html


## Useful commands


Run tests:

### Windows (PowerShell)
```powershell
.\mvnw.cmd test
```

### macOS/Linux
```bash
./mvnw test
```


## OpenAPI generation

This project uses `src/main/resources/static/openapi/api.yaml` as the single API contract.

During the Maven `generate-sources` phase, the OpenAPI Generator creates Spring API interfaces/controllers and request/response models from that YAML.

Swagger UI is also configured to read the same YAML file (`/openapi/api.yaml`), so the interactive documentation stays aligned with the generated code contract.