# Convocatorias API (`convocatorias_api`)

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)
![Arquitectura](https://img.shields.io/badge/Arquitectura-Limpia%20%2F%20Hexagonal-blue.svg)
![Base de Datos](https://img.shields.io/badge/DB-PostgreSQL%2017-blue.svg)
![Migraciones](https://img.shields.io/badge/Flyway-Migrated-green.svg)
![Tests](https://img.shields.io/badge/Tests-42%20passed-brightgreen.svg)
![Cobertura](https://img.shields.io/badge/JaCoCo-Active-blue.svg)
![Estado](https://img.shields.io/badge/Build-Passing-success.svg)

**Convocatorias API** es un servicio REST desarrollado en **Java 21** y **Spring Boot 4.1.0** construido bajo los principios de **Arquitectura Limpia (Clean Architecture)**. Gestiona convocatorias artísticas y culturales (becas, fondos, residencias, premios) con persistencia en **PostgreSQL**, migraciones controladas por **Flyway**, mapeo objeto-relacional con **Spring Data JPA** + **Hibernate 7**, y reducción de boilerplate mediante **Lombok**.

---

## Arquitectura del Proyecto

El proyecto está estructurado en 3 capas principales para garantizar mantenibilidad, testeabilidad e independencia de frameworks:

```text
convocatorias-api/
├── pom.xml                                                           # Configuración Maven
├── .env                                                              # Variables de entorno (DB credentials)
├── README.md                                                         # Documentación técnica
├── .gitignore
└── src/
    ├── main/
    │   ├── java/cl/tucultura/convocatorias_api/
    │   │   ├── ConvocatoriasApiApplication.java                     # Punto de entrada de Spring Boot
    │   │   │
    │   │   ├── domain/                                               # 1. CAPA DE DOMINIO
    │   │   │   └── model/
    │   │   │       └── Convocatoria.java                             # Modelo de dominio (record)
    │   │   │
    │   │   ├── application/                                          # 2. CAPA DE APLICACIÓN
    │   │   │   └── service/
    │   │   │       ├── ConvocatoriaService.java                      # Interfaz de casos de uso
    │   │   │       └── ConvocatoriaServiceImpl.java                  # Lógica de negocio
    │   │   │
    │   │   └── infrastructure/                                       # 3. CAPA DE INFRAESTRUCTURA
    │   │       ├── persistence/                                      # Adaptador de Persistencia
    │   │       │   ├── entity/
    │   │       │   │   └── ConvocatoriaEntity.java                   # Entidad JPA (Hibernate)
    │   │       │   ├── mapper/
    │   │       │   │   └── ConvocatoriaMapper.java                   # Mapeador Entity <-> Domain
    │   │       │   └── repository/
    │   │       │       └── ConvocatoriaRepository.java               # Repositorio Spring Data JPA
    │   │       └── web/                                              # Adaptador Web REST
    │   │           ├── controller/
    │   │           │   └── ConvocatoriaController.java               # Controlador REST
    │   │           └── dto/
    │   │               ├── ConvocatoriaRequestDTO.java               # DTO de entrada (POST)
    │   │               └── ConvocatoriaResponseDTO.java              # DTO de salida (GET)
    │   │
    │   └── resources/
    │       ├── application.yaml                                      # Configuración de Spring Boot
    │       ├── db/
    │       │   └── migration/
    │       │       └── V1__schema_inicial.sql                        # Migración inicial (Flyway)
    │       └── static/                                               # Recursos estáticos
    │
    └── test/
        └── java/cl/tucultura/convocatorias_api/
            ├── ConvocatoriasApiApplicationTests.java                 # Test de contexto (SpringBootTest)
            ├── domain/
            │   └── model/
            │       └── ConvocatoriaTest.java                         # Tests del modelo dominio
            ├── application/
            │   └── service/
            │       └── ConvocatoriaServiceImplTest.java              # Tests unitarios del servicio (Mockito)
            └── infrastructure/
                ├── persistence/
                │   └── mapper/
                │       └── ConvocatoriaMapperTest.java               # Tests unitarios del mapper
                └── web/
                    ├── controller/
                    │   └── ConvocatoriaControllerTest.java          # Tests de integración REST (MockMvc)
                    ├── dto/
                    │   ├── ConvocatoriaRequestDTOTest.java           # Tests unitarios del DTO de entrada
                    │   └── ConvocatoriaResponseDTOTest.java          # Tests unitarios del DTO de salida
                    └── exception/
                        └── GlobalExceptionHandlerTest.java           # Tests unitarios del handler de excepciones
```

---

## Dependencias

### Dependencias de Producción

| Dependencia | Versión | Propósito |
|---|---|---|
| Spring Boot Starter WebMVC | 4.1.0 | Servidor REST embebido (Tomcat) |
| Spring Boot Starter Data JPA | 4.1.0 | Acceso a datos con JPA/Hibernate |
| Spring Boot Starter Flyway | 4.1.0 | Migraciones de base de datos |
| Spring Boot Starter Validation | 4.1.0 | Validación de Bean Validation |
| PostgreSQL Driver | Runtime | Driver JDBC para PostgreSQL |
| Flyway PostgreSQL | Runtime | Soporte nativo Flyway para PostgreSQL |
| Lombok | Compile | Reducción de boilerplate |
| Jackson (via Spring) | Transitive | Serialización JSON (Jackson 3.x) |

### Dependencias de Testing

| Dependencia | Scope | Propósito |
|---|---|---|
| Spring Boot Starter Test | test | JUnit 5, Mockito, AssertJ, Hamcrest |
| Spring Boot Starter WebMVC Test | test | `@WebMvcTest` + MockMvc para tests de controladores |
| Spring Boot Starter Data JPA Test | test | `@DataJpaTest` + repositorios en H2 |
| Spring Boot Starter Flyway Test | test | `@FlywayTest` para tests con esquema controlado |
| Spring Boot Starter Validation Test | test | `@ValidationTest` para tests de validación |
| H2 Database | test | Base de datos en memoria (no requiere PostgreSQL) |
| JaCoCo Maven Plugin | build | Cobertura de código con reporte HTML |

---

## Requisitos Previos

- **Java 21** o superior instalado.
- **Maven 3.8+** (o utilizar el ejecutable `./mvnw` incluido).
- **PostgreSQL 17+** corriendo localmente.
- Una base de datos llamada `convocatorias_db`:
  ```sql
  CREATE DATABASE convocatorias_db;
  ```

---

## Cómo Ejecutar la Aplicación

El proyecto utiliza variables de entorno para la conexión a la base de datos. Crea un archivo `.env` en la raíz del proyecto:

```env
DB_URL=jdbc:postgresql://localhost:5432/convocatorias_db
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password
```

### Iniciar el servidor

```bash
# Cargar variables de entorno e iniciar
source .env && ./mvnw spring-boot:run

# O con clean build (recomendado después de cambios en migraciones)
./mvnw clean spring-boot:run
```

La aplicación se iniciará en `http://localhost:8090`.

Flyway ejecutará automáticamente la migración `V1__schema_inicial.sql` al iniciar, creando las tablas `fuentes`, `convocatorias`, `etiquetas` y la tabla intermedia `convocatoria_etiquetas`.

---

## Modelo de Base de Datos

```text
fuentes (UUID PK)
    └── convocatorias (UUID PK, FK fuente_id)
            └── convocatoria_etiquetas (PK compuesta)
                    └── etiquetas (SERIAL PK)
```

**Tipos de Convocatoria:** `BECA`, `FONDO`, `RESIDENCIA`, `PREMIO`, `CONVOCATORIA`

**Estados:** `ABIERTA`, `PROXIMAMENTE`, `CERRADA`, `CANCELADA`

> El estado se calcula automáticamente al crear una convocatoria según las fechas de apertura y cierre.

---

## Ejecución de Pruebas

```bash
# Ejecutar todos los tests
./mvnw clean test

# Generar reporte de cobertura JaCoCo
./mvnw jacoco:report

# Abrir reporte en el navegador
start target/site/jacoco/index.html
```

### Suite de Pruebas (42 tests)

| Clase de Test | Tipo | Tests | Qué cubre |
|---|---|---|---|
| `ConvocatoriaTest` | Unit puro | 3 | Modelo de dominio (record `Convocatoria`) |
| `ConvocatoriaMapperTest` | Unit puro | 9 | Mapeo Entity ↔ Domain con JSONB |
| `ConvocatoriaServiceImplTest` | Unit (Mockito) | 8 | Lógica de negocio: CRUD, cálculo de estado, validaciones |
| `ConvocatoriaControllerTest` | Integración (MockMvc) | 9 | Endpoints REST: GET, GET/{id}, POST exitoso, validaciones de body, tipo, fechas, URL |
| `ConvocatoriaRequestDTOTest` | Unit puro | 7 | DTO de entrada: constructor, conversión, defaults, validación de fechas |
| `ConvocatoriaResponseDTOTest` | Unit puro | 2 | DTO de salida: mapeo completo de campos |
| `GlobalExceptionHandlerTest` | Unit puro | 3 | Excepciones globales: validación, negocio, genéricas |
| `ConvocatoriasApiApplicationTests` | Integración (Spring) | 1 | Carga del contexto Spring Boot |

> **Nota Spring Boot 4.x**: `@WebMvcTest` se importa desde `org.springframework.boot.webmvc.test.autoconfigure` y `@MockitoBean` reemplaza a `@MockBean` (de `org.springframework.test.context.bean.override.mockito`).

---

## Cobertura de Código (JaCoCo)

| Métrica | Cobertura |
|---|---|
| Instrucciones | **96%** (25 de 750 missed) |
| Ramas | **90%** (3 de 32 missed) |
| Métodos | **87%** (3 de 24 missed) |
| Clases | **90%** (1 de 11 missed) |

### Cobertura por Paquete

| Paquete | Instrucciones | Ramas |
|---|---|---|
| `domain.model` | 100% | 100% |
| `application.service` | 100% | 100% |
| `infrastructure.web.controller` | 100% | n/a |
| `infrastructure.web.dto` | 100% | 100% |
| `infrastructure.web.exception` | 100% | 100% |
| `infrastructure.persistence.mapper` | 98% | 91% |
| `infrastructure.persistence.entity` | 0% | 0% |
| `ConvocatoriasApiApplication` (main) | 37% | n/a |

> **Notas**:
> - `infrastructure.persistence.entity` (0%) es una clase JPA con anotaciones sin lógica testable unitariamente.
> - `ConvocatoriasApiApplication` (37%) contiene solo el método `main()` que no se testea.
> - `infrastructure.web.exception` (100%) cubre los 3 handlers del `GlobalExceptionHandler`.

### Generar Reporte

```bash
./mvnw test jacoco:report
start target/site/jacoco/index.html
```

---

## Documentación de Endpoints REST

### 1. `GET /api/convocatorias` - Listar Convocatorias Activas

Retorna todas las convocatorias con estado `ABIERTA` y fecha de cierre futura.

- **Método**: `GET`
- **URL**: `http://localhost:8090/api/convocatorias`
- **Respuesta (`200 OK`)**:

```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "titulo": "Beca Creación Musical 2026",
    "descripcion": "Beca para artistas emergentes en música",
    "tipo": "BECA",
    "categoria": "Música",
    "monto": 5000000,
    "moneda": "CLP",
    "fechaApertura": "2026-08-01T00:00:00",
    "fechaCierre": "2026-10-31T23:59:59",
    "urlOficial": "https://www.cultura.gob.cl/beca-2026",
    "estado": "ABIERTA",
    "requisitos": ["Ser mayor de 18 años", "Residir en Chile"],
    "documentacion": ["Carta de presentación", "Portafolio"],
    "fuenteId": "f7e8d9c0-a1b2-3456-7890-abcdef123456"
  }
]
```

---

### 2. `GET /api/convocatorias/{id}` - Obtener Convocatoria por ID

Retorna una convocatoria específica por su UUID.

- **Método**: `GET`
- **URL**: `http://localhost:8090/api/convocatorias/{id}`
- **Respuesta (`200 OK`)**: Mismo formato que el anterior (objeto único).
- **Respuesta (`404 Not Found`)**: Si no existe la convocatoria.

---

### 3. `POST /api/convocatorias` - Crear Convocatoria

Crea una nueva convocatoria. El campo `estado` se calcula automáticamente según las fechas.

- **Método**: `POST`
- **URL**: `http://localhost:8090/api/convocatorias`
- **Headers**: `Content-Type: application/json`

#### Ejemplo de Petición (Request Body):

```json
{
  "titulo": "Residencia Artística Visual 2026",
  "descripcion": "Programa de residencia para artistas visuales",
  "tipo": "RESIDENCIA",
  "categoria": "Artes Visuales",
  "monto": 3000000,
  "moneda": "CLP",
  "fechaApertura": "2026-09-01T00:00:00",
  "fechaCierre": "2026-12-15T23:59:59",
  "urlOficial": "https://www.fundacionbeethoven.org/residencia",
  "requisitos": ["Ser artista visual", "Tener portafolio"],
  "documentacion": ["CV", "Propuesta artística"],
  "fuenteId": "f7e8d9c0-a1b2-3456-7890-abcdef123456"
}
```

#### Respuesta Exitosa (`200 OK`):

```json
{
  "id": "nuevo-uuid-generado",
  "titulo": "Residencia Artística Visual 2026",
  "descripcion": "Programa de residencia para artistas visuales",
  "tipo": "RESIDENCIA",
  "categoria": "Artes Visuales",
  "monto": 3000000,
  "moneda": "CLP",
  "fechaApertura": "2026-09-01T00:00:00",
  "fechaCierre": "2026-12-15T23:59:59",
  "urlOficial": "https://www.fundacionbeethoven.org/residencia",
  "estado": "PROXIMAMENTE",
  "requisitos": ["Ser artista visual", "Tener portafolio"],
  "documentacion": ["CV", "Propuesta artística"],
  "fuenteId": "f7e8d9c0-a1b2-3456-7890-abcdef123456"
}
```

> **Nota**: El `estado` se calcula automáticamente. Si la `fechaApertura` es futura → `PROXIMAMENTE`. Si `fechaCierre` es pasada → `CERRADA`. Si las fechas encuadran → `ABIERTA`.

---

## Guía de Pruebas con cURL

```bash
# 1. Listar convocatorias activas
curl -i -X GET http://localhost:8090/api/convocatorias

# 2. Obtener convocatoria por ID
curl -i -X GET http://localhost:8090/api/convocatorias/{id}

# 3. Crear una nueva convocatoria
curl -i -X POST http://localhost:8090/api/convocatorias \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Fondo de Música 2026",
    "descripcion": "Fondo de financiamiento para producciones musicales",
    "tipo": "FONDO",
    "categoria": "Música",
    "monto": 2000000,
    "moneda": "CLP",
    "fechaApertura": "2026-08-01T00:00:00",
    "fechaCierre": "2026-11-30T23:59:59",
    "urlOficial": "https://www.cultura.gob.cl/fondo-musica",
    "requisitos": ["Ser músico chileno"],
    "documentacion": ["CD o plataformas digitales"],
    "fuenteId": null
  }'
```

---

## Configuración en Postman

1. Crear una colección llamada `Convocatorias API`.
2. Agregar las 3 peticiones descritas arriba con su correspondiente verbo HTTP.
3. En la petición `POST`, configurar el Header `Content-Type: application/json` y seleccionar `body` -> `raw` -> `JSON`.
