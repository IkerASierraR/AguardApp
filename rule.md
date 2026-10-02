# Reglas: Mantener documentación siempre actualizada

## Regla 1 — README.md (obligatoria)

**Cada vez que modifiques código de la aplicación, tienes que actualizar también el archivo `README.md`, sin excepción.**

Esto incluye crear, editar, renombrar o eliminar archivos, funciones, componentes, endpoints, configuraciones, dependencias o scripts.

### Qué debes hacer en cada cambio

1. Haz el cambio de código que se pidió.
2. Antes de dar la tarea por terminada, abre `README.md` y actualiza las secciones afectadas:
   - **Descripción / funcionalidades**: agrega, modifica o quita las funcionalidades que cambiaron.
   - **Instalación y dependencias**: refleja cualquier paquete nuevo, eliminado o actualizado.
   - **Configuración / variables de entorno**: documenta variables nuevas o cambiadas.
   - **Uso / comandos / scripts**: actualiza comandos, ejemplos y parámetros.
   - **Estructura del proyecto**: refleja carpetas o archivos nuevos, movidos o eliminados.
   - **Stack tecnológico**: agrega o elimina tecnologías según las dependencias actuales.
   - **Diagramas C4**: actualiza si cambia la arquitectura, los contenedores o los componentes.
   - **Requerimientos funcionales**: actualiza si se implementan, modifican o eliminan RFs.
3. Si `README.md` no existe, créalo con al menos: nombre del proyecto, descripción, instalación, uso, estructura y changelog.
4. Comprueba que la información del README coincide con el código actual (sin datos desactualizados ni contradicciones).

---

## Regla 2 — Informes académicos (obligatoria)

**Cuando un cambio de código afecte el contenido de los informes académicos, debes actualizarlos también.**

Los informes se encuentran en `informes/` y se dividen en dos grupos:

### Informes que SÍ debes actualizar (FD03, FD04, FD05)

| Archivo | Documento | Cuándo actualizarlo |
|---|---|---|
| `FD03-SRS.md` | Especificación de Requerimientos | Al agregar, modificar o eliminar requerimientos funcionales/no funcionales, casos de uso, reglas de negocio, diagramas de clases, de secuencia o de actividades. |
| `FD04-SAD.md` | Arquitectura de Software | Al cambiar la estructura de capas, paquetes, módulos, dependencias, diagramas de componentes, de despliegue, modelo de datos o decisiones arquitectónicas. |
| `FD05-Informe-Final.md` | Informe Final | Al cambiar funcionalidades implementadas, resultados, cronograma, tecnologías usadas o conclusiones del proyecto. |

### Secciones típicas que se ven afectadas

**FD03-SRS.md:**
- Cuadro de requerimientos funcionales (inicial y final)
- Cuadro de requerimientos no funcionales
- Reglas de negocio
- Diagrama de casos de uso y sus narrativas
- Diagrama de clases
- Diagrama de secuencia
- Diagrama de actividades

**FD04-SAD.md:**
- Diagrama de paquetes y módulos
- Diagrama de componentes
- Diagrama de despliegue
- Modelo de datos (entidades y relaciones)
- Vista lógica (capas y dependencias)
- Vista de implementación (estructura de código)
- Vista de procesos (flujos y concurrencia)

**FD05-Informe-Final.md:**
- Desarrollo de la solución (funcionalidades implementadas)
- Marco teórico (si se agregan tecnologías nuevas)
- Conclusiones y recomendaciones
- Cronograma (si cambian las fechas o entregables)

### Informes que NO se actualizan (FD01, FD02)

| Archivo | Documento | Motivo |
|---|---|---|
| `FD01-Factibilidad.md` | Estudio de Factibilidad | Documento cerrado; los datos económicos y de viabilidad no cambian con el código. |
| `FD02-Vision.md` | Documento de Visión | Documento cerrado; la visión del proyecto se definió al inicio y no se modifica. |

---

## Formato del changelog (README.md)

```markdown
## Changelog

### AAAA-MM-DD
- [Agregado | Cambiado | Corregido | Eliminado] Descripción breve del cambio.
```

---

## Restricciones

- No des por terminada ninguna tarea que modifique código si `README.md` no se actualizó en el mismo cambio.
- Si el cambio afecta contenido de FD03, FD04 o FD05, actualiza esos archivos también antes de terminar.
- No borres información válida de ningún documento; solo actualiza lo que el cambio afecta.
- Escribe en el mismo idioma que ya usa cada documento (español).
- Si el cambio es mínimo (por ejemplo, un refactor interno sin impacto visible), igual agrega una entrada en el changelog del README.
- Mantén los diagramas Mermaid sincronizados con el código: si cambias una clase, un paquete o un flujo, actualiza el diagrama correspondiente.

---

## Verificación final (checklist)

Antes de terminar, confirma:

- [ ] El código fue modificado según lo pedido.
- [ ] `README.md` fue actualizado en las secciones relevantes.
- [ ] Se agregó una entrada en el changelog del README.
- [ ] El README coincide con el estado actual del código.
- [ ] Si el cambio afecta requerimientos o casos de uso → `FD03-SRS.md` fue actualizado.
- [ ] Si el cambio afecta arquitectura, capas o modelo de datos → `FD04-SAD.md` fue actualizado.
- [ ] Si el cambio afecta funcionalidades implementadas o resultados → `FD05-Informe-Final.md` fue actualizado.
- [ ] Los diagramas Mermaid reflejan el estado actual del sistema.
