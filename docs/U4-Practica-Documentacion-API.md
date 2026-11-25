# U4 - Práctica: Documentación de API con Spring Boot

**Estudiante:** Jose Alonso Macias Montoya  
**Fecha de entrega:** 11 de noviembre  
**Puntaje:** 100 puntos  
**Proyecto:** GeoTour API (`com.geotour:geotour-api:1.0.0`)

---

## 1. Objetivo

Instrumentar el proyecto Spring Boot con **Springdoc OpenAPI** para generar de forma automática y dinámica el contrato **OpenAPI 3.0**, modelar controladores y DTOs con anotaciones Java, publicar la documentación interactiva en **Swagger UI** y exportar el contrato y una colección de **Postman** con entornos de prueba.

---

## 2. Entorno de trabajo

| Componente | Versión / valor |
| --- | --- |
| Spring Boot | 3.3.5 |
| Java | 17 |
| Springdoc OpenAPI UI | 2.6.0 |
| Contrato generado | OpenAPI 3.0.1 |
| Base de datos | PostgreSQL (dev/prod), H2 en memoria para la verificación |
| Seguridad | Spring Security + JWT Bearer |
| Perfiles | `dev`, `prod` |

> La dependencia solicitada en el enunciado es `2.5.0`; se conserva `2.6.0` porque es la versión alineada con Spring Boot 3.3.5 y fue validada en la generación del contrato.

---

## 3. Implementación

### 3.1 Dependencia y configuración

- `pom.xml:86-90`: `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0`.
- `src/main/java/com/geotour/api/config/OpenApiConfig.java`: define `@Bean OpenAPI` con título, descripción, versión, contacto, licencia, servidor configurable (`app.openapi.server-url`) y el esquema de seguridad HTTP `bearerAuth` (JWT).
- `src/main/resources/application.yml:59-68`: habilita `/v3/api-docs` y `/swagger-ui.html`, con ordenamiento de operaciones por método y de etiquetas alfabético.
- `application-dev.yml` y `application-prod.yml`: sobreescriben la URL del servidor OpenAPI (`http://localhost:8080` vs `https://api.geotour.com`) y permiten desactivar la documentación con `OPENAPI_ENABLED` / `OPENAPI_UI_ENABLED`.

### 3.2 Acceso público a la documentación

`src/main/java/com/geotour/api/config/SecurityConfig.java:68-77` permite el acceso sin autenticación a:

```text
/v3/api-docs, /v3/api-docs/**, /v3/api-docs.yaml,
/swagger-ui/**, /swagger-ui.html, /webjars/**, /error
```

El resto de endpoints conserva las reglas por rol (`ROLE_USER`, `ROLE_ADMIN`) y el filtro JWT.

### 3.3 Anotaciones en controladores

Los seis controladores REST usan anotaciones de `io.swagger.v3.oas.annotations`:

- `@Tag` para agrupar: Autenticacion, Destinos, Reservas, Resenas, Resenas (Administracion), Clima.
- `@Operation` con `summary` y `description` en cada método HTTP.
- `@ApiResponses` / `@ApiResponse` con los códigos 200, 201, 204, 400, 401, 403, 404, 409, 500 y 502.
- `@Content(mediaType = "application/json", schema = @Schema(implementation = ...))` para enlazar cada respuesta con su DTO, incluidos los listados mediante `@ArraySchema`.
- `@SecurityRequirement(name = "bearerAuth")` en los endpoints protegidos y `@SecurityRequirements(value = {})` en los públicos.
- `@Parameter` para documentar los path variables.

Archivos: `AuthController.java`, `DestinoController.java`, `ReservaController.java`, `ResenaController.java`, `ResenaAdminController.java`, `ClimaController.java`.

### 3.4 Modelado de DTOs y validaciones

Los 12 DTOs de `com.geotour.api.dto` están documentados con `@Schema` (descripción, ejemplo, `requiredMode`, `minimum`, `maximum`, `maxLength`, `allowableValues`, `format`). Los DTOs de entrada además usan validaciones `jakarta.validation`: `@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax`, `@Email` y `@FutureOrPresent`, que Springdoc refleja como restricciones en el contrato.

Ejemplos: `DestinoDTO.java` (latitud/longitud, precio y capacidad), `ReservaDTO.java` (fecha futura y número de personas) y `ResenaDTO.java` (calificación 1 a 5).

### 3.5 Manejo global de excepciones

`src/main/java/com/geotour/api/exception/GlobalExceptionHandler.java` está anotado con `@RestControllerAdvice` y cada manejador con `@ApiResponse` + `@Content` apuntando a `ErrorResponseDTO`. De esta forma, los errores estandarizados aparecen documentados en Swagger para 400, 401, 403, 404, 405, 409, 500 y 502.

`ErrorResponseDTO` expone `timestamp`, `status`, `error`, `message`, `path` y `errors`.

### 3.6 Tipado estricto

No se usan `ResponseEntity<?>`, `Object` ni respuestas sin esquema: todos los métodos devuelven DTO concretos, listas tipadas, `void` con `@ResponseStatus` o `ResponseEntity<AuthResponseDTO>`. Cada `@ApiResponse` declara explícitamente el DTO devuelto o el DTO de error.

---

## 4. Entregables

| Entregable | Ubicación |
| --- | --- |
| Swagger UI interactivo | `http://localhost:8080/swagger-ui.html` |
| Especificación JSON dinámica | `http://localhost:8080/v3/api-docs` |
| Especificación YAML dinámica | `http://localhost:8080/v3/api-docs.yaml` |
| Contrato exportado | `docs/openapi.yaml` |
| Colección Postman | `docs/postman/GeoTour-API.postman_collection.json` |
| Entorno de desarrollo | `docs/postman/GeoTour-Dev.postman_environment.json` |
| Entorno de producción | `docs/postman/GeoTour-Prod.postman_environment.json` |
| Reporte | `docs/U4-Practica-Documentacion-API.md` |

### Resumen del contrato generado

| Métrica | Valor |
| --- | --- |
| Versión OpenAPI | 3.0.1 |
| Paths | 13 |
| Operaciones HTTP | 19 |
| Esquemas | 12 |
| Tags | 6 |
| Operaciones con `bearerAuth` | 12 |
| Códigos documentados | 200, 201, 204, 400, 401, 403, 404, 409, 500, 502 |

### Operaciones documentadas

| Método | Ruta | Autenticación | Éxito |
| --- | --- | --- | --- |
| POST | `/api/v1/auth/register` | Pública | 201 |
| POST | `/api/v1/auth/login` | Pública | 200 |
| GET | `/api/v1/destinos` | Pública | 200 |
| GET | `/api/v1/destinos/{id}` | Pública | 200 |
| POST | `/api/v1/destinos` | JWT ADMIN | 201 |
| PUT | `/api/v1/destinos/{id}` | JWT ADMIN | 200 |
| DELETE | `/api/v1/destinos/{id}` | JWT ADMIN | 204 |
| GET | `/api/v1/destinos/{destinoId}/clima` | Pública | 200 |
| GET | `/api/v1/destinos/{destinoId}/resenas` | Pública | 200 |
| POST | `/api/v1/destinos/{destinoId}/resenas` | JWT USER | 201 |
| GET | `/api/v1/destinos/{destinoId}/reseñas` | Pública | 200 |
| POST | `/api/v1/destinos/{destinoId}/reseñas` | JWT USER | 201 |
| DELETE | `/api/v1/resenas/{id}` | JWT USER autor / ADMIN | 204 |
| DELETE | `/api/v1/reseñas/{id}` | JWT USER autor / ADMIN | 204 |
| POST | `/api/v1/reservas` | JWT USER | 201 |
| GET | `/api/v1/reservas/mis-reservas` | JWT USER | 200 |
| PUT | `/api/v1/reservas/{id}/cancelar` | JWT USER dueño / ADMIN | 200 |
| GET | `/api/v1/reservas` | JWT ADMIN | 200 |
| DELETE | `/api/v1/reservas/{id}` | JWT ADMIN | 204 |

Las rutas con `ñ` se mantienen como alias de compatibilidad junto a su equivalente ASCII.

---

## 5. Procedimiento de verificación

1. **Compilación:** `./mvnw -DskipTests compile` → correcta.
2. **Generación del contrato:** se inició la aplicación con un perfil de pruebas usando H2 en memoria y se consultó `GET /v3/api-docs.yaml`; la respuesta HTTP 200 se exportó a `docs/openapi.yaml`. El documento resultante incluye `openapi: 3.0.1`, los 12 esquemas y los códigos de error de `GlobalExceptionHandler`.
3. **Swagger UI:** con el perfil `dev`, `http://localhost:8080/swagger-ui.html` muestra las seis etiquetas y permite **Try it out** sobre cada operación; los endpoints protegidos muestran el botón *Authorize* para el esquema `bearerAuth`.
4. **Postman:** importar `GeoTour-API.postman_collection.json` y uno de los dos archivos de entorno. La colección contiene 6 carpetas y 19 peticiones.
5. **Flujo sugerido en Postman:**
   1. Seleccionar `GeoTour-Dev` o `GeoTour-Prod` según el perfil activo (`application-dev.yml` / `application-prod.yml`).
   2. Ejecutar `POST /api/v1/auth/register` o `POST /api/v1/auth/login`.
   3. El script de prueba guarda automáticamente el `token` JWT en las variables de la colección.
   4. Ejecutar el resto de peticiones; las protegidas usan `Authorization: Bearer {{token}}`.
   5. Ajustar `destinoId`, `reservaId` y `resenaId` según los datos del entorno.
6. Cada petición incluye pruebas de código HTTP documentado, cuerpo JSON válido y tiempo de respuesta; las peticiones protegidas validan que el token exista.

---

## 6. Conclusiones

- El contrato OpenAPI deja de ser genérico: cada operación declara su propósito, parámetros, cuerpo de entrada, respuestas exitosas y respuestas de error con su DTO correspondiente.
- La documentación es dinámica: cualquier cambio en controladores o DTOs se refleja automáticamente al consultar `/v3/api-docs` y en Swagger UI.
- La seguridad quedó documentada con `bearerAuth` y, al mismo tiempo, las rutas de documentación son públicas sin debilitar el resto de la API.
- El manejo global de excepciones hace visibles en Swagger los formatos de error 4xx y 5xx, cumpliendo con el criterio de tipado estricto.
- La colección Postman y los entornos dev/prod permiten reproducir las pruebas tanto en local como contra el servidor de producción.
