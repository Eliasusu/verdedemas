# Reglas de Negocio - VerdeDeMas E-Commerce

## 1. Filosofía de la Marca

### 1.1 Frase Madre
> "No vendemos comida. Vendemos alivio cotidiano."

### 1.2 Mantra Principal
> "Comer por elección, no por emoción."
> 
> Pero ojo: no negar la emoción, sino acompañarla con conciencia.

### 1.3 Lo que Transmitimos
```
✨ Calma
✨ Elección (no imposición)
✨ Sostén (no restricción)
✨ Accesibilidad (no lujo)
✨ Cuidado sin culpa
```

### 1.4 Lo que NO Hacemos
```
❌ No empujamos "comprá ya o perdés"
❌ No dietas restrictivas
❌ No culpa alimenticia
❌ No marketing sensacionalista
❌ No promesas milagrosas
```

---

## 2. Reglas de Productos

### 2.1 Nomenclatura
```
CORRECTO:
✅ Base Vital de Vegetales
✅ Mix de Legumbres Andinas
✅ Preparación de Quinoa y Verduras
✅ Complemento Proteico Natural

INCORRECTO:
❌ Mix vegetal 500g
❌ Ensalada pre-hecha
❌ Producto bajo en calorías
❌ Mix fitness
```

### 2.2 Descripción Obligatoria
Cada producto debe tener:
1. **Qué es** (2-3 líneas simples)
   - Ej: "Vegetales apenas salteados, listos para combinar."

2. **Para qué sirve** (problema que resuelve)
   - Ej: "Para esos días donde querés comer bien sin pensar demasiado."

3. **Rendimiento**
   - Rinde X comidas
   - Ideal para X personas

4. **Usos posibles**
   - Salteados, Tartas, Acompañamientos

### 2.3 Precio
- Transparente y sin justificarse
- No incluir descuentos falsos
- Mostrar valor (cantidad, calidad) no precio "tirado"

### 2.4 Stock
- Mínimo nivel de stock: Definido por cada zona (futuro)
- Sin sobreventa

---

## 3. Reglas de Categorías

### 3.1 Categorías Permitidas

```
🥕 Bases de Vegetales
   - Crudités
   - Salteados
   - Preparaciones hervidas

🍚 Bases de Cereales y Legumbres
   - Arroces y granos
   - Legumbres cocidas
   - Mezclas proteicas

🥧 Masas y Preparaciones Listas
   - Masas para tartas
   - Rellenos preparados
   - Alimentos pre-cocidos

🥣 Complementos
   - Salsas caseras
   - Añadidos proteicos
   - Condimentos especiales
```

### 3.2 Validación
- Toda categoría debe tener mínimo 2 productos
- Toda categoría tiene descripción clara
- Solo admins pueden crear nuevas categorías (futuro)

---

## 4. Reglas de Zonas de Entrega

### 4.1 Ubicación Geográfica
```
📍 CIUDAD: Rosario, Santa Fe, Argentina

ZONAS DISPONIBLES:
├─ Zona Norte (Fisherton, Alberdi, Rucci, Tío Rojo)
├─ Zona Sur (Echesortu, Azcuénaga, Lisandro de la Torre)
├─ Zona Este (Funes, Roldán, Pérez)
└─ Zona Oeste (Villa Gobernador Gálvez, Pérez Millán)
```


### 4.2 Ciclo Semanal y Ventana Min/Max
```
Pedidos: Domingo a Miércoles (hasta 23:59)
Elaboración: Jueves y Viernes AM
Entregas: 
  - Viernes PM (17:00-20:00) → Zonas asignadas a FRIDAY_PM
  - Sábado AM (09:00-13:00)  → Zonas asignadas a SATURDAY_AM
  - Sábado PM (15:00-19:00)  → Zonas asignadas a SATURDAY_PM
```

### 4.3 Ventana dinámica de entrega (min-max)
```
Se calcula dinámicamente desde el día del pedido:
- díasMin = días hasta el próximo Viernes
- díasMax = días hasta el próximo Sábado

Ejemplo:
  Pedido un Lunes → díasMin = 4, díasMax = 5
  Pedido un Miércoles → díasMin = 2, díasMax = 3
```

### 4.4 Validación de Zona
```
✅ OBLIGATORIO:
├─ Zona debe existir en BD
├─ Zona debe estar activa (isActive = true)
├─ Zona debe tener costo de envío > $0
├─ Zona debe tener día de entrega asignado
└─ Pedido debe hacerse en periodo permitido (Dom-Mié)

❌ RESTRICCIONES:
├─ No se aceptan pedidos Jueves, Viernes, Sábado
├─ No se puede cambiar zona después de crear orden
├─ No se puede eliminar zona con órdenes asociadas
└─ Costo de envío no se aplica retroactivamente
```


### 4.5 Ejemplo de Zonas (Rosario)

```
ZONA NORTE
├─ Descripción: Fisherton, Alberdi, Rucci, Tío Rojo
├─ Costo: $300
├─ Entrega: Viernes 17:00-20:00
└─ Activa: ✅

ZONA SUR  
├─ Descripción: Echesortu, Azcuénaga, Lisandro de la Torre
├─ Costo: $300
├─ Entrega: Sábado 09:00-13:00
└─ Activa: ✅

ZONA ESTE
├─ Descripción: Funes, Roldán, Pérez
├─ Costo: $400
├─ Entrega: Sábado 09:00-13:00
└─ Activa: ✅

ZONA OESTE
├─ Descripción: Villa Gobernador Gálvez, Pérez Millán
├─ Costo: $400
├─ Entrega: Sábado 15:00-19:00
└─ Activa: ✅
```
### 4.6 Validación de Periodo de Pedidos

```
Nueva regla:
✅ Se aceptan pedidos cualquier día de la semana.
✅ Si el pedido se realiza Jueves/Viernes/Sábado, se agenda para el próximo ciclo de entrega.

Implementación:
- Calcular díasMin = días hasta el próximo Viernes.
- Calcular díasMax = días hasta el próximo Sábado.
- Informar fecha y franja horaria según la zona (Viernes PM, Sábado AM/PM).

Ejemplos:
  Pedido un Viernes → Entrega: próximo Viernes (díasMin=7) o Sábado (díasMax=8).
  Pedido un Lunes → Entrega: Viernes de esa semana (díasMin≈4) o Sábado (díasMax≈5).
```

---

## 5. Reglas de Órdenes

### 5.1 Creación de Orden
```
Datos OBLIGATORIOS:
├─ customerName (no vacío, máx 100 caracteres)
├─ customerPhone (formato válido, mínimo 10 dígitos)
├─ customerAddress (no vacía, máx 200 caracteres)
├─ deliveryZoneId (debe existir y estar activa)
└─ items[] (mínimo 1 producto, cantidad ≥ 1)
```

### 5.2 Cálculo de Valores
```
Cálculos OBLIGATORIOS:
├─ Subtotal = SUM(precio_producto × cantidad)
├─ Costo_envío = delivery_zone.shipping_cost (fijo)
└─ Total = Subtotal + Costo_envío
```

### 5.3 Validaciones en Orden
```
✅ customerName NO vacío
✅ customerPhone con formato válido (+54 9 XXXX-XXXXXX)
✅ customerAddress NO vacío
✅ deliveryZoneId existe en BD
✅ deliveryZoneId.isActive = true
✅ items.size() ≥ 1
✅ items[].quantity ≥ 1
✅ items[].productId existe y es activo
✅ price_producto > 0
✅ shipping_cost > 0
```

### 5.4 Estados de Orden

```
PENDING
  └─ Acaba de crearse
     No ha sido enviado a WhatsApp aún
     Puede ser eliminada (futuro)

SENT_TO_WHATSAPP
  └─ Link a WhatsApp fue generado
     Usuario fue redirigido
     Esperando confirmación en chat

CONFIRMED
  └─ Vendedor confirmó en WhatsApp
     Cliente confirmó presencia
     Se procede a preparación
     (Guardado manual por vendedor)

PREPARING
  └─ Está siendo preparada
     (Actualizado por vendedor)

DISPATCHED
  └─ Salió en reparto
     (Actualizado por vendedor)

DELIVERED
  └─ Fue entregada
     (Actualizado por vendedor)

CANCELLED
  └─ Cancelada por cliente o vendedor
     (Permanece en historial)
```

### 5.5 Transiciones de Estado Válidas
```
PENDING → SENT_TO_WHATSAPP (automático al crear)
        → CANCELLED (manual, por vendedor)

SENT_TO_WHATSAPP → CONFIRMED (manual, por vendedor)
                 → CANCELLED (manual, por vendedor)

CONFIRMED → PREPARING (manual, por vendedor)
          → CANCELLED (manual, por vendedor)

PREPARING → DISPATCHED (manual, por vendedor)
          → CANCELLED (manual, por vendedor)

DISPATCHED → DELIVERED (manual, por vendedor)

DELIVERED → (final, no hay transiciones)
CANCELLED → (final, no hay transiciones)
```

### 5.6 Timestamp Obligatorio
```
sent_to_whatsapp_at
  └─ Se registra la hora exacta en que se generó
     el link a WhatsApp
     
Sirve para:
  ├─ Auditoría
  ├─ Análisis de tiempo de respuesta
  └─ Alertas si no se confirma en X horas
```

---

## 6. Reglas de WhatsApp

### 6.1 Generación de Mensaje
```
Formato OBLIGATORIO:
├─ Emoji para cada sección
├─ *Texto en bold* para títulos
├─ Saltos de línea claros
├─ Información organizada
├─ NO caracteres especiales problemáticos
└─ Máximo 4096 caracteres (límite WhatsApp)
```

### 6.2 Contenido del Mensaje
```
Debe incluir:
✅ Nombre cliente
✅ Dirección
✅ Zona de entrega
✅ Teléfono
✅ Lista de productos (nombre × cantidad)
✅ Subtotal con símbolo $
✅ Costo de envío desglosado
✅ TOTAL en bold
✅ Rango de días de entrega
✅ Pregunta de confirmación
```

### 6.3 Link de WhatsApp
```
Formato:
https://wa.me/{numero_telefono}?text={mensaje_encoded}

Número teléfono:
├─ Debe venir del orden (customerPhone)
├─ Sin espacios, solo dígitos + signo +
└─ Formato: +549XXXXXXXXXX (Argentina)

Mensaje:
├─ URL encoded (UTF-8)
├─ Sin caracteres especiales problemáticos
└─ Mantiene saltos de línea con %0A
```

### 6.4 Flujo del Usuario
```
1. Usuario completa formulario en app
2. Valida los datos
3. Envía POST /api/orders
4. Backend valida y genera mensaje
5. Backend retorna whatsappLink
6. Frontend: Linking.openURL(whatsappLink)
7. Abre WhatsApp
8. Mensaje prearmado aparece en chat
9. Usuario revisa y envía
10. Vendedor recibe en WhatsApp
11. Vendedor responde por chat
12. Vendedor actualiza estado en sistema (futuro)
```

---

## 7. Reglas de Datos

### 7.1 Integridad
```
Restricciones SQL:
├─ PK: Todas las tablas tienen id auto-increment
├─ FK: order.delivery_zone_id → delivery_zones(id)
├─ FK: order_items.order_id → orders(id)
├─ FK: order_items.product_id → products(id)
├─ FK: products.category_id → categories(id)
├─ UNIQUE: categories.name
├─ UNIQUE: delivery_zones.name
├─ NOT NULL: customer_name, customer_phone, customer_address
├─ CHECK: price > 0
├─ CHECK: shipping_cost > 0
└─ CHECK: total_price = subtotal + shipping_cost
```

### 7.2 Auditoría
```
Todos registro tienen:
├─ created_at (TIMESTAMP, auto-insert)
├─ updated_at (TIMESTAMP, auto-update)
└─ Creados por BaseEntity (anotación @EntityListeners)
```

### 7.3 Retención de Datos
```
Datos a guardar SIEMPRE:
├─ Órdenes (historial completo)
├─ Productos (incluso si se dan de baja)
├─ Precios históricos (price_at_time en OrderItem)
└─ Estados de orden (auditoría)

Datos a NO guardar (MVP):
├─ IP del usuario
├─ Geolocalización
└─ Datos de pago
```

---

## 8. Reglas de Búsqueda y Filtrado

### 8.1 Búsqueda de Productos
```
GET /api/products/search?q=vegetales
  └─ Busca en:
     ├─ Nombre (ILIKE)
     ├─ Descripción (ILIKE)
     └─ Nombre de categoría
     
Retorna:
  └─ Lista de ProductResponse
     (máximo 50 resultados)
```

### 8.2 Filtro por Categoría
```
GET /api/products/category/{categoryId}
  └─ Retorna todos los productos activos
     de esa categoría
```

### 8.3 Filtro por Zona
```
GET /api/delivery-zones?active=true
  └─ Retorna solo zonas activas
     Ordenadas por nombre
```

---

## 9. Restricciones de Acceso (MVP)

### 9.1 Sin Autenticación
```
Cualquiera puede:
✅ VER productos
✅ VER categorías
✅ VER zonas de entrega
✅ CREAR una orden

NO puede (futuro con auth):
❌ VER órdenes de otros usuarios
❌ MODIFICAR órdenes
❌ ELIMINAR órdenes
```

### 9.2 Acceso Público
```
Todos estos endpoints SIN token:
├─ GET /api/products
├─ GET /api/products/{id}
├─ GET /api/products/category/{catId}
├─ GET /api/categories
├─ GET /api/delivery-zones
└─ POST /api/orders
```

---

## 10. Límites y Restricciones

### 10.1 Tamaño de Datos
```
Nombre cliente:      máximo 100 caracteres
Dirección:           máximo 200 caracteres
Teléfono:            máximo 20 caracteres
Nombre producto:     máximo 150 caracteres
Descripción:         máximo 1000 caracteres
Items por orden:     máximo 100 (razonable)
Cantidad por item:   máximo 999 unidades
```

### 10.2 Rate Limiting (Futuro)
```
Por ahora: Sin límite
Futuro: Máximo 10 órdenes/minuto por IP
```

### 10.3 Caché (Futuro)
```
Cachear (posible):
├─ Productos (5 minutos)
├─ Categorías (10 minutos)
├─ Zonas de entrega (15 minutos)

NO cachear:
├─ Órdenes (datos en tiempo real)
├─ Stock (cambios frecuentes)
```

---

## 11. SLA y Performance

### 11.1 Tiempos de Respuesta Objetivo
```
GET /api/products              < 200ms
GET /api/products/{id}         < 100ms
GET /api/categories            < 100ms
GET /api/delivery-zones        < 100ms
POST /api/orders               < 500ms
```

### 11.2 Disponibilidad
```
Meta: 99.5% uptime
Downtime permitido: ~3.6 horas/mes
```

---

## 12. Cumplimiento Legal (Argentina)

### 12.1 Privacidad
```
✅ Datos de cliente almacenados en BD
✅ Dato sensible: Teléfono y dirección
✅ Futura: Política de Privacidad
✅ Futura: Términos y Condiciones
```

### 12.2 AFIP (Impuestos)
```
⚠️ TODO:
├─ Facturación electrónica (futuro)
├─ IVA cálculo (futuro)
├─ Monotributo vs Responsable Inscripto
└─ Registro ante AFIP
```

### 12.3 Protección al Consumidor
```
✅ Precio transparente
✅ Descripción clara
✅ Responsable de negocio identificado
⚠️ TODO: Política de devoluciones
```

---

## 13. Casos de Uso Especiales

### 13.1 ¿Qué pasa si el usuario crea 2 órdenes del mismo producto?
```
Permitido: ✅
Resultado: Dos órdenes separadas
Ejemplo:
  Orden 1: Base Vegetales x2 → ID 42
  Orden 2: Base Vegetales x3 → ID 43
```

### 13.2 ¿Qué pasa si se modifica el precio de un producto?
```
NO afecta órdenes previas: ✅
Motivo: price_at_time guarda precio histórico
```

### 13.3 ¿Qué pasa si se deshabilita una zona?
```
No puede crear órdenes nuevas: ✅
Órdenes previas permanecen: ✅
```

### 13.4 ¿Qué pasa si el usuario entra mal el teléfono?
```
Validación rechaza: ✅
Mensaje de error claro: ✅
Ejemplo error:
  "Teléfono inválido: debe tener al menos 10 dígitos"
```

---

## 14. Matriz de Decisiones

| Escenario | Decisión | Razón |
|-----------|----------|-------|
| Usuario intenta crear orden sin zona | ❌ Rechazar | Zona es obligatoria para calcular envío |
| Usuario intenta crear orden sin productos | ❌ Rechazar | Orden sin items no tiene sentido |
| Cambiar precio de producto | ✅ Permitir (futuro) | Órdenes guardadas usan price_at_time |
| Eliminar producto | ❌ No permitir | Rompe integridad referencial. Solo desactivar |
| Crear segunda orden mismo cliente | ✅ Permitir | Cliente puede hacer múltiples pedidos |
| Modificar orden después de crear | ❌ No permitir (MVP) | Futuro: requiere autenticación |
| Enviar a WhatsApp sin guardar | ❌ No permitir | Necesitamos historial en BD |
