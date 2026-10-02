<p align="center">
  <img src="https://img.shields.io/badge/💧_AguardApp-Gestión_Hídrica-0ea5e9?style=for-the-badge&labelColor=0c4a6e" alt="AguardApp"/>
</p>

<h1 align="center">💧 AguardApp</h1>

<p align="center">
  <em>Sistema móvil para la gestión de la reserva domiciliaria de agua y la anticipación de cortes durante el racionamiento hídrico en Tacna</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Room-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room"/>
  <img src="https://img.shields.io/badge/Koin-F5A623?style=for-the-badge&logo=koin&logoColor=white" alt="Koin"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Plataforma-Android_8.0+-34A853?style=flat-square&logo=android&logoColor=white" alt="Android 8.0+"/>
  <img src="https://img.shields.io/badge/Min_SDK-24-blue?style=flat-square" alt="Min SDK 24"/>
  <img src="https://img.shields.io/badge/Target_SDK-36-blue?style=flat-square" alt="Target SDK 36"/>
  <img src="https://img.shields.io/badge/Arquitectura-MVVM_+_DDD-purple?style=flat-square" alt="MVVM + DDD"/>
  <img src="https://img.shields.io/badge/Versión-1.0-orange?style=flat-square" alt="Versión 1.0"/>
</p>

---

## 👥 Integrantes del Proyecto

| # | Integrante | Código | Rol |
|:-:|---|---|---|
| 🔵 | **Mamani Cori, Cristhian Carlos** | `2023077282` | Jefe de Proyecto · Core y Reserva |
| 🟢 | **Jahuira Pilco, Dayan Elvis** | `2022075749` | Sector y Mapas |
| 🟠 | **Sierra Ruiz, Iker Alberto** | `2023077090` | Recibo e IA · Líder de Pruebas |
| 🟣 | **Llica Mamani, Jimmy Mijair** | `2023076789` | Retos, Reportes y Despliegue |

> **Universidad Privada de Tacna** · Facultad de Ingeniería · Escuela Profesional de Ingeniería de Sistemas  
> **Curso:** Soluciones Móviles I · **Docente:** Mag. Alberto Johnatan Flor Rodríguez

---

## ❗ Problemática

La ciudad de **Tacna** se abastece de agua potable bajo un esquema de **racionamiento por sectores**, con servicio solo durante algunas horas al día. Los hogares enfrentan:

| Problema | Impacto |
|---|---|
| 🔴 **Sin información clara** de cuándo llega el agua a su sector | Desabastecimiento imprevisto |
| 🔴 **Sin control** sobre cuánta reserva queda en el hogar | Desperdicio del recurso |
| 🔴 **Información oficial dispersa** y poco confiable | Mala planificación del consumo |
| 🔴 **Compra de agua de emergencia** a sobreprecio | Gasto innecesario |

```mermaid
flowchart TD
    A([Inicio]) --> B["El vecino escucha rumores<br/>del horario del agua"]
    B --> C{"¿Llegó el agua?"}
    C -- No --> D["Espera sin saber<br/>cuándo llegará"]
    D --> C
    C -- Sí --> E["Llena tanques y<br/>recipientes de golpe"]
    E --> F["Usa el agua sin<br/>controlar la reserva"]
    F --> G{"¿Se quedó sin agua?"}
    G -- Sí --> H["💸 Compra agua de<br/>emergencia a sobreprecio"]
    G -- No --> Z([Fin])
    H --> Z
```

---

## ✅ Solución

**AguardApp** es una aplicación Android que permite a los hogares de Tacna:

| Funcionalidad | Descripción |
|---|---|
| 📊 **Gestionar el depósito** | Saber cuántos litros quedan y hasta cuándo alcanza |
| 💧 **Registrar llenados** | Un toque cuando llega el agua (completo o a la mitad) |
| 📉 **Aprender el consumo** | Ajusta la proyección cuando el agua se acaba antes de lo previsto |
| 🔔 **Avisos proactivos** | Notificaciones antes del agotamiento |
| 📡 **100 % local** | Funciona sin internet: todo se guarda en el teléfono (SQLite) |

---

## 📐 Factibilidad

Según el Informe de Factibilidad **(FD01, Versión 1.0)**, el proyecto es viable en todas las dimensiones:

| Dimensión | Estado | Detalle |
|---|:-:|---|
| 🔧 **Técnica** | ✅ | Equipo competente, tecnologías gratuitas, MVP operativo |
| 💰 **Económica** | ✅ | Inversión: S/ 17,720.00 · VAN positivo · TIR: 22.2% |
| ⚙️ **Operativa** | ✅ | Flujos simples, interfaz accesible |
| ⚖️ **Legal** | ✅ | Cumple Ley N.° 29733 de Protección de Datos Personales |
| 🌱 **Social y Ambiental** | ✅ | Fomenta el ahorro y consumo responsable del agua |

---

## 🎯 Visión y Misión

> ### 🔭 Visión
> Ser un referente en el desarrollo de soluciones móviles con **impacto social** en la región de Tacna, aplicando buenas prácticas de ingeniería de software para resolver problemas reales de la comunidad.

> ### 🚀 Misión
> Desarrollar aplicaciones móviles **accesibles y de calidad** que mejoren la vida de los ciudadanos; para este proyecto, ayudando a los hogares de Tacna a gestionar su reserva de agua durante el racionamiento.

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnologías |
|---|---|
| **Lenguaje** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white) |
| **UI** | ![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white) ![Material 3](https://img.shields.io/badge/Material_3-757575?style=flat-square&logo=materialdesign&logoColor=white) |
| **Base de datos** | ![Room](https://img.shields.io/badge/Room_(SQLite)-003B57?style=flat-square&logo=sqlite&logoColor=white) |
| **DI** | ![Koin](https://img.shields.io/badge/Koin-F5A623?style=flat-square) |
| **Tareas** | ![WorkManager](https://img.shields.io/badge/WorkManager-34A853?style=flat-square&logo=android&logoColor=white) |
| **Fechas** | ![kotlinx.datetime](https://img.shields.io/badge/kotlinx.datetime-7F52FF?style=flat-square) |
| **Arquitectura** | ![MVVM](https://img.shields.io/badge/MVVM-purple?style=flat-square) ![DDD](https://img.shields.io/badge/DDD-darkblue?style=flat-square) ![Clean Architecture](https://img.shields.io/badge/Clean_Architecture-teal?style=flat-square) |

---

## 📂 Estructura del Proyecto

```
AguardApp/
├── 📄 build.gradle.kts                    Configuración raíz de Gradle
├── 📄 settings.gradle.kts                 Módulos y repositorios
├── 📄 Readme.md                           ← Estás aquí
│
├── 📁 app/                                Módulo principal Android
│   ├── 📄 build.gradle.kts                Dependencias y plugins
│   ├── 📁 schemas/                        Esquemas exportados de Room
│   └── 📁 src/main/
│       ├── 📄 AndroidManifest.xml
│       ├── 📁 res/                        Recursos (iconos, strings, etc.)
│       └── 📁 java/com/example/aguardapp/
│           ├── 📄 AguardApplication.kt    Inicia DB, Koin y tarea horaria
│           ├── 📄 MainActivity.kt         Permiso de avisos
│           ├── 📄 App.kt                  Tema + acceso + depósito
│           │
│           ├── 📁 core/                   🔧 Infraestructura transversal
│           │   ├── 📁 data/               Usuario local (UUID) y DAO
│           │   ├── 📁 db/                 AguardAppDatabase (Room)
│           │   ├── 📁 di/                 Módulos de Koin
│           │   ├── 📁 ui/theme/           Colores, tipografías y tema
│           │   └── 📁 util/               Reloj y UUID
│           │
│           └── 📁 feature/               📦 Features (cada uno con DDD)
│               ├── 📁 bienvenida/         Pantalla de entrada (solo el primer uso)
│               └── 📁 deposito/           Depósito de agua del hogar
│
└── 📁 informes/                           📋 Documentación académica
    ├── 📄 FD01-Factibilidad.md
    ├── 📄 FD02-Vision.md
    ├── 📄 FD03-SRS.md
    ├── 📄 FD04-SAD.md
    └── 📄 FD05-Informe-Final.md
```

### Capas internas de cada Feature

Cada feature sigue el patrón **MVVM + DDD** con estas capas:

| Capa | Contenido | Depende de |
|---|---|---|
| 📐 `domain/` | `model/`, `repository/` (interfaces), `usecase/` — Kotlin puro | Nada |
| 💾 `data/` | Implementaciones de repositorios, `local/` (Room), `mapper/` | `domain/` |
| ⚙️ `infrastructure/` | Adaptadores Android: notificaciones y WorkManager | `domain/` |
| 🔌 `di/` | Módulo de Koin de la feature | Todas |
| 🖼️ `presentation/` | `*Screen`, `*ViewModel`, `*UiState` (MVVM), `componentes/` | `domain/` |

---

## 🏗️ Diagrama C4 — Nivel de Contexto

```mermaid
C4Context
    title Sistema AguardApp — Diagrama de Contexto

    Person(user, "Vecino de Tacna", "Hogar que gestiona su reserva de agua durante el racionamiento")

    System(app, "AguardApp", "App Android que gestiona el depósito de agua del hogar y emite avisos")

    System_Ext(android, "Android", "Notificaciones y tareas en segundo plano (WorkManager)")

    Rel(user, app, "Usa", "Android")
    Rel(app, android, "Programa avisos", "SDK")
```

## 🏗️ Diagrama C4 — Nivel de Contenedores

```mermaid
C4Container
    title Sistema AguardApp — Diagrama de Contenedores

    Person(user, "Vecino de Tacna")

    Container_Boundary(mobile, "App Android") {
        Container(ui, "Presentation Layer", "Jetpack Compose", "Screens, ViewModels y UiStates con MVVM")
        Container(domain, "Domain Layer", "Kotlin puro", "Models, UseCases y Repositories (interfaces)")
        Container(data, "Data Layer", "Room", "Implementación de repositorios, DAOs y Entities")
        Container(infra, "Infrastructure", "WorkManager", "Notificaciones y tareas en segundo plano")
        ContainerDb(roomdb, "Room Database", "SQLite", "Fuente de verdad local: perfil del hogar, llenados y avisos")
    }


    Rel(user, ui, "Interactúa")
    Rel(ui, domain, "Usa UseCases")
    Rel(data, domain, "Implementa interfaces")
    Rel(infra, domain, "Implementa interfaces")
    Rel(data, roomdb, "Lee/Escribe")
```

## 🏗️ Diagrama C4 — Nivel de Componentes (Feature Depósito)

```mermaid
C4Component
    title Feature Depósito — Nivel de Componentes

    Container_Boundary(deposito, "feature/deposito") {
        Component(screen, "DepositoScreen", "Compose", "Pantalla principal del depósito")
        Component(vm, "DepositoViewModel", "MVVM", "Gestiona el estado de la UI del depósito")
        Component(uc_armar, "ArmarDeposito", "Domain", "Calcula nivel, consumo y agotamiento")
        Component(uc_avisar, "RecalcularYAvisar", "Domain", "Avisa cuando el agua está por acabarse")
        Component(repo_if, "DepositoRepository", "Interface", "Contrato de acceso a datos del depósito")
        Component(repo_impl, "DepositoRepositoryImpl", "Data", "Implementación con Room")
        Component(dao, "DepositoDao", "Room", "Queries SQLite para perfil, llenados y novedades")
    }

    Rel(screen, vm, "Observa UiState")
    Rel(vm, repo_if, "Usa")
    Rel(repo_impl, uc_armar, "Invoca")
    Rel(uc_avisar, repo_if, "Usa")
    Rel(repo_impl, repo_if, "Implementa")
    Rel(repo_impl, dao, "Lee/Escribe")
```

---

## 📊 Diagrama de Clases Principal

```mermaid
classDiagram
    class Usuario {
        id
        bienvenidaCompletada
    }
    class PerfilHogar {
        tipoReservorio
        capacidadLitros
        habitantes
    }
    class Deposito {
        nivelLitros
        consumoEstimado
        agotamientoProyectado
    }
    class EventoLlenado {
        fechaHora
        tipo
    }
    class Aviso {
        agotamiento
    }

    Usuario "1" -- "1" PerfilHogar
    PerfilHogar "1" -- "0..1" Deposito
    Deposito "1" -- "0..*" EventoLlenado
    Deposito ..> Aviso : genera
```

---

## 🔧 Requisitos Previos

| Requisito | Versión |
|---|---|
| Android Studio | Ladybug o superior |
| JDK | 11+ |
| Android SDK | API 24 (mínimo) — API 36 (target) |
| Dispositivo/Emulador | Android 8.0+ |

## 🚀 Cómo Ejecutar

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/AguardApp.git

# 2. Abrir en Android Studio

# 3. Sincronizar Gradle y ejecutar
./gradlew :app:assembleDebug

# O simplemente presionar ▶️ Run en Android Studio
```

---

## 📋 Requerimientos Funcionales Principales

| ID | Requerimiento | Prioridad |
|---|---|:-:|
| RF-04 | Configurar perfil del hogar | 🔴 Alta |
| RF-06 | Calcular depósito y agotamiento | 🔴 Alta |
| RF-11 | Operar sin conexión | 🔴 Alta |

> 📄 Ver documentación completa en [`informes/FD03-SRS.md`](./informes/FD03-SRS.md)

---

## 📚 Documentación

| Documento | Descripción |
|---|---|
| [`FD01-Factibilidad.md`](./informes/FD01-Factibilidad.md) | Estudio de factibilidad técnica, económica y operativa |
| [`FD02-Vision.md`](./informes/FD02-Vision.md) | Documento de visión del proyecto |
| [`FD03-SRS.md`](./informes/FD03-SRS.md) | Especificación de Requerimientos de Software |
| [`FD04-SAD.md`](./informes/FD04-SAD.md) | Documento de Arquitectura de Software |
| [`FD05-Informe-Final.md`](./informes/FD05-Informe-Final.md) | Informe final del proyecto |

---

## 📜 Licencia

Proyecto académico desarrollado en la **Universidad Privada de Tacna** — 2026.

---

<p align="center">
  <sub>Hecho con 💧 para los hogares de Tacna</sub>
</p>
