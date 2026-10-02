# AguardApp

App Android (Kotlin + Jetpack Compose) para saber cuánta agua queda en el reservorio del hogar y hasta cuándo alcanza.
Proviene de AguaTacna (Kotlin Multiplatform), migrada a un único módulo Android con arquitectura **MVVM + DDD**.

## Estructura

```
app/src/main/java/com/example/aguardapp/
├── AguardApplication.kt     Inicia la base de datos, Koin y la tarea horaria de la reserva
├── MainActivity.kt          Permisos de avisos y sincronización mientras la app está visible
├── App.kt                   Tema + puerta de acceso + navegación
├── core/                    Lo compartido por todas las features
│   ├── data/                Usuario local (UUID inmutable) y su DAO
│   ├── db/                  AguardAppDatabase (Room)
│   ├── di/                  Módulos de Koin e inicio de la aplicación
│   ├── navigation/          Barra inferior y destinos
│   ├── nube/                Cliente de Supabase
│   ├── sesion/              Modo de acceso e inicio con Google
│   ├── ui/theme/            Colores, tipografías y tema
│   └── util/                Reloj, UUID y captura de fotos
└── feature/
    ├── bienvenida/          Pantalla de entrada (sin cuenta / Google)
    ├── reserva/             Nivel del reservorio, proyección, avisos y recortes
    ├── sector/              Sector del domicilio, cronograma y cisternas
    ├── recibo/              Escaneo (OCR) e historial del recibo de agua
    └── retos/               Ahorro, retos, comunidad y reportes
```

Cada feature sigue las mismas capas:

| Capa | Contenido |
|---|---|
| `domain/` | `model`, `repository` (interfaces), `usecase` y, si aplica, `service`/`port`. Kotlin puro, sin Android. |
| `data/` | Implementaciones de repositorios, `local` (Room: entidades y DAO), `sync` (Supabase) y `mapper`. |
| `infrastructure/` | Adaptadores de Android: OCR con ML Kit (recibo), notificaciones, receiver y WorkManager (reserva). |
| `di/` | Módulo de Koin de la feature. |
| `presentation/` | `*Screen`, `*ViewModel` y `*UiState` (MVVM); `componentes/` agrupa los composables reutilizables. |

## Otros directorios

- `app/schemas/` — esquemas exportados de Room (para migraciones futuras).
- `supabase/migrations/` — tablas y políticas de la nube.

## Comandos

```bash
./gradlew :app:assembleDebug
```
