# Copilot Instructions Template

Use this template to create `.github/copilot-instructions.md` in any project. It ensures AI agents respond concisely like a senior mentor.

---

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

[Descripción breve: qué es, stack, MVP scope]

**Tech Stack:** [lenguaje | framework | BD | herramientas clave]

---

## Architecture & Code Patterns

### Core Layers/Components
[Estructura principal del proyecto, relaciones entre módulos, patrones recurrentes]

### Key Design Decisions
[Por qué se eligió así, qué alternativas se descartaron]

---

## Critical Business Rules

[Reglas que MUST ser respetadas, validaciones, flujos core del negocio]

---

## Build & Development Commands

```bash
# Build command
# Run command
# Test command
```

---

## Key Integration Points

[APIs externas, bases de datos, servicios críticos]

---

## Project-Specific Conventions

- **Pattern 1:** [descripción + ejemplo]
- **Pattern 2:** [descripción + ejemplo]
- **Pattern 3:** [descripción + ejemplo]

---

## Important Files to Know

- **File 1:** [ruta](ruta) — Qué hace y por qué es crítico
- **File 2:** [ruta](ruta) — Qué hace y por qué es crítico
- **File 3:** [ruta](ruta) — Qué hace y por qué es crítico

---

## Future Features (Scaffolded, Not Active)

[Qué está preparado pero sin usar. Importante para no implementar sin pedir]

---

## Common Pitfalls to Avoid

- ❌ Pitfall 1: [descripción + cómo evitarlo]
- ❌ Pitfall 2: [descripción + cómo evitarlo]

---

## When in Doubt

- Check: [archivo/patrón específico a consultar]
- Ask: [tipo de pregunta que justifica exploración adicional]
- Reference: [documento de negocio o arquitectura principal]
