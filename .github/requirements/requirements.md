# Requerimientos - VerdeDeMas E-Commerce

## 1. Propuesta de Valor

**Frase Madre:**
> No vendemos comida. Vendemos alivio cotidiano.

**Mantra:**
> "Comer por elección, no por emoción."
> Pero ojo: no negar la emoción, sino acompañarla con conciencia.

**Traducción simple para la web:**
> No comemos perfecto. Comemos con intención.
> Elegimos alimentos que sostienen, no que anestesian.

---

## 2. MVP - Funcionalidades Fase 1 (80% Backend)

### 2.1 Gestión de Productos
- [x] Crear productos con: nombre, descripción, precio, categoría, imagen URL
- [x] Listar productos con filtros (categoría, precio, búsqueda)
- [x] Obtener detalle de producto
- [x] Búsqueda de productos por texto

**Modelo de Producto:**
```
Nombre Emocional + Claro
  ├─ "Base Vital de Vegetales" (no "Mix vegetal 500g")
  
Descripción (2-3 líneas)
  ├─ Vegetales apenas salteados, listos para combinar.
  ├─ Para esos días donde querés comer bien sin pensar demasiado.
  
Rendimiento
  ├─ Rinde X comidas
  ├─ Ideal para X personas
  
Uso
  ├─ Salteados, Tartas, Acompañamientos
  
Precio
  ├─ Transparente, sin justificarse
```

### 2.2 Gestión de Categorías
- [x] Crear categorías: "Bases de vegetales", "Bases de cereales", "Masas listas", "Complementos"
- [x] Listar categorías activas
- [x] Obtener productos por categoría

### 2.3 Gestión de Zonas de Entrega
- [x] Crear zonas con: nombre, descripción, costo de envío, día de entrega
- [x] Listar zonas de entrega activas
- [x] Obtener detalle de zona
- [x] Aceptar pedidos cualquier día y agendar próxima entrega
- [x] Calcular fecha/franja y ventana (díasMin/díasMax) según zona

**Ubicación Geográfica:**
```
Rosario, Santa Fe, Argentina

Zonas:
├─ Norte → $300, Viernes PM
├─ Sur  → $300, Sábado AM
├─ Este → $400, Sábado AM
└─ Oeste → $400, Sábado PM
```

**Ciclo Semanal:**
```
Pedidos:     Abierto todos los días (Dom-Mié ingresan al ciclo actual; Jue-Sáb al próximo)
Elaboración: Jueves - Viernes AM
Entregas:    Viernes PM - Sábado
```

### 2.4 Sistema de Pedidos por WhatsApp
- [x] Formulario de pedido con: nombre cliente, teléfono, dirección, zona de entrega, productos + cantidad
- [x] Validación de datos del formulario
- [x] Validación de zona de entrega
- [x] Generación automática de mensaje WhatsApp formateado
- [x] Cálculo automático de: subtotal, costo de envío, total
- [x] Guardar pedido en BD para historial
- [x] Link directo a WhatsApp (https://wa.me/...)

**Formato de Mensaje WhatsApp:**
```
🌱 *NUEVO PEDIDO VERDEDEMAS*

👤 *Cliente:* [Nombre]
📍 *Dirección:* [Dirección]
🚚 *Zona:* [Zona]
📞 *Teléfono:* [Teléfono]
─────────────────────
*PRODUCTOS:*
• Base Vital de Vegetales x2
• Base de Legumbres x1
─────────────────────
💰 *Subtotal:* $450
🚚 *Envío (Zona Norte):* $300
*TOTAL:* $750

📦 *Entrega estimada:* Viernes 2026-01-24
🕐 *Horario:* 17:00-20:00

¿Confirmás disponibilidad y entrega?
```

---

## 3. Validaciones

### 3.1 Validaciones de Entrada
- [x] Nombre cliente: No vacío, máx 100 caracteres
- [x] Teléfono: Formato válido (+54 9 XXXX-XXXXXX), mínimo 10 dígitos
- [x] Dirección: No vacía, máx 200 caracteres
- [x] Zona de entrega: Debe existir en BD y estar activa
- [x] Productos: Mínimo 1 producto, cantidad mínimo 1
- [x] Periodo de pedidos: Se acepta cualquier día; informar ventana (hasta Viernes/Sábado)


### 3.2 Validaciones de Negocio
- [x] Precio de producto: Mayor a 0
- [x] Costo de envío: Mayor a 0
- [x] Orden: Total = Subtotal + Costo envío

---

## 4. Datos a Persistir

### 4.1 Base de Datos (PostgreSQL)

**Tablas:**
```sql
categories
  ├─ id (PK)
  ├─ name (UNIQUE)
  ├─ description
  └─ timestamps

products
  ├─ id (PK)
  ├─ name
  ├─ description
  ├─ price
  ├─ category_id (FK)
  └─ timestamps

delivery_zones
  ├─ id (PK)
  ├─ name (UNIQUE)
  ├─ description
  ├─ shipping_cost
  ├─ delivery_days_min
  ├─ delivery_days_max
  ├─ is_active
  └─ timestamps

orders
  ├─ id (PK)
  ├─ customer_name
  ├─ customer_phone
  ├─ customer_address
  ├─ delivery_zone_id (FK)
  ├─ subtotal
  ├─ shipping_cost
  ├─ total_price
  ├─ status (ENUM)
  ├─ sent_to_whatsapp_at
  └─ timestamps

order_items
  ├─ id (PK)
  ├─ order_id (FK)
  ├─ product_id (FK)
  ├─ quantity
  ├─ price_at_time
  └─ timestamps
```

---

## 5. Comportamientos No Funcionales

- [x] API REST
- [x] Respuestas JSON
- [x] CORS habilitado para desarrollo
- [x] Manejo centralizado de excepciones
- [x] Validación de entrada con @Valid
- [x] Logs básicos
- [x] Sin autenticación en MVP
- [x] Sin carrito persistente (se calcula en tiempo real)
- [x] Sin pago online en MVP

---

## 6. Matriz de Responsabilidad (RACI)

| Componente | Backend | Frontend | Descripción |
|-----------|---------|----------|------------|
| Listar productos | ✅ Responsable | - | GET /api/products |
| Listar categorías | ✅ Responsable | - | GET /api/categories |
| Listar zonas | ✅ Responsable | - | GET /api/delivery-zones |
| Formulario pedido | - | ✅ Responsable | React Native form |
| Validación servidor | ✅ Responsable | - | Spring Boot @Valid |
| Cálculo total | ✅ Responsable | ✅ Consulta | Backend calcula, frontend visualiza |
| Generación WhatsApp | ✅ Responsable | ✅ Redirige | Backend genera URL, frontend abre link |
| Almacenar pedido | ✅ Responsable | - | BD PostgreSQL |

---

## 7. Estados de Orden

```
PENDING              → Acaba de crearse, aún no enviado a WhatsApp
                      
SENT_TO_WHATSAPP     → Link a WhatsApp generado, usuario redirigido
                      
CONFIRMED            → Vendedor confirmó por WhatsApp (manual)
                      
PREPARING            → En preparación (manual por vendedor)
                      
DISPATCHED           → En camino (manual por vendedor)
                      
DELIVERED            → Entregado (manual por vendedor)
                      
CANCELLED            → Cancelado por cliente o vendedor
```

---

## 8. Fases de Desarrollo

### **Fase 1: MVP (Actual)**
- [x] Backend 80% funcional
- [x] Productos, categorías, zonas
- [x] Pedidos por WhatsApp
- [ ] Frontend simple (HTML estático o Next.js)

### **Fase 2: Expansión (Futuro)**
- [ ] Autenticación JWT
- [ ] Carrito persistente
- [ ] Login / Registro de usuarios
- [ ] Historial de pedidos del usuario
- [ ] WebSocket para tracking en tiempo real
- [ ] Reseñas y ratings
- [ ] Sistema de direcciones guardadas

### **Fase 3: Monetización (Futuro)**
- [ ] Pago online (Mercado Pago, Stripe)
- [ ] Notificaciones por email
- [ ] Admin panel
- [ ] Analytics y reportes

---

## 9. Qué NO Incluir en MVP

- ❌ Login / Autenticación
- ❌ Carrito persistente
- ❌ Wishlist
- ❌ Reseñas
- ❌ Pago online
- ❌ Stock management
- ❌ Admin dashboard
- ❌ WebSocket
- ❌ Notificaciones por email/SMS
- ❌ Multi-idioma
- ❌ Analytics

---

## 10. Mensajes de Error Esperados

```json
{
  "timestamp": "2026-01-17T10:30:00Z",
  "status": 400,
  "message": "Validación fallida",
  "errors": [
    {
      "field": "customerName",
      "message": "El nombre es requerido"
    }
  ]
}
```

```json
{
  "timestamp": "2026-01-17T10:30:00Z",
  "status": 404,
  "message": "Zona de entrega no encontrada"
}
```

---

## 11. Criterios de Aceptación

- [x] Todos los endpoints funcionan sin autenticación
- [x] BD se genera automáticamente con Flyway
- [x] Validaciones retornan errores claros en JSON
- [x] Mensaje WhatsApp es legible y completo
- [x] Link WhatsApp abre en app/web sin problemas
- [x] Pedido se guarda correctamente en BD
- [x] CORS permite requests desde React Native
