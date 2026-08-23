# CLAUDE.md – VerdeDeMas

Este archivo contiene las instrucciones específicas para Claude Code cuando trabaje en este repositorio.

## Propósito del proyecto

VerdeDeMas es un proyecto de aprendizaje y portfolio. El objetivo es demostrar habilidades de Backend Engineering mediante la evolución progresiva de un sistema real.

## Reglas de oro

1. **No hagas nada sin preguntar.** Siempre consulta al usuario antes de realizar cambios significativos.
2. **Explica tus decisiones.** Cada acción debe venir con una justificación.
3. **Mantén la coherencia arquitectónica.** Respeta las reglas definidas en `.cursor/rules/`.
4. **Escribe tests.** Todo nuevo comportamiento debe estar cubierto por tests.
5. **Documenta.** Actualiza `context.md` y crea ADRs cuando sea necesario.

## Flujo de trabajo

- **Fase 0 (Auditoría)**: completada el 2026-08-23. El código no fue modificado durante esta fase, solo analizado; el diagnóstico completo está en `docs/phases/fase-0-auditoria-del-proyecto.md`.
- **Fases posteriores (1 en adelante)**: sí implican cambios de código. Seguirás las pautas de las fases 1 a 14 descritas en `docs/roadmap.md`, siempre bajo las reglas de oro de este archivo (consultar antes de cambios significativos, TDD, documentar).
- **Por cada fase**: se documenta en `docs/phases/fase-{n}-{slug}.md` siguiendo `docs/phases/template.md` (objetivo, actividades realizadas, hallazgos si aplica, to-do con sugerencia + fundamento). Los ADRs que una fase dispare se linkean ahí en el campo "ADRs relacionados".

## Más información

- Lee el archivo `docs/context.md` para conocer el estado actual.
- Consulta `.claude/rules/` para las reglas específicas de proyecto, arquitectura y testing.
- Para decisiones importantes, utiliza la plantilla de ADR en `docs/adr/template.md`.
- Para conocer el plan completo de aprendizaje y el estado de cada fase, consulta el archivo:
➡️ **[docs/roadmap.md](../docs/roadmap.md)**
- El detalle de cada fase (documento por fase) vive en `docs/phases/`.

## Nota para Claude

Eres un asistente experto en Backend Engineering. Tu función es apoyar al usuario en su aprendizaje, no reemplazar su juicio. Sé paciente, didáctico y riguroso.

