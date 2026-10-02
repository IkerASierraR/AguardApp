# Regla: Mantener README.md siempre actualizado

## Regla obligatoria

**Cada vez que modifiques código de la aplicación, tienes que actualizar también el archivo `README.md`, sin excepción.**

Esto incluye crear, editar, renombrar o eliminar archivos, funciones, componentes, endpoints, configuraciones, dependencias o scripts.

## Qué debes hacer en cada cambio

1. Haz el cambio de código que se pidió.
2. Antes de dar la tarea por terminada, abre `README.md` y actualiza las secciones afectadas:
   - **Descripción / funcionalidades**: agrega, modifica o quita las funcionalidades que cambiaron.
   - **Instalación y dependencias**: refleja cualquier paquete nuevo, eliminado o actualizado.
   - **Configuración / variables de entorno**: documenta variables nuevas o cambiadas.
   - **Uso / comandos / scripts**: actualiza comandos, ejemplos y parámetros.
   - **Estructura del proyecto**: refleja carpetas o archivos nuevos, movidos o eliminados.
   - **API / endpoints** (si aplica): rutas, métodos, parámetros y respuestas.
   - **Changelog / historial de cambios**: agrega una entrada con la fecha y un resumen breve del cambio.
3. Si `README.md` no existe, créalo con al menos: nombre del proyecto, descripción, instalación, uso, estructura y changelog.
4. Comprueba que la información del README coincide con el código actual (sin datos desactualizados ni contradicciones).

## Formato del changelog

```markdown
## Changelog

### AAAA-MM-DD
- [Agregado | Cambiado | Corregido | Eliminado] Descripción breve del cambio.
```

## Restricciones

- No des por terminada ninguna tarea que modifique código si `README.md` no se actualizó en el mismo cambio.
- No borres información válida del README; solo actualiza lo que el cambio afecta.
- Escribe el README de forma clara, concisa y en el mismo idioma que ya usa.
- Si el cambio es mínimo (por ejemplo, un refactor interno sin impacto visible), igual agrega una entrada en el changelog.

## Verificación final (checklist)

Antes de terminar, confirma:

- [ ] El código fue modificado según lo pedido.
- [ ] `README.md` fue actualizado en las secciones relevantes.
- [ ] Se agregó una entrada en el changelog.
- [ ] El README coincide con el estado actual del código.
