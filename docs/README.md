# Documentación del Proyecto Novatrade

## Descripción General

Novatrade es un sistema de comercio electrónico basado en microservicios, diseñado para gestionar productos, inventarios y ventas. La arquitectura está compuesta por dos microservicios principales:

1. **Microservicio de Catálogo**: Responsable de la gestión del catálogo de productos.
2. **Microservicio de Inventario**: Responsable de la gestión del inventario y procesamiento de compras.

## Instrucciones de Instalación y Ejecución

### Requisitos Previos

- JDK 17 o superior
- Docker y Docker Compose
- Gradle
- SQL Server (o acceso a un contenedor)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/novatrade-microservices-mssql-pro.git
   cd novatrade-microservices-mssql-pro
   ```

2. **Iniciar contenedores de servicios**
   ```bash
   docker-compose up -d
   ```

3. **Configurar la base de datos**
   ```bash
   # En Windows
   configure_database.bat
   
   # En Linux/Mac
   ./setup_database.sql
   ```

4. **Verificar la conexión a la base de datos**
   ```bash
   # En Windows
   verify_connection.bat
   
   # En Linux/Mac
   cat test_connection.sql | docker exec -i sql-server /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P YourStrong@Passw0rd
   ```

### Ejecución de los Microservicios

1. **Microservicio de Catálogo**
   ```bash
   cd catalog-ms
   ./gradlew bootRun
   ```

2. **Microservicio de Inventario** (en una nueva terminal)
   ```bash
   cd inventory-ms
   ./gradlew bootRun
   ```

### Prueba de los Servicios

Se incluyen scripts para probar los endpoints:
```bash
# En Windows
.\Test-Endpoints.ps1

# En Linux/Mac
./test-endpoints.sh
```

También puede importar la colección de Postman desde `docs/postman_collection.json` para probar manualmente los endpoints.

## Arquitectura del Sistema

### Visión General

La arquitectura de Novatrade sigue los principios de microservicios, con componentes independientes que se comunican a través de APIs RESTful. Los servicios están diseñados siguiendo el patrón de arquitectura hexagonal (Ports and Adapters), lo que permite una clara separación de responsabilidades y facilita las pruebas unitarias.

![Arquitectura Novatrade](./arquitectura_novatrade.png)

### Componentes Principales

#### Microservicio de Catálogo
- **Responsabilidades**: Gestión de productos, incluyendo creación, consulta y listado.
- **Tecnologías**: Spring Boot, JPA/Hibernate, SQL Server.
- **Endpoints**: Documentados en `swagger/catalog-api.yaml`.

#### Microservicio de Inventario
- **Responsabilidades**: Gestión de inventario y procesamiento de compras.
- **Tecnologías**: Spring Boot, JPA/Hibernate, SQL Server.
- **Endpoints**: Documentados en `swagger/inventory-api.yaml`.
- **Características especiales**: Implementación de idempotencia para transacciones seguras.

### Base de Datos

Ambos microservicios utilizan SQL Server como sistema de gestión de bases de datos, con esquemas independientes para garantizar el aislamiento de datos.

### Comunicación entre Servicios

Los microservicios se comunican mediante llamadas REST. El diagrama a continuación ilustra el flujo de interacción entre los servicios:

```
┌─────────────────┐      GET /products/{id}      ┌─────────────────┐
│                 │◄─────────────────────────────│                 │
│  Microservicio  │                             │  Microservicio  │
│   de Catálogo   │                             │  de Inventario  │
│                 │                             │                 │
└─────────────────┘                             └─────────────────┘
        ▲                                               │
        │                                               │
        │ GET /products                                 │ POST /purchases
        │                                               │
        │                                               ▼
┌─────────────────────────────────────────────────────────────┐
│                       Cliente / Usuario                      │
└─────────────────────────────────────────────────────────────┘
```

## Decisiones Técnicas y Justificaciones

### Arquitectura Hexagonal

Se eligió el patrón de arquitectura hexagonal para:
- **Separación de responsabilidades**: Mantener la lógica de negocio aislada de las dependencias externas.
- **Facilidad de pruebas**: Permitir pruebas unitarias sin depender de infraestructura externa.
- **Flexibilidad**: Facilitar cambios en la capa de infraestructura sin afectar la lógica de negocio.

### Microservicios independientes

Se decidió separar el sistema en dos microservicios para:
- **Escalabilidad individual**: Permitir escalar cada servicio según sus necesidades específicas.
- **Aislamiento de fallos**: Evitar que un problema en un servicio afecte al otro.
- **Desarrollo independiente**: Permitir que equipos trabajen en paralelo en diferentes funcionalidades.

### Implementación del endpoint de compra en el Microservicio de Inventario

El endpoint de compra (`POST /api/v1/purchases`) fue implementado en el **Microservicio de Inventario** por las siguientes razones:

1. **Responsabilidad principal**: La compra implica principalmente una reducción de inventario, lo cual es responsabilidad directa del microservicio de Inventario.

2. **Transaccionalidad**: La operación de compra necesita garantizar que la actualización del inventario sea atómica. Al colocar esta lógica en el mismo servicio, se evitan problemas de consistencia distribuida.

3. **Idempotencia**: El microservicio de Inventario implementa la idempotencia mediante el uso de claves de idempotencia, esencial para operaciones de compra.

4. **Reducción de latencia**: Al estar la lógica de compra y gestión de inventario en el mismo servicio, se reducen las llamadas entre servicios y, por ende, la latencia.

5. **Datos de consulta**: El microservicio de Inventario consulta al de Catálogo solo para obtener información de presentación (nombre, precio), pero la lógica de negocio de la compra está contenida en Inventario.

### Uso de formato JSON:API

Se eligió el formato JSON:API para estandarizar las respuestas de los endpoints y proporcionar una estructura coherente para:
- Representación de recursos
- Manejo de relaciones
- Estandarización de errores
- Compatibilidad con herramientas de cliente

## Flujo de Compra Implementado

El flujo de compra se implementa mediante una secuencia de operaciones que involucran ambos microservicios:

1. **Solicitud de compra**:
   - El cliente envía una solicitud POST a `/api/v1/purchases` con productId y quantity
   - Opcionalmente incluye un header `Idempotency-Key` para evitar compras duplicadas

2. **Validación y procesamiento en el Microservicio de Inventario**:
   - Verifica si existe una respuesta cacheada para la clave de idempotencia
   - Si existe, retorna la respuesta almacenada sin procesar nuevamente
   - Si no existe:
     - Consulta el inventario disponible para el producto
     - Verifica que haya suficiente stock
     - Actualiza el inventario restando la cantidad comprada

3. **Consulta de información del producto**:
   - El microservicio de Inventario llama al de Catálogo (GET /products/{id})
   - Obtiene el nombre y precio actual del producto

4. **Cálculo y respuesta**:
   - Calcula el precio total de la compra
   - Genera un identificador único para la transacción
   - Construye y retorna la respuesta de la compra
   - Almacena la respuesta asociada a la clave de idempotencia (si se proporcionó)

### Diagrama de secuencia del flujo de compra

```
┌─────────┐          ┌───────────────┐          ┌─────────────┐
│ Cliente │          │ Inventario MS │          │ Catálogo MS │
└────┬────┘          └───────┬───────┘          └──────┬──────┘
     │                       │                         │
     │  POST /purchases      │                         │
     │───────────────────────>                         │
     │                       │                         │
     │                       │  GET /products/{id}     │
     │                       │────────────────────────>│
     │                       │                         │
     │                       │  Detalles del producto  │
     │                       │<────────────────────────│
     │                       │                         │
     │                       │ [Procesa la compra]     │
     │                       │─────┐                   │
     │                       │     │ Verifica stock    │
     │                       │<────┘                   │
     │                       │                         │
     │                       │─────┐                   │
     │                       │     │ Actualiza         │
     │                       │     │ inventario        │
     │                       │<────┘                   │
     │                       │                         │
     │  Respuesta de compra  │                         │
     │<──────────────────────│                         │
     │                       │                         │
```

Se utilizaron herramientas de IA para mejorar el desarrollo:

- **chat gpt**: para la validacion de las pruebas unitarias.
- **AI Code Reviewer**: Análisis de código estatico y validacion de cobertura.


## Patrones de Diseño Implementados

1. **Arquitectura Hexagonal**: Separación clara entre lógica de negocio (dominio) y adaptadores de infraestructura.
2. **Patrón Repository**: Para abstraer el acceso a datos.
3. **Idempotencia**: En el procesamiento de transacciones, para evitar duplicidad en compras.
4. **JSON:API**: Formato estandarizado para las respuestas de API.

## Mejoras Futuras

- Implementación de gateway API
- Sistema de autenticación y autorización
- Integración con servicios de pago
- Sistema de notificaciones para eventos importantes
