# Copilot Instructions - VerdeDeMas Backend

## Communication Style

**Respuestas concisas y mentoría:**
- Máximo 3 párrafos en respuestas normales
- Código completo solo si explícitamente se pide
- Primero guía y razonamiento, luego dónde implementar
- Explicar el "por qué" de decisiones arquitectónicas
- Nunca dar paredes de texto innecesarias
- Asumir seniority del desarrollador—no sobrexplicar lo obvio
- Si pregunta sobre código: dar dirección como senior mentoreando a junior

---

## Project Overview
VerdeDeMas is a Spring Boot e-commerce backend for a **verdulería** (vegetable shop) in Rosario, Argentina. MVP focuses on order creation with WhatsApp integration—no active order tracking yet ("fire and forget").

**Tech Stack:** Java 21 | Spring Boot 3.5.9 | PostgreSQL | Flyway | Maven | Lombok  
**Database:** PostgreSQL 14+ with Flyway migrations (`src/main/resources/db/migration/`)

---

## Architecture & Code Patterns

### Layered Architecture (4-tier)
1. **Controllers** (`src/main/java/com/eliasit/verdedemas/*/controller/`)
   - REST endpoints, @Valid DTOs, delegate to Service layer
   
2. **Services** (`*/service/`) — Core business logic
   - Orchestrate repositories & external integrations
   - Apply business rules from `.github/bussines-rules/bussines-rules.md`
   - Marked with `@Service`, `@RequiredArgsConstructor`, `@Transactional`
   
3. **Repositories** (`*/repository/`)
   - Spring Data JPA (extends `JpaRepository<Entity, ID>`)
   - Custom @Query when needed
   
4. **Entities** (`*/entity/`)
   - Extend `BaseEntity` (provides `createdAt`, `updatedAt` via `@CreatedDate`, `@LastModifiedDate`)
   - Use BigDecimal for money fields
   - Relationships: Product ← Category, Order ←→ OrderItem → Product, Order → DeliveryZone

### Module Organization
Each domain has: `controller/`, `service/`, `repository/`, `entity/`, `dto/{request,response}/`
- **order**: Order creation flow + WhatsApp link generation
- **product**: Product catalog management
- **category**: Product categorization
- **deliveryzone**: Delivery cost & scheduling
- **shared**: `BaseEntity`, `OrderStatus` enum, `GlobalExceptionHandler`, `Constants`

---

## Critical Business Rules

### Order Creation Workflow
1. **Validate period**: `validateOrderPeriod()` is currently a no-op — orders are accepted **any day of the week**. (Historical/discarded rule, no longer enforced: Sun–Wed accepted, Thu–Sat rejected.)
2. **Calculate delivery dates**: `daysMin = days to next Friday`, `daysMax = days to next Saturday`
3. **Validate delivery zone**: Must exist, be active, have shipping cost & assigned delivery time
4. **Calculate total**: `subtotal = sum(product.price × quantity)`, then add zone's `shippingCost`
5. **Create Order + OrderItems**: Cascade save; set `status = OrderStatus.PENDING`
6. **Generate WhatsApp message**: Format order details with estimated delivery date/time, URL-encode, create `wa.me/{phone}?text=...` link
7. **Update status**: Set to `SENT_TO_WHATSAPP`, record `sentToWhatsappAt`

**Key file:** [src/main/java/com/eliasit/verdedemas/order/service/OrderService.java](src/main/java/com/eliasit/verdedemas/order/service/OrderService.java)

### Delivery Schedule (Rosario)
- **Zones implemented (seeded in `V2__seed.sql`):** Norte ($300, `FRIDAY_PM`), Sur ($300, `SATURDAY_AM`)
- **Zones planned, not implemented:** Este, Oeste — no seed data, no rows in the DB yet
- **Delivery times:** Viernes PM (17:00–20:00), Sábado AM (09:00–13:00), Sábado PM (15:00–19:00)
- **Zone entity field:** `deliveryDay` is a plain `String` (not a typed enum) that holds one of `FRIDAY_PM`, `SATURDAY_AM`, `SATURDAY_PM` by convention. An unused `DeliveryDay` enum exists in `shared/entity/` but `DeliveryZone` does not reference it.

### Product & Category Naming
- **Use descriptive names:** "Base Vital de Vegetales", not "Mix vegetal 500g"
- **Descriptions must include:** (1) What it is, (2) Problem it solves, (3) Rendimiento (meals/servings), (4) Possible uses
- **Categories:** ≥2 products per category; only admins create categories (future feature)

---

## Build & Development Commands

```bash
# Build
./mvnw clean package

# Run locally
./mvnw spring-boot:run

# Tests (future)
./mvnw test

# Database migrations (auto-applied on startup via Flyway)
# See: spring.flyway.locations=classpath:db/migration
```

**Database setup:**
```bash
# Create PostgreSQL database
createdb verdedemas

# Configure connection in src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/verdedemas
spring.datasource.username=postgres
spring.datasource.password=root
```

---

## Key Integration Points

### WhatsApp Integration
- **Base URL:** `Constants.WHATSAPP_BASE_URL = "https://wa.me/"`
- **Format:** `https://wa.me/{phoneNumber}?text={urlEncodedMessage}`
- **Encoding:** Use `URLEncoder.encode(message, StandardCharsets.UTF_8)`
- **Generated in:** `OrderService.generateWhatsAppLink(phone, message)`
- **Frontend:** React Native calls `Linking.openURL(whatsappLink)` to open WhatsApp

### Exception Handling
- **Custom exceptions:** `ResourceNotFoundException`, `BusinessException` (in `shared/exception/`), neither annotated with `@ResponseStatus`
- **Global handler:** `GlobalExceptionHandler` is an empty class — no `@ControllerAdvice`, no `@ExceptionHandler` methods. It does nothing today.
- **Current behavior:** thrown `ResourceNotFoundException`/`BusinessException` are unhandled and fall through to Spring Boot's default error handling (typically surfaced as `500`, not the `404`/`400` the docs describe as the intended contract)
- **DTO validation:** `@Valid` on controller params triggers Bean Validation, but failures are **not** caught by any custom handler — Spring Boot's default validation error response is returned instead of a project-defined JSON shape

### Configuration
- **JPA Auditing:** `@EnableJpaAuditing` in [src/main/java/com/eliasit/verdedemas/config/JpaConfig.java](src/main/java/com/eliasit/verdedemas/config/JpaConfig.java) enables `@CreatedDate` / `@LastModifiedDate`
- **Future:** Security config scaffolded (dependencies present: Spring Security, JWT)

---

## Project-Specific Conventions

- **Dates:** Use `LocalDate` for dates, `LocalDateTime` for timestamps; calculate delivery dates via `java.time.temporal.TemporalAdjusters` (see OrderService)
- **Money:** Always use `BigDecimal` with precision = 10, scale = 2
- **Logging:** `Logger log = Logger.getLogger(ClassName.class.getName())`
- **Lombok:** Use `@RequiredArgsConstructor` for DI, `@Data`, `@EqualsAndHashCode(callSuper=true)` for entities extending `BaseEntity`
- **DTOs:** Separate `request/` and `response/` subdirectories; use clear naming (e.g., `CreateOrderRequest`, `OrderResponse`). Known typo, not yet refactored: in `product` and `deliveryzone` the response package is actually spelled `dto/reponse/` (missing the "s"), and in `category` there's a dead duplicate class at `dto/reponse/CreateCategoryRequest.java` (the real one lives in `dto/request/`) — don't "fix" the typo without being asked, it's tracked as a known issue.

---

## Important Files to Know

- **Business logic:** `.github/bussines-rules/bussines-rules.md` (product naming, zone rules, order periods)
- **Architecture details:** `.github/arquitecture/arquitecture.md` (entity relations, request/response flows)
- **Constants & messages:** [src/main/java/com/eliasit/verdedemas/shared/util/Constants.java](src/main/java/com/eliasit/verdedemas/shared/util/Constants.java)
- **Enum values:** [src/main/java/com/eliasit/verdedemas/shared/util/OrderStatus.java](src/main/java/com/eliasit/verdedemas/shared/util/OrderStatus.java)
- **DB migrations:** `src/main/resources/db/migration/` (Flyway scripts, auto-run on `spring.jpa.hibernate.ddl-auto=validate`)

---

## Future Features (Scaffolded, Not Active)

- **Authentication:** Spring Security + JWT (dependencies present)
- **Real-time updates:** WebSocket starter already included
- **Order tracking:** `Order.status` enum ready for CONFIRMED, DELIVERED states
- **Admin panel:** Security config ready for role-based access
- **Time-series analytics:** Timescaledb optional dependency noted

Avoid implementing these until explicitly required—maintain MVP simplicity.
