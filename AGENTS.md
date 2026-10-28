# AGENTS.md

Guía para agentes de código que trabajen en este repositorio (MedLab).

## Proyecto

Sistema MedicLab: **backend** Java + **frontend** Angular/Tauri.

| Componente | Stack |
|---|---|
| Backend | Java 25, Spring Boot 4.1.1, Maven (`./mvnw`), Lombok, MariaDB, Flyway |
| Frontend | Angular + Tauri (escritorio), `@stomp/rx-stomp` para WebSockets |
| CI/CD | Jenkins (`jenkins/`), Docker (`docker/`) |

Estructura raíz: `backend/`, `frontend/`, `e2e/`, `docker/`, `jenkins/`, `docs/`.

## Estructura del backend (`backend/src/main/java/com/medlab/main/`)

```
config/       configuraciones (SecurityConfig, WebSocketConfig)
controller/   controllers REST y WS (@Controller con @SubscribeMapping)
dto/          records DTO (request/response/WS)
entity/       entidades JPA (siempre sufijo *Entity)
exception/    excepciones + GlobalExceptionHandler
repository/   interfaces propias + interfaces JPA (XxxJpaRepository)
repository/impl/  implementaciones (XxxRepositoryImpl)
service/      interfaces de servicio (constantes de interfaz permitidas)
service/impl/ implementaciones (XxxServiceImpl)
shared/       utilidades compartidas (ApiResponse)
```

## Convenciones backend

### Repositorios (patrón obligatorio en 3 capas)

```
XxxRepository          // interfaz propia (dominio, nombres en inglés)
XxxJpaRepository       // interfaz extends JpaRepository, solo consultas
impl/XxxRepositoryImpl // @Repository, delega en la JpaRepository o usa @PersistenceContext
```

- Los services **solo** consumen las interfaces propias (`XxxRepository`), nunca las JPA.
- **`@Query` solo cuando sea estrictamente necesario** (consultas nativas que no se
  pueden expresar como derivadas). Preferir: consultas derivadas + `@EntityGraph`
  para el fetching. Ejemplos justificados de `@Query`: Haversine nativo en
  `FarmaciasJpaRepository`, `row_number` nativo en `CalificacionesJpaRepository`.

### Servicios (patrón obligatorio interfaz + impl)

- Todo servicio tiene interfaz `XxxService` en `service/` e impl `XxxServiceImpl`
  en `service/impl/`. Aplica también a helpers pequeños (`TiempoRelativoService`,
  `MensajeDisponibilidadService`, `MedicamentoDetalleNotificador`).
- Los services trabajan solo con **DTOs** y las interfaces de repositorio propias.
  Nunca exponen entidades fuera de la capa de servicio.
- Constantes de negocio en la interfaz del servicio (ej. `CALIFICACIONES_RECIENTES`).

### Entidades

- Sufijo obligatorio `*Entity` (`MedicamentosEntity`, `CalificacionEntity`…).
- Ids tipo `Long` (coinciden con `BIGINT` en el esquema).
- Sin `unique=true` en `@Column` (los constraints viven en Flyway).
- Campos como `fechaHoraReporte` se manejan como solo-lectura (DB los completa).

### Nombres (español/inglés: verbo en inglés + sustantivo de dominio en español)

- Métodos: `createMedicamento`, `getMedicamentoById`, `searchMedicamentosByTexto`,
  `getAllFarmaciasAprobadas`, `calculateTendenciaDisponibilidad`.
- Variables y parámetros: compuestas y explícitas, nunca cortas ni vagas
  (`medicamentoResponse`, `farmaciaCercanaDto`, `allowedOrigins`).
- Helpers de conversión: `toXxxDto` (`toMedicamentoBusquedaDto`, `toFarmaciaMapaDto`).
- Clases: singular (`MedicamentosController` gestiona medicamentos).
- Comentarios y Javadoc en **español**; identificadores en inglés con sustantivos
  de dominio en español cuando aplique.

### Respuestas y contratos

- **Todo endpoint REST** envuelve su respuesta en `shared/ApiResponse<T>`.
- Los `@SubscribeMapping` de WebSocket responden **DTO crudo** (sin envoltorio):
  es el contrato del frontend de la landing y no se puede cambiar.
- Validación con Jakarta (`@Validated`, `@NotBlank`, `@NotNull`, `@Positive`…)
  — `spring-boot-starter-validation` está en el pom.

### Excepciones

- REST: `GlobalExceptionHandler` (+ `@RestControllerAdvice`).
- WS: `@MessageExceptionHandler` en el controller WS (el cliente no recibe nada).
- Nombres en inglés: `MedicamentoNotFoundException`.

## WebSockets (`config/WebSocketConfig.java`)

- Endpoint **`/ws` con WebSocket crudo, sin SockJS** (el frontend conecta
  `brokerURL: ws://localhost:8080/ws` con rx-stomp; SockJS rompería ese handshake).
- Orígenes permitidos por propiedad **`spring.cors.allowed-origins`**
  (dev: `http://localhost:1420,tauri://localhost,http://tauri.localhost`).
- Broker simple en **`/ws`** (broadcasts del buscador: `/ws/medicamentos/...`) y
  **`/topic`** (detalle: `/topic/medicamentos/{id}`); prefijo de aplicación `/app`.
- Destinos del frontend (no cambiar sin coordinar):
  - `/app/medicamentos/buscar` (header `q`), `/app/medicamentos/detalle`
  - `/app/farmacias/aprobadas`, `/app/farmacias/cercanas` (headers `medicamentoId`, `latitud`, `longitud`)
  - `/topic/medicamentos/{id}` para cambios del detalle.

## Base de datos y configuración

- MariaDB + **Flyway**: esquema en `backend/src/main/resources/db/migration/V1__init_schema.sql`.
  Cambios de esquema = nuevas migraciones `Vn__*.sql`, nunca editar V1 en producción.
- Perfil dev: `application-dev.yml` está **gitignored** (local); la plantilla
  tracked es `application-dev.yml.example` — si se añade una propiedad nueva,
  actualizar el `.example` **y avisar** de que cada dev debe añadirla a su local.
  Si un `@Value` no tiene default, la propiedad debe existir en todos los perfiles
  activos o el arranque falla.

## Tests

- Misma estructura espejo bajo `backend/src/test/java/com/medlab/main/`.
- Mockito: mockear las **interfaces propias** de repository; las impls de servicios
  se instancian directamente (`new XxxServiceImpl(...)`) — no hay mocks de JPA.

## Commits y flujo

- Commits **atómicos** con mensajes en **español**.
- Antes de commitear: no usar `git add .` a ciegas en merges; añadir por directorio
  revisando `git status` (evitar archivar archivos con marcadores de conflicto).
- Verificar tras cambios grandes: `grep -rn "<<<<<<<" backend/src` y búsqueda de
  referencias a clases/métodos renombrados.

## Compilación

- **El agente nunca compila ni arranca la app.** El usuario verifica con:
  `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- Ejecutar tests: `./mvnw test` (solo si el usuario lo pide).
