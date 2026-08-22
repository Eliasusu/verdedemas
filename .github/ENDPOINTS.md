# Documentación de Endpoints - VerdeDeMas API

**Base URL:** `http://localhost:8080/api`

---

## 📋 Índice de Endpoints

1. [Órdenes](#órdenes) — crear, obtener por ID, listar por teléfono de cliente
2. [Productos](#productos) — solo listado (sin detalle, sin filtro por categoría, sin búsqueda)
3. [Zonas de Entrega](#zonas-de-entrega) — solo listado (sin CRUD, solo 2 zonas cargadas: Norte y Sur)
4. [Categorías](#categorías) — CRUD completo

---

## 🛒 Órdenes

### 1. Crear Orden
**Genera una orden y envía el enlace a WhatsApp**

- **Endpoint:** `POST /api/orders`
- **Content-Type:** `application/json`
- **Autenticación:** No requerida (MVP)

#### Request Body

```json
{
  "customerName": "Juan Pérez",
  "customerPhone": "+5493416123456",
  "customerAddress": "Calle Corrientes 1234, Rosario",
  "deliveryZoneId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

#### Validaciones

| Campo | Validación | Mensaje |
|-------|-----------|---------|
| `customerName` | Requerido, máx 100 caracteres | "El nombre es requerido" |
| `customerPhone` | Requerido, patrón `^\\+?[0-9]{10,}$` | "Teléfono inválido: debe tener al menos 10 dígitos" |
| `customerAddress` | Requerido, máx 200 caracteres | "La dirección es requerida" |
| `deliveryZoneId` | Requerido | "Debes seleccionar una zona de entrega" |
| `items` | Lista no vacía | "Debes seleccionar al menos un producto" |
| `items[].productId` | Requerido | "El ID del producto es requerido" |
| `items[].quantity` | Requerido, entre 1 y 999 | "La cantidad debe ser mínimo 1" |

#### Response (201 Created / 200 OK)

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
    {
      "productId": 1,
      "productName": "Base Vital de Vegetales",
      "quantity": 2,
      "priceAtTime": 250.00,
      "subtotal": 500.00
    },
    {
      "productId": 3,
      "productName": "Espinaca Fresca",
      "quantity": 1,
      "priceAtTime": 80.00,
      "subtotal": 80.00
    }
  ],
  "subtotal": 580.00,
  "shippingCost": 300.00,
  "totalPrice": 880.00,
  "status": "SENT_TO_WHATSAPP",
  "whatsappLink": "https://wa.me/5493416123456?text=Hola%20VerdeDeMas...",
  "deliveryDate": "Viernes 2026-01-24",
  "deliveryWindowMin": 3,
  "deliveryWindowMax": 4,
  "deliveryTimeRange": "17:00-20:00"
}
```

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200/201` | Orden creada exitosamente |
| `400` | Error en validación de datos |
| `404` | Zona de entrega o producto no encontrado |
| `500` | Error interno del servidor |

**Nota:** La orden se crea automáticamente con estado `SENT_TO_WHATSAPP`. El `whatsappLink` debe abrirse con `Linking.openURL()` desde React Native.

---

### 2. Obtener Orden por ID

- **Endpoint:** `GET /api/orders/{id}`
- **Parámetros:** 
  - `id` (Path) - ID de la orden
- **Autenticación:** No requerida (MVP)

#### Response (200 OK)

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
    {
      "productId": 1,
      "productName": "Base Vital de Vegetales",
      "quantity": 2,
      "priceAtTime": 250.00,
      "subtotal": 500.00
    }
  ],
  "subtotal": 500.00,
  "shippingCost": 300.00,
  "totalPrice": 800.00,
  "status": "SENT_TO_WHATSAPP",
  "whatsappLink": "https://wa.me/5493416123456?text=...",
  "deliveryDate": "Viernes 2026-01-24",
  "deliveryWindowMin": 3,
  "deliveryWindowMax": 4,
  "deliveryTimeRange": "17:00-20:00"
}
```

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Orden encontrada |
| `404` | Orden no encontrada (contrato previsto — ver nota sobre `GlobalExceptionHandler` al final del documento) |
| `500` | Error interno del servidor (también es lo que se obtiene HOY ante un 404, ver nota final) |

---

### 3. Listar Órdenes por Teléfono de Cliente

- **Endpoint:** `GET /api/orders/customer/{phone}`
- **Parámetros:**
  - `phone` (Path) - Teléfono del cliente (`customerPhone` tal como fue guardado en la orden)
- **Autenticación:** No requerida (MVP)
- **Descripción:** Retorna todas las órdenes asociadas a ese número de teléfono (incluye el `whatsappLink` recalculado para cada una)

#### Response (200 OK)

```json
[
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
      {
        "productId": 1,
        "productName": "Base Vital de Vegetales",
        "quantity": 2,
        "priceAtTime": 250.00,
        "subtotal": 500.00
      }
    ],
    "subtotal": 500.00,
    "shippingCost": 300.00,
    "totalPrice": 800.00,
    "status": "SENT_TO_WHATSAPP",
    "whatsappLink": "https://wa.me/+5493416123456?text=...",
    "deliveryDate": "Viernes 2026-01-24",
    "deliveryWindowMin": 3,
    "deliveryWindowMax": 4,
    "deliveryTimeRange": "17:00-20:00"
  }
]
```

**Nota:** si no hay órdenes para ese teléfono, retorna una lista vacía `[]` (no un 404).

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Lista obtenida (puede ser vacía) |
| `500` | Error interno del servidor |

---

## 📦 Productos

### 1. Listar Productos Activos

- **Endpoint:** `GET /api/products`
- **Autenticación:** No requerida (MVP)
- **Descripción:** Retorna el catálogo de productos activos disponibles para compra

#### Response (200 OK)

```json
[
  {
    "id": 1,
    "name": "Base Vital de Vegetales",
    "description": "Mix de vegetales frescos seleccionados diariamente. Perfecto para salteados, sopas y guisos. Rinde 4 comidas. Usos: Salteados, Tartas, Guisos",
    "price": 250.00,
    "imageUrl": "https://cdn.example.com/base-vital.jpg",
    "servings": 4,
    "usages": "Salteados, Tartas, Guisos",
    "category": {
      "id": 1,
      "name": "Mixes"
    },
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "Lechuga Fresca",
    "description": "Lechuga recién cortada de nuestros proveedores locales. Ideal para ensaladas. Rinde 2 comidas. Usos: Ensaladas",
    "price": 80.00,
    "imageUrl": "https://cdn.example.com/lechuga.jpg",
    "servings": 2,
    "usages": "Ensaladas",
    "category": {
      "id": 2,
      "name": "Hoja Verde"
    },
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  }
]
```

#### Características

- Retorna solo productos con `isActive = true`
- Incluye categoría del producto
- Información completa incluyendo timestamps de auditoría

**Nota técnica:** el controller devuelve la entidad JPA `Product` directamente — no usa el DTO `ProductResponse` (existe en `product/dto/reponse/`, pero está vacío/sin implementar). En la práctica, el campo `category` de la respuesta real serializa el objeto `Category` **completo** (incluye `description`, `isActive`, `createdAt`, `updatedAt`), no solo `id`/`name` como se simplifica en el ejemplo de arriba.

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Lista de productos obtenida exitosamente |
| `500` | Error interno del servidor |

---

## 🚚 Zonas de Entrega

⚠️ **Estado real (seed `V2__seed.sql`):** solo existen 2 zonas cargadas — **Zona Norte** y **Zona Sur**. Este y Oeste son zonas **planificadas, todavía no implementadas** (sin fila en la base de datos ni en el seed); se dejan documentadas como referencia de trabajo futuro, no como zonas activas.

### 1. Listar Zonas de Entrega Activas

- **Endpoint:** `GET /api/delivery-zones`
- **Autenticación:** No requerida (MVP)
- **Descripción:** Retorna las zonas de entrega activas cargadas en base de datos

#### Response (200 OK) — refleja el seed real

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

**Planificadas, no implementadas (no aparecen en la respuesta real hoy):** Zona Este, Zona Oeste.

#### Valores de `deliveryDay`

`deliveryDay` es un `String` simple en la entidad (no un enum tipado), aunque el código lo trata como si tomara únicamente estos valores:

| Valor | Descripción | Horario |
|-------|-------------|---------|
| `FRIDAY_PM` | Viernes por la tarde | 17:00 - 20:00 |
| `SATURDAY_AM` | Sábado por la mañana | 09:00 - 13:00 |
| `SATURDAY_PM` | Sábado por la tarde | 15:00 - 19:00 |

Del seed real, solo se usan `FRIDAY_PM` (Zona Norte) y `SATURDAY_AM` (Zona Sur). `SATURDAY_PM` está soportado en el código (`OrderService.getDeliveryTimeRange`) pero ninguna zona cargada lo usa todavía.

#### Características

- Retorna solo zonas con `isActive = true`
- Incluye costo de envío por zona
- Información sobre días y barrios de cobertura

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Lista de zonas obtenida exitosamente |
| `500` | Error interno del servidor |

---

## 📂 Categorías

### 1. Listar Categorías Activas

- **Endpoint:** `GET /api/categories`
- **Autenticación:** No requerida (MVP)
- **Descripción:** Retorna todas las categorías de productos activas

#### Response (200 OK)

```json
[
  {
    "id": 1,
    "name": "Mixes",
    "description": "Combinaciones de vegetales frescos pre-seleccionados",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "Hoja Verde",
    "description": "Lechugas, espinacas y verdes frescos",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  }
]
```

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Lista de categorías obtenida exitosamente |
| `500` | Error interno del servidor |

---

### 2. Obtener Categoría por ID

- **Endpoint:** `GET /api/categories/{id}`
- **Parámetros:**
  - `id` (Path) - ID de la categoría
- **Autenticación:** No requerida (MVP)

#### Response (200 OK)

```json
{
  "id": 1,
  "name": "Mixes",
  "description": "Combinaciones de vegetales frescos pre-seleccionados",
  "isActive": true,
  "createdAt": "2026-01-15T10:30:00",
  "updatedAt": "2026-01-15T10:30:00"
}
```

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Categoría encontrada |
| `404` | Categoría no encontrada |
| `500` | Error interno del servidor |

---

### 3. Crear Categoría

**⚠️ Nota:** Por ahora sin autenticación. En producción solo administradores podrán crear categorías.

- **Endpoint:** `POST /api/categories`
- **Content-Type:** `application/json`
- **Autenticación:** No requerida (MVP)
- **HTTP Status:** `201 Created`

#### Request Body

```json
{
  "name": "Raíces",
  "description": "Zanahorias, remolachas, papas y otros tubérculos"
}
```

#### Validaciones

| Campo | Validación | Mensaje |
|-------|-----------|---------|
| `name` | Requerido, máx 100 caracteres, único | "El nombre de la categoría es requerido" |
| `description` | Máx 255 caracteres (opcional) | "La descripción no puede exceder 255 caracteres" |

#### Response (201 Created)

```json
{
  "id": 3,
  "name": "Raíces",
  "description": "Zanahorias, remolachas, papas y otros tubérculos",
  "isActive": true,
  "createdAt": "2026-01-18T16:53:40",
  "updatedAt": "2026-01-18T16:53:40"
}
```

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `201` | Categoría creada exitosamente |
| `400` | Nombre duplicado o validación fallida |
| `500` | Error interno del servidor |

---

### 4. Actualizar Categoría

**⚠️ Nota:** Por ahora sin autenticación. En producción solo administradores podrán actualizar.

- **Endpoint:** `PUT /api/categories/{id}`
- **Parámetros:**
  - `id` (Path) - ID de la categoría
- **Content-Type:** `application/json`
- **Autenticación:** No requerida (MVP)

#### Request Body

```json
{
  "name": "Raíces y Tubérculos",
  "description": "Zanahorias, remolachas, papas, batatas y otros tubérculos frescos"
}
```

#### Validaciones

| Campo | Validación |
|-------|-----------|
| `name` | Requerido, máx 100 caracteres, único |
| `description` | Máx 255 caracteres (opcional) |

#### Response (200 OK)

```json
{
  "id": 3,
  "name": "Raíces y Tubérculos",
  "description": "Zanahorias, remolachas, papas, batatas y otros tubérculos frescos",
  "isActive": true,
  "createdAt": "2026-01-18T16:53:40",
  "updatedAt": "2026-01-18T16:55:20"
}
```

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Categoría actualizada exitosamente |
| `400` | Nombre duplicado o validación fallida |
| `404` | Categoría no encontrada |
| `500` | Error interno del servidor |

---

### 5. Eliminar (Desactivar) Categoría

**⚠️ Nota:** Por ahora sin autenticación. En producción solo administradores podrán eliminar. La eliminación es un soft delete (desactivación).

- **Endpoint:** `DELETE /api/categories/{id}`
- **Parámetros:**
  - `id` (Path) - ID de la categoría
- **Autenticación:** No requerida (MVP)
- **HTTP Status:** `204 No Content`

#### Response

No retorna contenido (204 No Content)

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `204` | Categoría eliminada (desactivada) exitosamente |
| `404` | Categoría no encontrada |
| `500` | Error interno del servidor |

---

## 📊 Flujo de Integración Típico (React Native)

```
1. GET /api/products
   └─> Mostrar catálogo de productos

2. GET /api/delivery-zones
   └─> Mostrar opciones de entrega

3. POST /api/orders
   └─> Crear orden con productos y zona seleccionada
   └─> Obtener whatsappLink

4. Linking.openURL(whatsappLink)
   └─> Abrir WhatsApp con mensaje pre-llenado
   └─> Usuario confirma y envía manualmente
```

---

## ⚠️ Códigos de Error Comunes

⚠️ **Estado real de hoy:** `GlobalExceptionHandler` (`shared/exception/GlobalExceptionHandler.java`) es una clase **vacía** — sin `@ControllerAdvice` ni métodos `@ExceptionHandler`. Las tablas de "Estados de Respuesta" de cada endpoint describen el **contrato previsto/deseado**, pero hoy:
- Las fallas de `@Valid` (`MethodArgumentNotValidException`) sí las resuelve el manejo por defecto de Spring Boot, devolviendo un JSON de error razonable (aunque no con el formato exacto documentado abajo).
- `ResourceNotFoundException` y `BusinessException` **no tienen handler ni `@ResponseStatus`**, por lo que hoy se propagan como excepción no controlada y Spring Boot las traduce en un **500 Internal Server Error genérico**, no en el `404`/`400` documentado en cada sección. Implementar `GlobalExceptionHandler` es trabajo pendiente.

### 400 Bad Request
- Campos requeridos faltantes
- Datos inválidos (teléfono incorrecto, cantidad fuera de rango)
- JSON malformado

**Ejemplo:**
```json
{
  "timestamp": "2026-01-18T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Teléfono inválido: debe tener al menos 10 dígitos"
}
```

### 404 Not Found (contrato previsto — ver nota arriba)
- Orden no encontrada
- Producto no encontrado
- Zona de entrega no encontrada
- Categoría no encontrada

### 500 Internal Server Error
- Error al procesar la orden
- Error de conexión a base de datos
- Error al generar enlace de WhatsApp
- Hoy también: cualquier `ResourceNotFoundException`/`BusinessException` (ver nota arriba)

---

## 🔐 Consideraciones de Seguridad (MVP)

- Sin autenticación requerida en esta versión
- Validación de entrada en DTOs
- Sin limitación de rate limiting

**Para producción:**
- Implementar Spring Security + JWT
- Agregar validación CORS si es necesario
- Implementar rate limiting
- Validar números de teléfono contra formato Argentina

---

## 📝 Notas para el Cliente

- Las órdenes se crean con estado `SENT_TO_WHATSAPP`
- El stock de productos no se gestiona en esta versión (MVP)
- Los cambios de orden deben solicitarse manualmente vía WhatsApp
- Los precios son los vigentes al momento de la compra

