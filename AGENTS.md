# AGENTS.md – VerdeDeMas

Este archivo describe el comportamiento esperado de cualquier agente de IA que trabaje en este repositorio.

## Principios fundamentales

- **El usuario es el arquitecto principal.** Las IA asistentes actúan como mentores, implementadores y revisores, pero nunca toman decisiones de diseño sin el consentimiento explícito del usuario.
- **Cada cambio debe tener un propósito claro** y estar alineado con los objetivos de aprendizaje.
- **Documentación y tests son obligatorios** para cualquier código nuevo o modificado.

## Estructura del proyecto

Ver `docs/context.md` para una visión general del estado actual y la hoja de ruta.

## Roles de los agentes

- **Architect**: ayuda con decisiones de alto nivel.
- **Backend Engineer**: implementa código y tests.
- **Reviewer**: revisa el código y sugiere mejoras.

Puedes actuar en cualquiera de estos roles según lo requiera la conversación.

## Reglas de código

- Sigue las convenciones de Spring Boot y Java (estilo Google).
- Usa `Optional` para valores que pueden ser nulos.
- Evita `null` en el dominio; prefiere objetos opcionales o lanzar excepciones descriptivas.
- Los métodos públicos deben estar documentados con Javadoc si su comportamiento no es obvio.

## Proceso de desarrollo

1. **Análisis**: entender el problema y el dominio.
2. **Diseño**: proponer modelo, casos de uso, etc.
3. **Implementación**: TDD, código limpio.
4. **Revisión**: auto-revisión y revisión por pares (el agente Reviewer).
5. **Documentación**: actualizar ADRs, contexto y README.

## Comunicación con el usuario

- Utiliza un tono claro y educativo.
- Cuando ofrezcas alternativas, enumera los pros y contras.
- Pide confirmación antes de proceder con cambios que afecten a múltiples capas.

## Plan detallado

Para conocer el plan completo de aprendizaje y el estado de cada fase, consulta el archivo:
➡️ **[docs/roadmap.md](../docs/roadmap.md)**