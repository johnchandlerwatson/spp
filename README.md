# SPP Demo API

![Java 25](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?logo=springboot&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Enabled-2496ED?logo=docker&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-Metrics-E6522C?logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-F46800?logo=grafana&logoColor=white)
![Loki](https://img.shields.io/badge/Loki-Logs-1F60C4?logo=grafana&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Contract%20First-6BA539?logo=swagger&logoColor=white)

Spring Boot API demo with an OpenAPI-first workflow and a ready-to-run observability stack.

## Features

- OpenAPI-driven contract and generated server interfaces
- Swagger UI for rapid API exploration
- Dockerized local stack for app, metrics, and dashboards
- Prebuilt Grafana panels for API health, latency, and error diagnostics

## Quick Start

### Prerequisites

- Java 25 (local run)
- Docker Desktop or Docker Engine + Docker Compose (container run)


### Run the Stack

From the repository root:


```powershell
.\scripts\start-stack.ps1
```

## Endpoints

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Loki: http://localhost:3100
- Prometheus scrape endpoint from app: http://localhost:8080/actuator/prometheus

Grafana default credentials (local dev): admin / admin

## Observability Stack

Docker Compose starts these services:

- spp-app: Spring Boot API
- prometheus: scrapes app metrics
- grafana: provisioned data source + dashboards
- loki: log storage and query backend
- alloy: collects app logs and ships them to Loki

Endpoint activity logs are emitted as structured JSON with fields such as:

- event (http.access)
- http_method
- http_path
- http_status
- duration_ms
- request_id
- client_ip

Dashboards:

- **SPP / API Operations** — request rate, latency percentiles, error ratios, JVM health
- **SPP / Structured Logs** — log volume histogram + live stream with per-entry field expansion


Snapshot 1: Operations overview

![SPP API operations dashboard overview](image.png)

Overview of API status, request throughput, latency trends, and error ratios.

Snapshot 2: Diagnostic detail

![SPP API diagnostics dashboard details](image-1.png)

## Useful Commands

Run tests:

```powershell
.\mvnw.cmd test
```


## OpenAPI Generation

This project uses `src/main/resources/static/openapi/api.yaml` as the single API contract.

During Maven generate-sources, OpenAPI Generator creates Spring API interfaces/controllers and models from that YAML.

Swagger UI is configured to read the same contract from `/openapi/api.yaml` so docs stay aligned with generated code.