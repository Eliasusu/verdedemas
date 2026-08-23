# Roadmap de Aprendizaje — VerdeDeMas

Este documento detalla el plan de evolución del proyecto. Cada fase tiene un objetivo claro y un conjunto de conceptos a aprender.

El detalle de cada fase en particular con notas, conceptos, decisiones tomadas estan en **[docs/phases](../docs/phases)**

**Estado actual:** ⚪️ Pendiente | 🔵 En curso | ✅️ Completado

---

## Fase 0 — Auditoría del Proyecto
**Estado:** ✅️ Completado (2026-08-23)

**Objetivo:** Analizar el código actual sin modificarlo, entender qué hace, detectar problemas y deuda técnica.

**Actividades:**
1. Revisar README, pom.xml, estructura de paquetes.
2. Reconstruir el modelo de negocio implícito.
3. Identificar casos de uso, acoplamientos, lógica mal ubicada.
4. Entregar diagnóstico con puntos específicos.

**Conceptos involucrados:** Reingeniería inversa, análisis de código, deuda técnica.

**Detalle:** ➡️ **[docs/phases/fase-0-auditoria-del-proyecto.md](phases/fase-0-auditoria-del-proyecto.md)**

---

## Fase 1 — Entender el Dominio
**Estado:** ⚪️ Pendiente

**Objetivo:** Descubrir el dominio de VerdeDeMas conceptualmente (sin Java).

**Actividades:**
- Identificar actores, entidades, value objects, reglas de negocio.
- Producir modelo de dominio, lenguaje ubicuo, posibles bounded contexts.

**Conceptos involucrados:** Domain-Driven Design (intro), Modelado conceptual, Lenguaje Ubicuo.

---

## Fase 2 — Domain-Driven Design (DDD)
**Estado:** ⚪️ Pendiente

**Objetivo:** Aplicar DDD progresivamente en el código.

**Actividades:**
- Implementar Entities, Value Objects, Aggregates, Repositories.
- Definir Services de dominio y aplicación.
- Utilizar Domain Events.

**Conceptos involucrados:** Entity, Value Object, Aggregate Root, Repository, Domain Service, Application Service, Invariants.

---

## Fase 3 — TDD (Test-Driven Development)
**Estado:** ⚪️ Pendiente

**Objetivo:** Aprender a escribir tests primero (Red-Green-Refactor).

**Actividades:**
- Escribir tests de unidad para el dominio.
- Usar mocks, stubs y fakes para aislar.
- Enfocarse en comportamiento vs implementación.

**Conceptos involucrados:** Unit Testing, Test Doubles, Aislamiento, Testabilidad.

---

## Fase 4 — BDD (Behavior-Driven Development)
**Estado:** ⚪️ Pendiente

**Objetivo:** Convertir casos de uso en escenarios (Given-When-Then).

**Conceptos involucrados:** BDD, Acceptance Criteria, Gherkin, diferencias con TDD.

---

## Fase 5 — Arquitectura (Hexagonal / Clean)
**Estado:** ⚪️ Pendiente

**Objetivo:** Elegir y aplicar una arquitectura limpia y mantenible.

**Actividades:**
- Comparar Layered, Clean, Hexagonal, Onion.
- Definir capas: Domain, Application, Infrastructure, Adapters.
- Establecer reglas de dependencia.

**Conceptos involucrados:** Hexagonal Architecture, Ports & Adapters, Inversión de dependencias.

---

## Fase 6 — API Design
**Estado:** ⚪️ Pendiente

**Objetivo:** Diseñar APIs REST robustas y consistentes.

**Actividades:**
- Revisar APIs actuales.
- Modelar recursos, DTOs, manejo de errores, validación.
- Versionado y compatibilidad hacia atrás. OpenAPI.

**Conceptos involucrados:** REST, HTTP Semantics, DTOs, API Contracts, OpenAPI.

---

## Fase 7 — Event-Driven Architecture (Conceptual)
**Estado:** ⚪️ Pendiente

**Objetivo:** Entender eventos de dominio y comunicación asíncrona.

**Conceptos involucrados:** Domain Events, Integration Events, Eventual Consistency, Idempotencia.

---

## Fase 8 — Mensajería / Kafka
**Estado:** ⚪️ Pendiente

**Objetivo:** Implementar Kafka solo si existe un caso de uso real.

**Conceptos involucrados:** Producer, Consumer, Topics, Partitions, Offsets, DLQ, Semánticas de entrega.

---

## Fase 9 — CQRS
**Estado:** ⚪️ Pendiente

**Objetivo:** Evaluar si CQRS aporta valor a VerdeDeMas. Si no, justificar el "NO".

**Conceptos involucrados:** Command Query Responsibility Segregation, Write/Read Models.

---

## Fase 10 — Docker / CI/CD
**Estado:** ⚪️ Pendiente

**Objetivo:** Crear entorno reproducible y pipeline automatizado.

**Actividades:**
- Dockerizar app, DB y Kafka (si aplica).
- Configurar CI (compilar, testear, empaquetar, imagen Docker).

**Conceptos involucrados:** Docker, CI/CD, Pipelines.

---

## Fase 11 — Documentación (ADR)
**Estado:** ⚪️ Pendiente

**Objetivo:** Documentar decisiones importantes usando Architecture Decision Records.

**Conceptos involucrados:** ADRs, Documentación técnica, Trade-offs.

---

## Fase 12 — Code Review
**Estado:** ⚪️ Pendiente

**Objetivo:** Revisar el código continuamente aplicando SOLID, Clean Code, etc.

**Conceptos involucrados:** SOLID, Cohesión, Acoplamiento, Mantenibilidad.

---

## Fase 13 — Entrevista Técnica
**Estado:** ⚪️ Pendiente

**Objetivo:** Preparar preguntas y respuestas sobre las decisiones tomadas.

**Conceptos involucrados:** Defensa de decisiones arquitectónicas, Trade-offs.

---

## Fase 14 — Portfolio
**Estado:** ⚪️ Pendiente

**Objetivo:** Dejar el proyecto listo para mostrar en entrevistas.

**Actividades:**
- README profesional con diagramas, stack, decisiones.
- Scripts de ejecución claros.

---

## Nota
Este roadmap es flexible. Podemos saltar, combinar o repetir fases según sea necesario. El objetivo es aprender, no cumplir un checklist rígido.