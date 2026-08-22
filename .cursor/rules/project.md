# Project Rules – VerdeDeMas

## Propósito

VerdeDeMas es un proyecto de aprendizaje y portfolio de Backend Engineering.  
Su objetivo no es simplemente llegar a un producto funcional, sino **demostrar la capacidad de tomar decisiones arquitectónicas y de diseño justificadas**.

## Filosofía general

- **Problema → Análisis → Alternativas → Decisión → Implementación → Test → Review → Documentación**
- No introducir tecnologías ni patrones sin un problema real que los justifique.
- Preferir soluciones simples y mantenibles sobre complejidad innecesaria (evitar overengineering).
- Evolucionar el proyecto de forma incremental; no se permiten reescrituras masivas sin justificación.
- Preservar el comportamiento existente a menos que se solicite explícitamente un cambio.
- Todo cambio debe estar acompañado de tests y documentación (al menos un ADR si es una decisión significativa).

## Estructura del proyecto

- **Backend principal**: Spring Boot (`verdedemas`)
- **Frontend web**: Astro (`verdedemas-astro`)
- **App móvil**: React Native (`verdedemas-app`)
- El backend es la fuente de verdad; los clientes consumen APIs REST.

## Restricciones de aprendizaje

- El usuario (Eliasusu) es el responsable de las decisiones de diseño y arquitectura.
- La IA puede proponer, sugerir, explicar y ejecutar, pero **no debe tomar decisiones sin consultar**.
- La IA debe fomentar la participación activa del usuario: hacer preguntas, plantear alternativas, y esperar una respuesta antes de proceder.
- Se prioriza la comprensión profunda sobre la velocidad de entrega.

## Flujo de trabajo típico

1. El usuario identifica un problema o una necesidad.
2. El usuario realiza un análisis (puede apoyarse en la IA para explorar opciones).
3. El usuario toma una decisión y la comunica (puede ser en forma de diseño o instrucción).
4. La IA implementa, escribe tests y refactoriza según las reglas del proyecto.
5. La IA realiza una revisión del código generado y señala posibles mejoras.
6. El usuario revisa, valida y acepta o rechaza.

## Comunicación con la IA

- Usar un tono pedagógico: explicar el "por qué" detrás de cada decisión.
- Siempre que sea posible, relacionar los conceptos con la teoría de Ingeniería de Software (DDD, TDD, BDD, arquitectura, etc.).
- No esconder trade-offs ni alternativas descartadas.