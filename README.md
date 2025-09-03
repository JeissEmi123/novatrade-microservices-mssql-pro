# Sistema-de-comercio-electr-nico-basado-en-microservicios

# Novatrade Microservicios — PRO (SQL Server)

Microservicios **enterprise-ready** con **SQL Server**, **Flyway (T‑SQL)**, **JSON:API**, **idempotencia**, **API keys**, **OpenAPI**, **Resilience4j**, **Actuator**, **Dockerfiles** y **Docker Compose**.

## Servicios
- **catalog-ms**: catálogo de productos (crear, obtener, listar). Puerto 8081.
- **inventory-ms**: inventario + **compras** (descuenta stock consultando `catalog-ms`). Puerto 8082.

## Requisitos previos
- Docker y Docker Compose.
- Puertos libres: 11433 (SQL Server), 8081 y 8082.

## Ejecutar
```bash
docker compose up --build
# Swagger UI:
#   http://localhost:8081/swagger-ui.html
#   http://localhost:8082/swagger-ui.html
```
> El servicio `mssql-init` crea las bases `catalog` e `inventory`. Los servicios se reinician automáticamente si arrancan antes de que las DBs estén listas.

## Variables sensibles (compose)
- `SA_PASSWORD=YourStrong!Passw0rd` (cámbialo en producción).
- `CATALOG_API_KEY`, `INVENTORY_API_KEY`. Envíalas en `X-API-KEY` para llamadas internas.

## JSON:API
Todas las respuestas usan `application/vnd.api+json` con `data`/`errors`. Errores 400/404/409 estandarizados.

## Idempotencia de compras
`inventory-ms` soporta **Idempotency-Key** (cabecera). Si repites la misma key, obtienes la misma respuesta almacenada.

## SQL Server + Flyway
- Migraciones T‑SQL (`V1__init.sql`) crean tablas e índices.
- `ddl-auto=validate` para asegurar que el esquema coincide.

## Resiliencia y observabilidad
- Cliente de `inventory-ms` a `catalog-ms` con **Retry + CircuitBreaker** (Resilience4j).
- **Actuator**: health/info/metrics.
- **Logging JSON** con Logstash encoder.

## Probar rápido
```bash
# Crear producto (catalog-ms)
curl -s -X POST http://localhost:8081/api/v1/products   -H 'Content-Type: application/vnd.api+json'   -d '{"data":{"type":"products","attributes":{"name":"Teclado","price":199.99,"description":"Switches azules"}}}'

# Asignar inventario (inventory-ms)
curl -s -X PATCH http://localhost:8082/api/v1/inventory/1   -H 'Content-Type: application/vnd.api+json'   -d '{"data":{"type":"inventory","attributes":{"quantity":10}}}'

# Comprar con idempotencia (inventory-ms)
curl -s -X POST http://localhost:8082/api/v1/purchases   -H 'Content-Type: application/vnd.api+json'   -H 'Idempotency-Key: demo-123'   -d '{"data":{"type":"purchases","attributes":{"productId":1,"quantity":2}}}'
```

## Estructura por servicio
```
<service>/
├─ model/                       # Dominio y puertos
├─ usecase/                     # Casos de uso (negocio)
├─ infrastructure/
│  ├─ entry-points/rest-web     # Controllers (JSON:API, filtros, errores)
│  └─ driven-adapters/sqlserver-jpa  # JPA/SQL Server, repos e integraciones
└─ applications/app-service     # @SpringBootApplication + wiring
```

## Roadmap sugerido
- Tests de integración con **Testcontainers** (SQL Server).
- Seeds con Flyway (`V2__seed_data.sql`).
- Outbox + eventos de inventario (Kafka/NATS).
- OpenTelemetry para trazas distribuidas.
