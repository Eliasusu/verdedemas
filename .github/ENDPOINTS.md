# Documentación de Endpoints - VerdeDeMas API

**Base URL:** `http://localhost:8080/api`

---

## 📋 Índice de Endpoints

1. [Órdenes](#órdenes)
2. [Productos](#productos)
3. [Zonas de Entrega](#zonas-de-entrega)
4. [Categorías](#categorías)

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
    "name": "Norte",
    "shippingCost": 150.00,
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
  "shippingCost": 150.00,
  "totalPrice": 730.00,
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
    "name": "Norte",
    "shippingCost": 150.00,
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
  "shippingCost": 150.00,
  "totalPrice": 650.00,
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
| `404` | Orden no encontrada |
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

#### Estados de Respuesta

| Código | Descripción |
|--------|-------------|
| `200` | Lista de productos obtenida exitosamente |
| `500` | Error interno del servidor |

---

## 🚚 Zonas de Entrega

### 1. Listar Zonas de Entrega Activas

- **Endpoint:** `GET /api/delivery-zones`
- **Autenticación:** No requerida (MVP)
- **Descripción:** Retorna las zonas de entrega disponibles en Rosario

#### Response (200 OK)

```json
[
  {
    "id": 1,
    "name": "Norte",
    "description": "Barrios: Saladillo, Alberdi, General López",
    "shippingCost": 150.00,
    "deliveryDay": "FRIDAY_PM",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "Sur",
    "description": "Barrios: Acoyte, Urquiza, Ludueña",
    "shippingCost": 150.00,
    "deliveryDay": "SATURDAY_AM",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  },
  {
    "id": 3,
    "name": "Este",
    "description": "Barrios: Candioti, Fisherton, Pellegrini",
    "shippingCost": 200.00,
    "deliveryDay": "SATURDAY_PM",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  },
  {
    "id": 4,
    "name": "Oeste",
    "description": "Barrios: Refinería, Mosconi, Rocamora",
    "shippingCost": 180.00,
    "deliveryDay": "FRIDAY_PM",
    "isActive": true,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  }
]
```

#### Valores de `deliveryDay`

| Valor | Descripción | Horario |
|-------|-------------|---------|
| `FRIDAY_PM` | Viernes por la tarde | 17:00 - 20:00 |
| `SATURDAY_AM` | Sábado por la mañana | 09:00 - 13:00 |
| `SATURDAY_PM` | Sábado por la tarde | 15:00 - 19:00 |

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

### 404 Not Found
- Orden no encontrada
- Producto no encontrado
- Zona de entrega no encontrada

### 500 Internal Server Error
- Error al procesar la orden
- Error de conexión a base de datos
- Error al generar enlace de WhatsApp

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

