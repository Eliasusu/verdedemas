# Documentación de Endpoints - VerdeDeMas API

> Este documento reemplaza a `.github/ENDPOINTS.md` (eliminado tras la Fase 0).
>
> **Base URL:** `http://localhost:8080/api`
>
> ⚠️ Este documento describe el **contrato real** del código tal como existe hoy (Fase 0 —
> auditoría, verificado 2026-08-23), no un contrato aspiracional. Cuando el comportamiento real
> difiere de lo semánticamente correcto, se indica explícitamente con una nota "Estado de manejo
> de errores". Ver `docs/phases/fase-0-auditoria-del-proyecto.md` para el diagnóstico completo.

---

## Índice de Endpoints

1. [Órdenes](#órdenes) — crear, obtener por ID, listar por teléfono de cliente
2. [Productos](#productos) — solo listado (sin detalle público, sin filtro por categoría, sin búsqueda, sin CRUD)
3. [Zonas de Entrega](#zonas-de-entrega) — solo listado (sin CRUD, solo 2 zonas cargadas: Norte y Sur)
4. [Categorías](#categorías) — CRUD completo (única con DTOs de request/response reales)

**Nota de arquitectura:** `Product` y `DeliveryZone` no tienen capa de DTO implementada — sus
controllers devuelven la **entidad JPA** serializada directamente por Jackson. `Category` y
`Order` sí usan DTOs de respuesta dedicados.

---

## Órdenes

### 1. Crear Orden

**Genera una orden y devuelve un link a WhatsApp**

- **Endpoint:** `POST /api/orders`
- **Content-Type:** `application/json`
- **Autenticación:** No requerida (MVP, `SecurityConfig` permite todo)
- **HTTP Status real:** `200 OK` — el controller (`OrderController.java:30-34`) **no** tiene
  `@ResponseStatus`, por lo tanto Spring devuelve `200`, no `201`, pese a crear un recurso nuevo.

#### Request Body

```json
{
  "customerName": "Juan Pérez",
  "customerPhone": "+5493416123456",
  "customerAddress": "Calle Corrientes 1234, Rosario",
  "deliveryZoneId": 1,
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ]
}
```

#### Validaciones reales (`CreateOrderRequest`, con `@Valid` en el controller)

| Campo | Validación | Mensaje |
|-------|-----------|---------|
| `customerName` | `@NotBlank`, `@Size(max=100)` | "El nombre es requerido" |
| `customerPhone` | `@NotBlank`, `@Pattern(^\+?[0-9]{10,}$)` | "Teléfono inválido: debe tener al menos 10 dígitos" |
| `customerAddress` | `@NotBlank`, `@Size(max=200)` | "La dirección es requerida" |
| `deliveryZoneId` | `@NotNull` | "Debes seleccionar una zona de entrega" |
| `items` | `@NotEmpty`, `@Valid` (valida cada item) | "Debes seleccionar al menos un producto" |
| `items[].productId` | `@NotNull` | "El ID del producto es requerido" |
| `items[].quantity` | `@NotNull`, `@Min(1)`, `@Max(999)` | "La cantidad debe ser mínimo 1" |

Estas validaciones **sí funcionan hoy**: al fallar, Spring Boot (manejo por defecto, sin
`GlobalExceptionHandler` custom) devuelve `400 Bad Request` con el body estándar de
`MethodArgumentNotValidException`.

#### Response real (`200 OK`)

```json
{
  "id": 1,
  "customerName": "Juan Pérez",
  "customerPhone": "+5493416123456",
  "customerAddress": "Calle Corrientes 1234, Rosario",
  "deliveryZone": {
    "id": 1,
    "name": "Zona Norte",
    "shippingCost": 300.00,
    "deliveryDay": "FRIDAY_PM"
  },
  "items": [
    { "productId": 1, "productName": "Base Vital de Vegetales", "quantity": 2, "priceAtTime": 450.00, "subtotal": 900.00 },
    { "productId": 2, "productName": "Mix de Quinoa y Verduras", "quantity": 1, "priceAtTime": 550.00, "subtotal": 550.00 }
  ],
  "subtotal": 1450.00,
  "shippingCost": 300.00,
  "totalPrice": 1750.00,
  "status": "SENT_TO_WHATSAPP",
  "whatsappLink": "https://wa.me/5493415830657?text=...",
  "deliveryDate": "Viernes 2026-08-28",
  "deliveryWindowMin": 5,
  "deliveryWindowMax": 6,
  "deliveryTimeRange": "17:00-20:00"
}
```

**Nota real:** el `whatsappLink` en este endpoint apunta al número del encargado
(`Constants.MANAGER_PHONE`), no al del cliente. La orden se guarda con estado `SENT_TO_WHATSAPP`
inmediatamente (no hay paso intermedio de confirmación por parte del vendedor).

#### Estado de manejo de errores

| Código | Caso | ¿Es real hoy? |
|--------|------|----------------|
| `200` | Orden creada (no `201`, ver nota arriba) | ✅ real |
| `400` | Falla de `@Valid` en el request body | ✅ real (manejo default de Spring) |
| `404` | Zona de entrega o producto inexistente (`ResourceNotFoundException`) | ⚠️ **NO real** — hoy da `500`, ver sección de errores al final |
| `500` | Cualquier excepción no controlada, incluyendo las de negocio de arriba | ✅ real (y hoy cubre casos que deberían ser 404) |

---

### 2. Obtener Orden por ID

- **Endpoint:** `GET /api/orders/{id}`
- **Parámetros:** `id` (path) — Long
- **Autenticación:** No requerida

#### Response (`200 OK`)

Mismo formato que la respuesta de creación (`OrderResponse`), regenerando el `whatsappLink` hacia
`MANAGER_PHONE`.

#### Estado de manejo de errores

| Código | Caso | ¿Es real hoy? |
|--------|------|----------------|
| `200` | Orden encontrada | ✅ real |
| `404` | Orden no encontrada | ⚠️ **NO real** — `OrderService.getOrderById` lanza `ResourceNotFoundException`, sin handler hoy resulta en `500` |
| `400` | `id` no numérico en el path | ✅ real (Spring resuelve `MethodArgumentTypeMismatchException` con `400` por defecto, sin necesidad de handler custom) |
| `500` | Error interno / el 404 de arriba hoy también cae acá | ✅ real |

---

### 3. Listar Órdenes por Teléfono de Cliente

- **Endpoint:** `GET /api/orders/customer/{phone}`
- **Parámetros:** `phone` (path) — string, tal como fue guardado en `customerPhone`
- **Autenticación:** No requerida
- **Descripción:** Retorna todas las órdenes de ese teléfono, recalculando `whatsappLink` para
  cada una.

⚠️ **Bug real detectado (Fase 0):** en este endpoint el `whatsappLink` se genera con
`order.getCustomerPhone()` como destinatario (`OrderService.java:142`), **no** con
`Constants.MANAGER_PHONE` como en los otros dos endpoints de Order. Es decir, el mismo campo
`whatsappLink` tiene semántica distinta según qué endpoint lo devolvió.

#### Response (`200 OK`)

```json
[
  {
    "id": 1,
    "customerName": "Juan Pérez",
    "customerPhone": "+5493416123456",
    "customerAddress": "Calle Corrientes 1234, Rosario",
    "deliveryZone": { "id": 1, "name": "Zona Norte", "shippingCost": 300.00, "deliveryDay": "FRIDAY_PM" },
    "items": [ { "productId": 1, "productName": "Base Vital de Vegetales", "quantity": 2, "priceAtTime": 450.00, "subtotal": 900.00 } ],
    "subtotal": 900.00,
    "shippingCost": 300.00,
    "totalPrice": 1200.00,
    "status": "SENT_TO_WHATSAPP",
    "whatsappLink": "https://wa.me/+5493416123456?text=...",
    "deliveryDate": "Viernes 2026-08-28",
    "deliveryWindowMin": 5,
    "deliveryWindowMax": 6,
    "deliveryTimeRange": "17:00-20:00"
  }
]
```

Si no hay órdenes para ese teléfono, retorna `[]` (no `404`) — esto es correcto semánticamente
para una colección.

#### Estado de manejo de errores

| Código | Caso |
|--------|------|
| `200` | Lista obtenida (puede ser vacía) — real |
| `500` | Error interno del servidor — real |

---

## Productos

⚠️ **Sin DTO real:** el controller devuelve la entidad JPA `Product` directamente
(`ProductController.java:25`). Los archivos `product/dto/request/CreateProductRequest.java` y
`product/dto/reponse/ProductResponse.java` existen pero están **vacíos y sin usar** (código
muerto). No hay endpoints de creación, edición ni borrado, ni `GET /{id}` público (el servicio
tiene `getProductById` pero solo lo usa `OrderService` internamente).

### 1. Listar Productos Activos

- **Endpoint:** `GET /api/products`
- **Autenticación:** No requerida

#### Response real (`200 OK`) — entidad `Product` completa, incluyendo `Category` anidada completa

```json
[
  {
    "id": 1,
    "name": "Base Vital de Vegetales",
    "description": "Vegetales salteados listos para combinar. Rinde 2-3 comidas.",
    "price": 450.00,
    "imageUrl": null,
    "servings": 3,
    "usages": "Salteados, Tartas, Guisos",
    "category": {
      "id": 1,
      "name": "Bases de Vegetales",
      "description": "Vegetales preparados listos para usar",
      "isActive": true,
      "createdAt": "2026-01-15T10:30:00",
      "updatedAt": "2026-01-15T10:30:00"
    },
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "Mix de Quinoa y Verduras",
    "description": "Quinoa cocida con vegetales frescos. Rinde 2 porciones.",
    "price": 550.00,
    "imageUrl": null,
    "servings": 2,
    "usages": "Bowl, Ensaladas",
    "category": {
      "id": 2,
      "name": "Bases de Cereales",
      "description": "Granos y legumbres cocidos",
      "isActive": true,
      "createdAt": "2026-01-15T10:30:00",
      "updatedAt": "2026-01-15T10:30:00"
    },
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  }
]
```

**Nota:** el ejemplo refleja el seed real (`V2__seed.sql`): 2 categorías ("Bases de Vegetales",
"Bases de Cereales") y 2 productos. `imageUrl` no está poblado en el seed actual.

#### Estado de manejo de errores

| Código | Caso |
|--------|------|
| `200` | Lista obtenida — real |
| `500` | Error interno del servidor — real |

---

## Zonas de Entrega

⚠️ **Sin DTO real:** el controller devuelve la entidad JPA `DeliveryZone` directamente
(`DeliveryZoneController.java:25`). Los archivos `deliveryzone/dto/request/CreateDeliveryZoneRequest.java`
y `deliveryzone/dto/reponse/DeliveryZoneResponse.java` existen vacíos y sin usar. No hay CRUD ni
`GET /{id}` público.

⚠️ **Estado real del seed (`V2__seed.sql`):** solo existen 2 zonas — **Zona Norte** y **Zona Sur**.
Este y Oeste no están cargadas.

### 1. Listar Zonas de Entrega Activas

- **Endpoint:** `GET /api/delivery-zones`
- **Autenticación:** No requerida

#### Response real (`200 OK`)

```json
[
  {
    "id": 1,
    "name": "Zona Norte",
    "description": "Fisherton, Alberdi, Rucci",
    "shippingCost": 300.00,
    "deliveryDay": "FRIDAY_PM",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "Zona Sur",
    "description": "Echesortu, Azcuénaga",
    "shippingCost": 300.00,
    "deliveryDay": "SATURDAY_AM",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  }
]
```

#### `deliveryDay`: String libre, no enum tipado

En la entidad, `deliveryDay` es `private String deliveryDay` (`DeliveryZone.java:39-41`), no el
enum `DeliveryDay` que existe en `shared/entity/DeliveryDay.java` pero que **no se usa en ningún
lado**. `OrderService` compara contra literales de string (`"FRIDAY_PM".equals(...)`). Los tres
valores soportados por código son:

| Valor | Horario |
|-------|---------|
| `FRIDAY_PM` | 17:00–20:00 |
| `SATURDAY_AM` | 09:00–13:00 |
| `SATURDAY_PM` | 15:00–19:00 (soportado en código, ninguna zona cargada lo usa hoy) |

#### Estado de manejo de errores

| Código | Caso |
|--------|------|
| `200` | Lista obtenida — real |
| `500` | Error interno del servidor — real |

---

## Categorías

Único agregado con DTOs de request/response reales (`category/dto/request/*`,
`category/dto/response/CategoryResponse.java`) y CRUD completo.

### 1. Listar Categorías Activas

- **Endpoint:** `GET /api/categories`

```json
[
  { "id": 1, "name": "Bases de Vegetales", "description": "Vegetales preparados listos para usar", "isActive": true, "createdAt": "2026-01-15T10:30:00", "updatedAt": "2026-01-15T10:30:00" },
  { "id": 2, "name": "Bases de Cereales", "description": "Granos y legumbres cocidos", "isActive": true, "createdAt": "2026-01-15T10:30:00", "updatedAt": "2026-01-15T10:30:00" }
]
```

| Código | Caso |
|--------|------|
| `200` | real |
| `500` | real |

### 2. Obtener Categoría por ID

- **Endpoint:** `GET /api/categories/{id}`

```json
{ "id": 1, "name": "Bases de Vegetales", "description": "Vegetales preparados listos para usar", "isActive": true, "createdAt": "2026-01-15T10:30:00", "updatedAt": "2026-01-15T10:30:00" }
```

| Código | Caso | ¿Real hoy? |
|--------|------|------------|
| `200` | Categoría encontrada | ✅ real |
| `404` | Categoría no encontrada | ⚠️ **NO real** — `CategoryService.getById` lanza `ResourceNotFoundException`, hoy resulta en `500` |
| `400` | `id` no numérico | ✅ real (default de Spring) |
| `500` | Interno / el 404 de arriba hoy también | ✅ real |

### 3. Crear Categoría

- **Endpoint:** `POST /api/categories`
- **HTTP Status:** `201 Created` (único endpoint de creación del proyecto con `@ResponseStatus`
  correcto)

#### Request Body

```json
{ "name": "Raíces", "description": "Zanahorias, remolachas, papas y otros tubérculos" }
```

#### Validaciones reales (`CreateCategoryRequest`, con `@Valid`)

| Campo | Validación |
|-------|-----------|
| `name` | `@NotBlank`, `@Size(max=100)`. Unicidad validada en `CategoryService.create` (no en DB a nivel de constraint expuesto vía DTO) |
| `description` | `@Size(max=255)`, opcional |

#### Response (`201 Created`)

```json
{ "id": 3, "name": "Raíces", "description": "Zanahorias, remolachas, papas y otros tubérculos", "isActive": true, "createdAt": "2026-08-23T16:53:40", "updatedAt": "2026-08-23T16:53:40" }
```

#### Estado de manejo de errores

| Código | Caso | ¿Real hoy? |
|--------|------|------------|
| `201` | Categoría creada | ✅ real |
| `400` | Falla de `@Valid` | ✅ real |
| `409`/`400` | Nombre duplicado (`BusinessException`) | ⚠️ **NO real** — hoy resulta en `500`, no en `409`/`400` |
| `500` | Interno / el caso de arriba hoy también | ✅ real |

### 4. Actualizar Categoría

- **Endpoint:** `PUT /api/categories/{id}`
- **HTTP Status:** `200 OK`

Mismas validaciones que la creación (`UpdateCategoryRequest`, campos idénticos a
`CreateCategoryRequest`).

| Código | Caso | ¿Real hoy? |
|--------|------|------------|
| `200` | Actualizada | ✅ real |
| `400` | Falla de `@Valid` | ✅ real |
| `404` | Categoría no encontrada | ⚠️ **NO real** — hoy `500` |
| `409`/`400` | Nombre duplicado | ⚠️ **NO real** — hoy `500` |
| `500` | Interno / los dos casos de arriba hoy también | ✅ real |

### 5. Eliminar (Desactivar) Categoría

- **Endpoint:** `DELETE /api/categories/{id}`
- **HTTP Status:** `204 No Content` (soft delete: `isActive = false`)

| Código | Caso | ¿Real hoy? |
|--------|------|------------|
| `204` | Desactivada | ✅ real |
| `404` | Categoría no encontrada | ⚠️ **NO real** — hoy `500` |
| `500` | Interno / el caso de arriba hoy también | ✅ real |

---

## Estado real del manejo de errores (aplica a TODOS los endpoints)

`shared/exception/GlobalExceptionHandler.java` es hoy una clase **vacía**, sin
`@RestControllerAdvice` ni ningún `@ExceptionHandler`. Esto significa:

- `ResourceNotFoundException` y `BusinessException` (`shared/exception/`) son `RuntimeException`
  sin `@ResponseStatus` propio → **no manejadas** → Spring Boot las traduce en
  `500 Internal Server Error` con el body genérico:

```json
{
  "timestamp": "2026-08-23T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/categories/999"
}
```

  en vez del `404`/`409` semánticamente correcto documentado arriba en cada endpoint.

- Las fallas de `@Valid` (`MethodArgumentNotValidException`) sí las resuelve el manejo por defecto
  de Spring Boot con `400 Bad Request` — esto **sí funciona correctamente hoy**, sin necesidad de
  handler custom.
- Errores de tipo de path variable (`id` no numérico) también resultan en `400` por el manejo por
  defecto de Spring.

**Pendiente de implementación** (no realizado en la Fase 0, solo documentado): un
`@RestControllerAdvice` que mapee `ResourceNotFoundException → 404` y `BusinessException →
409`/`400`, con un body de error consistente entre todos los endpoints.

---

## Otras observaciones de contrato

- **Sin versionado:** todos los paths son `/api/...`, sin `/api/v1/...`.
- **Sin paginación:** los tres listados (`/api/products`, `/api/delivery-zones`, `/api/categories`)
  devuelven arrays completos sin `page`/`size`.
- **CORS abierto:** `WebConfig.java` permite `allowedOrigins("*")` para todos los métodos en
  `/api/**` — coherente con el estado "MVP sin autenticación" pero a revisar antes de producción.
- **Sin autenticación:** `SecurityConfig.java` permite todas las requests (`permitAll()`).
- **Sin tests de controller:** `src/test/java` solo tiene un test de carga de contexto
  (`VerdedemasApplicationTests`); ningún endpoint tiene test de aceptación (`@WebMvcTest`/
  `MockMvc`), incluyendo el comportamiento de errores documentado en este archivo.

---

## Flujo de Integración Típico (cliente)

```
1. GET /api/products
   └─> Mostrar catálogo de productos

2. GET /api/delivery-zones
   └─> Mostrar opciones de entrega

3. POST /api/orders
   └─> Crear orden con productos y zona seleccionada (responde 200, no 201)
   └─> Obtener whatsappLink (apunta al encargado en creación y consulta por ID)

4. Linking.openURL(whatsappLink)
   └─> Abrir WhatsApp con mensaje pre-llenado
   └─> Usuario confirma y envía manualmente
```
