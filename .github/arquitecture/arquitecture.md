# Arquitectura - VerdeDeMas E-Commerce

## 1. Stack Tecnológico

### Backend
```
Java 21
├─ Spring Boot 3.5.9          (Framework web)
├─ Spring Data JPA             (ORM)
├─ Spring Security 3.x         (Futuro: autenticación)
├─ JWT (jjwt 0.12.3)          (Futuro: tokens)
├─ Spring WebSocket            (Futuro: real-time)
└─ Validation (Bean Validation)
```

### Database
```
PostgreSQL 14+
├─ Flyway                      (Migrations)
├─ Timescaledb (opcional)      (Futuro: time-series)
└─ pgAdmin (admin, opcional)
```

### Build & Dependency Management
```
Maven 3.9+
└─ pom.xml                     (Gestor de dependencias)
```

### Testing (Futuro)
```
JUnit 5
├─ Mockito
├─ AssertJ
└─ Spring Boot Test
```

### Frontend (Futuro)
```
React Native
├─ Axios                       (HTTP client)
├─ AsyncStorage                (Local storage)
└─ React Navigation            (Routing)
```

---

## 2. Arquitectura de Capas

```
┌─────────────────────────────────────────────┐
│         HTTP Requests (REST API)            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Controller Layer                          │
│  (ProductController, OrderController, etc)  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Service Layer                             │
│  (ProductService, OrderService, etc)        │
│  - Lógica de negocio                        │
│  - Validaciones                             │
│  - Orquestación                             │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Repository Layer (Data Access)           │
│  (ProductRepository, OrderRepository, etc)  │
│  - JPA Queries                              │
│  - Custom @Query                            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Database Layer                            │
│  (PostgreSQL)                               │
└─────────────────────────────────────────────┘
```

---

## 3. Flujo de Solicitud - Ejemplo: Crear Orden

```
Frontend (React Native)
      │
      │ POST /api/orders
      │ Content-Type: application/json
      │ {
      │   "customerName": "Juan",
      │   "customerPhone": "+5491123456789",
      │   "customerAddress": "Calle 123, La Plata",
      │   "deliveryZoneId": 1,
      │   "items": [
      │     { "productId": 5, "quantity": 2 },
      │     { "productId": 10, "quantity": 1 }
      │   ]
      │ }
      ▼
OrderController.create()
      │
      ├─ @Valid → Valida CreateOrderRequest
      │          (Si falla → GlobalExceptionHandler retorna 400)
      │
      ▼
OrderService.createAndSendToWhatsApp()
      │
      ├─ getZoneById(deliveryZoneId)          (Busca zona)
      ├─ getProductById(productIds[])         (Busca productos)
      ├─ calculateSubtotal()                  (Suma precios)
      ├─ calculateTotal(subtotal + shipping)  (Total)
      ├─ save(Order)                          (Persiste en BD)
      ├─ saveAll(OrderItems)                  (Líneas de orden)
      ├─ generateWhatsAppMessage()            (Formatea mensaje)
      ├─ generateWhatsAppLink()               (Codifica URL)
      └─ updateOrderStatus(SENT_TO_WHATSAPP)  (Actualiza estado)
      │
      ▼
OrderRepository.save()
OrderItemRepository.saveAll()
      │
      ▼
PostgreSQL (INSERT)
      │
      ▼
OrderController retorna OrderResponse
      │
      │ 201 CREATED
      │ {
      │   "id": 42,
      │   "customerName": "Juan",
      │   "subtotal": 450.00,
      │   "shippingCost": 150.00,
      │   "totalPrice": 600.00,
      │   "deliveryZone": { "id": 1, "name": "Zona Centro" },
      │   "whatsappLink": "https://wa.me/5491123456789?text=..."
      │ }
      │
      ▼
Frontend
      │
      ├─ Abre WhatsApp con link: Linking.openURL(whatsappLink)
      │
      ▼
Usuario confirma en WhatsApp
```

---

## 4. Mapeo de Entidades a Base de Datos

```
┌─ Product (Entidad JPA)
│  └─ @Entity @Table("products")
│     ├─ id: Long
│     ├─ name: String
│     ├─ description: String
│     ├─ price: BigDecimal
│     ├─ category: Category (ManyToOne)
│     └─ timestamps (heredados de BaseEntity)
│
├─ Category
│  └─ @Entity @Table("categories")
│     ├─ id: Long
│     ├─ name: String
│     ├─ description: String
│     ├─ products: List<Product> (OneToMany)
│     └─ timestamps
│
├─ DeliveryZone
│  └─ @Entity @Table("delivery_zones")
│     ├─ id: Long
│     ├─ name: String
│     ├─ description: String
│     ├─ shippingCost: BigDecimal
│     ├─ deliveryDay: String              (NO es un enum tipado; valores usados por convención:
│     │                                    "FRIDAY_PM", "SATURDAY_AM", "SATURDAY_PM". Existe un
│     │                                    enum `DeliveryDay` en shared/entity/ pero no se usa aquí)
│     ├─ isActive: Boolean
│     └─ timestamps
│
├─ Order
│  └─ @Entity @Table("orders")
│     ├─ id: Long
│     ├─ customerName: String
│     ├─ customerPhone: String
│     ├─ customerAddress: String
│     ├─ deliveryZone: DeliveryZone (ManyToOne)
│     ├─ items: List<OrderItem> (OneToMany)
│     ├─ subtotal: BigDecimal
│     ├─ shippingCost: BigDecimal
│     ├─ totalPrice: BigDecimal
│     ├─ status: OrderStatus (Enum)
│     ├─ sentToWhatsappAt: LocalDateTime
│     └─ timestamps
│
└─ OrderItem
   └─ @Entity @Table("order_items")
      ├─ id: Long
      ├─ order: Order (ManyToOne)
      ├─ product: Product (ManyToOne)
      ├─ quantity: Integer
      ├─ priceAtTime: BigDecimal
      └─ timestamps
```

---

## 5. Diagrama de Relaciones (ER)

```
categories (1) ──────── (N) products
                              │
                              │ (N)
                              │
                    order_items (N) ──── (1) orders
                              │
                              │ (1)
                              │
                        delivery_zones (1)
```

```sql
categories
   │
   └─ PK: id
      
products
   ├─ PK: id
   └─ FK: category_id → categories(id)
   
order_items
   ├─ PK: id
   ├─ FK: order_id → orders(id)
   └─ FK: product_id → products(id)
   
orders
   ├─ PK: id
   ├─ FK: delivery_zone_id → delivery_zones(id)
   └─ status: ENUM
   
delivery_zones
   └─ PK: id
```

---

## 6. Organización de Paquetes (estado actual: capas técnicas por módulo)

**Estado real (verificado en código):** esto NO es Domain-Driven Design. Es una arquitectura por capas técnicas (Controller → Service → Repository → Entity), organizada en paquetes por entidad de negocio — un patrón cercano a *transaction script* con separación por módulo. No hay Value Objects, Aggregates con invariantes, Domain Events, ni lógica de dominio encapsulada en las entidades: las entidades son POJOs anotados con Lombok `@Data`/`@NoArgsConstructor`/`@AllArgsConstructor` sin comportamiento propio, y toda la lógica vive en la capa `service/`.

```
product/                    ← Módulo: Gestión de Productos
├── entity/
├── dto/
├── repository/
├── service/
└── controller/

category/                   ← Módulo: Gestión de Categorías
├── entity/
├── dto/
├── repository/
├── service/
└── controller/

deliveryzone/              ← Módulo: Gestión de Entregas
├── entity/
├── dto/
├── repository/
├── service/
└── controller/

order/                     ← Módulo: Gestión de Órdenes
├── entity/
├── dto/
├── repository/
├── service/
└── controller/
```

Cada módulo agrupa sus propias capas técnicas y colabora con otros a través del service layer (p. ej. `OrderService` depende de `ProductService` y `DeliveryZoneService`), pero **no son Bounded Contexts** en el sentido DDD: comparten un único modelo de datos relacional, sin fronteras de consistencia ni lenguaje ubicuo definidos por módulo.

**Objetivo futuro (ver Fase 2 del roadmap):** evolucionar hacia un diseño más cercano a DDD táctico (Value Objects, agregados con invariantes propias, y eventualmente bounded contexts reales) si la complejidad del dominio lo justifica. Esto **todavía no está implementado** — es una aspiración documentada, no el estado actual del código.

---

## 7. Flujo de Validación

```
Cliente (React Native)
      │
      ▼ POST /api/orders
┌──────────────────────────┐
│   @Valid Annotation      │  ← Valida estructura JSON
│   (Bean Validation)      │    Si falla → 400 Bad Request
└──────────────────────────┘
      │ ✅ JSON válido
      ▼
┌──────────────────────────┐
│   OrderService           │  ← Valida lógica de negocio
│   - Zona existe?         │
│   - Productos existen?   │
│   - Precio válido?       │
│   - Stock disponible?    │  (Futuro)
└──────────────────────────┘
      │ ✅ Validaciones OK
      ▼
┌──────────────────────────┐
│   Guardar en BD          │
│   Generar WhatsApp       │
│   Retornar OrderResponse │
└──────────────────────────┘
```

---

## 8. Manejo de Excepciones

⚠️ **Estado actual: esto es el diseño objetivo, NO lo implementado.** `GlobalExceptionHandler` (`shared/exception/GlobalExceptionHandler.java`) es hoy una clase completamente vacía: sin `@ControllerAdvice`, sin ningún método `@ExceptionHandler`. Tampoco `ResourceNotFoundException` ni `BusinessException` tienen `@ResponseStatus`. En consecuencia, ninguna excepción de negocio se captura ni se formatea como se describe abajo; Spring Boot las resuelve con su manejo de errores por defecto (una excepción no controlada como `ResourceNotFoundException` termina en `500 Internal Server Error`, no en `404`). Implementar este handler es trabajo pendiente.

```
┌─ Exception
│  │
│  ├─ ResourceNotFoundException
│  │  └─ Cuando product, category, zone no existen
│  │     Código previsto: 404 NOT_FOUND (hoy: 500, sin handler)
│  │
│  ├─ BusinessException
│  │  └─ Errores de lógica de negocio
│  │     Código previsto: 400 BAD_REQUEST (hoy: 500, sin handler)
│  │
│  ├─ DataIntegrityViolationException
│  │  └─ Violación de constraints BD
│  │     Código previsto: 409 CONFLICT (hoy: manejo por defecto de Spring Boot)
│  │
│  └─ MethodArgumentNotValidException
│     └─ Fallan @Valid validations
│        Código: 400 BAD_REQUEST (este sí lo resuelve el manejo por defecto de Spring Boot,
│                                  sin necesidad de GlobalExceptionHandler)
│
└─ GlobalExceptionHandler (objetivo futuro, NO implementado)
   ├─ Capturaría todas las excepciones
   ├─ Formatearía respuesta JSON
   └─ Retornaría status HTTP apropiado
```

**Ejemplo de respuesta de error (formato objetivo, aún no producido por el código real):**
```json
{
  "timestamp": "2026-01-17T10:30:00Z",
  "status": 404,
  "message": "Zona de entrega con ID 99 no encontrada",
  "path": "/api/orders"
}
```

---

## 9. Integración WhatsApp

```
┌─────────────────────────────────────────────┐
│  OrderService.generateWhatsAppMessage()     │
└────────────────────┬────────────────────────┘
                     │
         StringBuilder para armar mensaje
              con datos de orden
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  🌱 *NUEVO PEDIDO VERDEDEMAS*               │
│  👤 *Cliente:* Juan García                  │
│  📍 *Dirección:* Calle 123                  │
│  🚚 *Zona:* Centro                          │
│  ─────────────────────                      │
│  *PRODUCTOS:*                               │
│  • Base Vital x2                            │
│  • Legumbres x1                             │
│  ─────────────────────                      │
│  💰 *Subtotal:* $450                        │
│  🚚 *Envío:* $150                           │
│  *TOTAL:* $600                              │
│  ¿Confirmás?                                │
└──────────────────┬──────────────────────────┘
                   │
    URLEncoder.encode(mensaje, UTF-8)
                   │
                   ▼
┌──────────────────────────────────────────────────┐
│  https://wa.me/5491123456789?text=...mensaje... │
└──────────────────┬───────────────────────────────┘
                   │
      Frontend: Linking.openURL(whatsappLink)
                   │
                   ▼
          Abre WhatsApp con mensaje
                prearmado
```

---

## 10. Configuración por Ambiente

⚠️ **Estado real:** hoy solo existe `src/main/resources/application.properties` (sin perfiles). Los bloques `application-dev.properties` / `application-prod.properties` de abajo son el diseño objetivo para separar ambientes, **todavía no creados**.

```yaml
# application.properties (real, único archivo existente hoy)
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/verdedemas
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.root=INFO
logging.level.com.verdedemas=DEBUG    # nota: así está en el código, con el groupId Maven,
                                       # no con el paquete Java real (com.eliasit.verdedemas)

# application-dev.properties (⏳ objetivo futuro, no existe todavía)
spring.datasource.url=jdbc:postgresql://localhost:5432/verdedemas_dev
spring.jpa.show-sql=true
logging.level.com.eliasit.verdedemas=DEBUG

# application-prod.properties (⏳ objetivo futuro, no existe todavía)
spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
server.ssl.enabled=true
```

---

## 11. Flujo de Deployment (Futuro)

```
Código en GitHub
      │
      ▼
GitHub Actions (CI)
      ├─ Maven test
      ├─ Maven build
      └─ Docker build
            │
            ▼
Docker Registry (DockerHub / ECR)
      │
      ▼
Deploy a servidor
      ├─ Flyway migrations
      ├─ Start Spring Boot
      └─ Health check
            │
            ▼
Producción
```

---

## 12. Consideraciones de Escalabilidad

### Fase 1 (Actual)
- Single instance Spring Boot
- PostgreSQL single node
- No caching
- API simple REST

### Fase 2 (Mediano)
- Load balancer (NGINX)
- Multiple Spring Boot instances
- Redis cache
- Database read replicas
- CDN para imágenes

### Fase 3 (Grande)
- Kubernetes
- Microservicios (product, order, user)
- Event-driven (Kafka)
- Database sharding
- API Gateway
