# Arquitectura — VerdeDeMas

> Este documento reemplaza y fusiona `.github/arquitecture/arquitecture.md` y
> `.github/notes/project-structure.md` (eliminados tras la Fase 0). Describe primero el **estado
> real y verificado del código** (Fase 0/1 del roadmap) y, en una sección separada al final, la
> **arquitectura objetivo** (Fase 2/5). No mezclar ambas secciones al leer o citar este documento:
> todo lo que aparece antes de "Arquitectura objetivo" es lo que el código hace HOY, verificado
> línea por línea el 2026-08-23 (ver `docs/phases/fase-0-auditoria-del-proyecto.md`).

## 1. Stack tecnológico real

```
Java 21 (pom.xml: <java.version>21</java.version>)
├─ Spring Boot 3.5.9 (spring-boot-starter-parent)
├─ Spring Web (spring-boot-starter-web)
├─ Spring Data JPA (spring-boot-starter-data-jpa)
├─ PostgreSQL 14+ (driver 42.7.1)
├─ Flyway 11.20.2 (flyway-core + flyway-database-postgresql)
├─ Bean Validation (spring-boot-starter-validation)
├─ Lombok (optional, excluido del jar final)
├─ Jackson (jackson-databind)
├─ Spring Security 3.x + JWT (jjwt 0.12.3) — dependencias presentes,
│   SIN implementación de autenticación real (ver Fase 0)
└─ Spring WebSocket — dependencia presente, SIN uso real en el código
```

`groupId` de Maven: `com.verdedemas` (`pom.xml`). **Paquete Java real:
`com.eliasit.verdedemas`.** Estos dos nombres NO coinciden — es un mismatch conocido, no un error
de este documento. Tiene una consecuencia real: la propiedad
`logging.level.com.verdedemas=DEBUG` en `application.properties` apunta al groupId de Maven y
nunca activa DEBUG para el código de la aplicación (paquete real: `com.eliasit.verdedemas`).

## 2. Estructura de paquetes real

Organización por **módulo de negocio** (no por capa técnica global), y dentro de cada módulo, por
capa técnica:

```
src/main/java/com/eliasit/verdedemas/
├── VerdedemasApplication.java
│
├── config/
│   ├── WebConfig.java        # CORS (allowedOrigins "*")
│   ├── SecurityConfig.java   # permitAll() + csrf disabled (sin auth real)
│   └── JpaConfig.java        # @EnableJpaAuditing (createdAt/updatedAt)
│
├── shared/
│   ├── entity/
│   │   ├── BaseEntity.java      # @MappedSuperclass: createdAt, updatedAt
│   │   └── DeliveryDay.java     # ⚠️ enum sin ninguna referencia en el resto
│   │                            #    del código (código muerto)
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java  # ⚠️ clase VACÍA, sin @ControllerAdvice
│   │   ├── ResourceNotFoundException.java
│   │   └── BusinessException.java
│   └── util/
│       ├── Constants.java
│       └── OrderStatus.java
│
├── category/                 # CRUD completo (list, getById, create, update, delete)
│   ├── entity/Category.java
│   ├── dto/
│   │   ├── request/CreateCategoryRequest.java   # real, usada por el controller
│   │   ├── request/UpdateCategoryRequest.java
│   │   ├── response/CategoryResponse.java       # real, usada por el controller
│   │   └── reponse/CreateCategoryRequest.java   # ⚠️ TYPO ("reponse" sin la "s"):
│   │                                             #    clase vacía, DUPLICADA y MUERTA
│   ├── repository/CategoryRepository.java
│   ├── service/CategoryService.java   # retorna entidades Category al controller
│   └── controller/CategoryController.java  # mapea Category → CategoryResponse
│
├── product/                  # Solo GET /api/products (list, sin filtros/paginación)
│   ├── entity/Product.java
│   ├── dto/
│   │   ├── request/CreateProductRequest.java  # ⚠️ vacía, sin uso
│   │   └── reponse/ProductResponse.java       # ⚠️ TYPO de paquete + vacía, sin uso
│   ├── repository/ProductRepository.java
│   ├── service/ProductService.java
│   └── controller/ProductController.java   # ⚠️ devuelve List<Product> (entidad cruda)
│
├── deliveryzone/              # Solo GET /api/delivery-zones (list)
│   ├── entity/DeliveryZone.java   # deliveryDay: String libre, no usa el enum DeliveryDay
│   ├── dto/
│   │   ├── request/CreateDeliveryZoneRequest.java  # ⚠️ vacía, sin uso
│   │   └── reponse/DeliveryZoneResponse.java       # ⚠️ TYPO de paquete + vacía, sin uso
│   ├── repository/DeliveryZoneRepository.java
│   ├── service/DeliveryZoneService.java
│   └── controller/DeliveryZoneController.java  # ⚠️ devuelve List<DeliveryZone> (entidad cruda)
│
└── order/                     # POST /, GET /{id}, GET /customer/{phone}
    ├── entity/Order.java, OrderItem.java   # relación bidireccional Order↔OrderItem
    ├── dto/
    │   ├── request/CreateOrderRequest.java   # completo, con Bean Validation
    │   └── response/OrderResponse.java       # completo, con mapeo manual en el service
    ├── repository/OrderRepository.java, OrderItemRepository.java
    ├── service/OrderService.java   # orquesta ProductService y DeliveryZoneService
    │                                # (NO accede a sus repositorios directamente:
    │                                #  esta es la colaboración correcta entre módulos)
    └── controller/OrderController.java
```

**Nota sobre el typo `dto/reponse`:** en tres módulos (`product`, `category`, `deliveryzone`) el
paquete de DTOs de salida está mal escrito como `reponse` en vez de `response`. En `product` y
`deliveryzone` esto no generó duplicados porque no existe la versión correcta — simplemente las
clases están vacías y sin usar. En `category` sí generó una clase duplicada y muerta:
`category/dto/reponse/CreateCategoryRequest.java` (vacía) convive con la versión real y usada
`category/dto/request/CreateCategoryRequest.java`. Este typo queda documentado tal cual está en el
código (no se corrigió durante la Fase 0, que fue solo de análisis); ver el to-do correspondiente
en `docs/phases/fase-0-auditoria-del-proyecto.md`.

**Endpoints implementados hoy:**

```
GET    /api/categories                    → Listar categorías activas
GET    /api/categories/{id}               → Detalle de categoría
POST   /api/categories                    → Crear categoría
PUT    /api/categories/{id}               → Actualizar categoría
DELETE /api/categories/{id}               → Eliminar (desactivar) categoría

GET    /api/products                      → Listar productos activos (sin filtros)

GET    /api/delivery-zones                → Listar zonas activas (solo Norte y Sur cargadas)

POST   /api/orders                        → Crear pedido + generar link de WhatsApp
GET    /api/orders/{id}                   → Detalle de pedido
GET    /api/orders/customer/{phone}       → Pedidos por teléfono de cliente
```

`product` y `deliveryzone` no tienen endpoints de creación/actualización/eliminación todavía; solo
`category` y `order` tienen su CRUD/flujo completo. Ver `docs/api/endpoints.md` para el contrato
detallado de cada endpoint.

## 3. Diagrama de capas actual (sin DDD)

El código sigue **capas técnicas simples** (Controller → Service → Repository → Entity),
organizadas por módulo de negocio. No hay separación entre dominio, aplicación e infraestructura,
no hay Value Objects, no hay Aggregates con invariantes propias, ni Domain Events. Las entidades
son POJOs Lombok (`@Data`) sin comportamiento de negocio propio: toda la lógica vive en `service/`.

```
┌────────────────────────────────────────────┐
│  HTTP Request (JSON)                        │
└──────────────────┬───────────────────────────┘
                    │
┌───────────────────▼───────────────────────────┐
│  Controller (@RestController)                 │
│  - Deserializa request, valida (@Valid)       │
│  - Delega al Service                          │
│  - category/order: mapea a DTO de salida      │
│  - product/deliveryzone: devuelve la entidad  │
│    directamente (NO sigue el patrón DTO)      │
└───────────────────┬───────────────────────────┘
                    │
┌───────────────────▼───────────────────────────┐
│  Service (@Service, @Transactional)           │
│  - Lógica de negocio y validaciones           │
│  - Colabora con otros Services (no accede a   │
│    repositorios de otros módulos: OrderService│
│    usa ProductService/DeliveryZoneService,    │
│    NO ProductRepository/DeliveryZoneRepository)│
└───────────────────┬───────────────────────────┘
                    │
┌───────────────────▼───────────────────────────┐
│  Repository (Spring Data JPA)                  │
└───────────────────┬───────────────────────────┘
                    │
┌───────────────────▼───────────────────────────┐
│  Entity (@Entity, Lombok @Data)                │
└───────────────────┬───────────────────────────┘
                    │
              PostgreSQL (Flyway)
```

**Un patrón correcto a preservar:** `OrderService` no accede a `ProductRepository` ni
`DeliveryZoneRepository`; colabora con `ProductService`/`DeliveryZoneService`. Esa es la forma
correcta de comunicación entre módulos en esta arquitectura por capas — evita el acoplamiento
cruzado a nivel de persistencia, aunque los módulos no sean Bounded Contexts en sentido DDD.

## 4. Manejo de excepciones — actual vs. objetivo

**Estado actual (verificado):** `shared/exception/GlobalExceptionHandler.java` es una clase
completamente vacía — sin `@ControllerAdvice`, sin ningún método `@ExceptionHandler`. Ni
`ResourceNotFoundException` ni `BusinessException` tienen `@ResponseStatus`. Consecuencia real:
hoy, cualquier excepción de negocio no controlada resulta en `500 Internal Server Error`, no en el
código HTTP que su nombre sugiere.

```
ResourceNotFoundException       → HOY: 500 (sin handler)  | OBJETIVO: 404
BusinessException                → HOY: 500 (sin handler)  | OBJETIVO: 400/409
DataIntegrityViolationException  → HOY: manejo default     | OBJETIVO: 409
MethodArgumentNotValidException  → HOY: 400 (ya resuelto por Spring Boot
                                             por defecto, sin necesidad de
                                             GlobalExceptionHandler)
```

**Diseño objetivo (no implementado):** un `@RestControllerAdvice` que capture estas excepciones y
devuelva un cuerpo JSON consistente:

```json
{
  "timestamp": "2026-08-23T10:30:00Z",
  "status": 404,
  "message": "Zona de entrega con ID 99 no encontrada",
  "path": "/api/orders"
}
```

## 5. Configuración técnica actual

Solo existe un archivo de configuración: `src/main/resources/application.properties` (sin
perfiles `dev`/`prod`):

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/verdedemas
spring.datasource.username=postgres
spring.datasource.password=root          # ⚠️ credencial en texto plano, committeada
spring.jpa.hibernate.ddl-auto=validate   # correcto: no autogenera esquema, usa Flyway
spring.jpa.show-sql=false
spring.flyway.enabled=true
logging.level.root=INFO
logging.level.com.verdedemas=DEBUG       # ⚠️ apunta al groupId Maven, NO al paquete
                                          #    real (com.eliasit.verdedemas) → no-op
```

**Flyway** (`src/main/resources/db/migration/`):
- `V1__init.sql` — crea 5 tablas: `categories`, `products`, `delivery_zones`, `orders`,
  `order_items`, con FKs y timestamps `NOT NULL`.
- `V2__seed.sql` — carga 2 categorías, 2 productos, 2 zonas de entrega (Norte, Sur — "Este"/"Oeste"
  mencionadas en comentarios/documentación no existen en el seed real).

**Seguridad** (`SecurityConfig.java`): CSRF deshabilitado, `permitAll()` en todos los requests —
no hay autenticación implementada pese a que las dependencias JWT están en `pom.xml`. **CORS**
(`WebConfig.java`): `allowedOrigins("*")` para `/api/**`. Razonable para el MVP actual sin login,
pero debe tratarse como deuda de seguridad explícita antes de producción (ver Fase 0).

## 6. Deuda técnica identificada (resumen)

Ver `docs/phases/fase-0-auditoria-del-proyecto.md` para el diagnóstico completo y priorizado.
Resumen de lo relevante a arquitectura:

- `GlobalExceptionHandler` vacío (excepciones de negocio → 500 en vez del código correcto).
- `product`/`deliveryzone` exponen entidades JPA crudas en la API pública.
- Typo de paquete `dto/reponse` en tres módulos, con una clase duplicada y muerta en `category`.
- DTOs vacíos y sin uso en `product`/`deliveryzone`.
- `logging.level.com.verdedemas` no controla el logging real de la app.
- Credenciales de BD en texto plano, sin perfiles de entorno.
- `@Data` de Lombok en entidades JPA con relación bidireccional `Order`↔`OrderItem` → riesgo de
  `StackOverflowError` en `toString`/`equals`/`hashCode`.
- Enum `DeliveryDay` sin usar; `DeliveryZone.deliveryDay` usa strings mágicos con lógica duplicada
  en `OrderService`.
- Uso de `java.util.logging.Logger` en vez de SLF4J en todos los controllers/services, con
  concatenación de strings en los mensajes.
- Dependencias JWT y WebSocket declaradas sin ningún uso en el código.
- Sin tests más allá de `contextLoads()`.

---

## Arquitectura objetivo (Fase 2/5 del roadmap)

⚠️ **Nada de esta sección está implementado hoy.** Es la dirección de evolución acordada en
`docs/roadmap.md`, a aplicar de forma incremental y solo cuando la complejidad del dominio lo
justifique — nunca por "limpieza" sin motivo de negocio o técnico (regla de arquitectura del
proyecto).

### Capas objetivo (Clean/Hexagonal — Fase 5)

```
Domain        → Entidades de negocio, Value Objects, Aggregates con
                 invariantes propias, servicios de dominio, eventos de
                 dominio, interfaces de repositorio. No depende de nada.
Application   → Casos de uso (application services), DTOs de entrada/
                 salida, puertos hacia infraestructura. Depende de Domain.
Infrastructure→ Implementación JPA de los repositorios, clientes HTTP,
                 mensajería. Depende de Application y Domain.
Adapters      → Controllers REST, manejadores de eventos, seguridad.
                 Dependen de Application (y de Infrastructure mediando
                 puertos).
```

### DDD táctico (Fase 2)

- **Value Objects** candidatos ya visibles en el código actual: dinero (`BigDecimal
  price/subtotal/shippingCost/totalPrice`), teléfono (`customerPhone` con regex de validación
  repetida), día/franja de entrega (el enum `DeliveryDay` ya existe pero no se usa — sería la base
  natural de un Value Object `DeliveryWindow`).
- **Aggregate Root** candidato: `Order` con `OrderItem` como parte del mismo aggregate (ya está
  modelado así a nivel JPA con `cascade = ALL, orphanRemoval = true` — falta la protección de
  invariantes en el propio objeto, no en el service).
- **Domain Events** candidatos: `OrderCreated`, `OrderSentToWhatsApp` (hoy esta transición de
  estado es un `setStatus()` directo en `OrderService.createAndSendToWhatsApp`).
- **Bounded Contexts:** no se introducen mientras exista un único modelo de datos relacional
  compartido sin fronteras de consistencia definidas (regla de arquitectura del proyecto) —
  evaluar recién si/cuando el dominio lo justifique.

### Manejo de excepciones objetivo

Ver sección 4 arriba — `@RestControllerAdvice` centralizado, con mapeo explícito de excepciones de
dominio a códigos HTTP.

### Configuración por ambiente objetivo

```properties
# application-dev.properties (no existe todavía)
spring.datasource.url=jdbc:postgresql://localhost:5432/verdedemas_dev
spring.jpa.show-sql=true
logging.level.com.eliasit.verdedemas=DEBUG

# application-prod.properties (no existe todavía)
spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
server.ssl.enabled=true
```

### Escalabilidad futura (referencial, sin compromiso de fecha)

```
Fase 1 (actual):  instancia única, PostgreSQL single node, sin cache.
Fase 2 (mediano): load balancer, múltiples instancias, cache (Redis),
                  réplicas de lectura.
Fase 3 (grande):  Kubernetes, posibles microservicios si hay una razón de
                  negocio/técnica real (regla de arquitectura del proyecto:
                  no se introducen sin justificación), Kafka si hay caso de
                  uso concreto de eventos asíncronos.
```
