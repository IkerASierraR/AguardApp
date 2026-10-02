# FD04 — Documento de Arquitectura de Software (SAD)

## Proyecto AguardApp

**Sistema:** AguardApp — sistema móvil para la gestión del depósito domiciliario de agua durante el racionamiento hídrico en Tacna.  
**Curso:** Soluciones Móviles I  
**Docente:** Mag. Alberto Johnatan Flor Rodríguez  
**Versión:** 2.0  
**Fecha:** 02/10/2026  
**Lugar:** Tacna — Perú, 2026

### Integrantes

- Jahuira Pilco, Dayan Elvis (2022075749)
- Llica Mamani, Jimmy Mijair (2023076789)
- Mamani Cori, Cristhian Carlos (2023077282)
- Sierra Ruiz, Iker Alberto (2023077090)

---

## Control de versiones

| Versión | Hecha por | Revisada por | Aprobada por | Fecha | Motivo |
|---|---|---|---|---|---|
| 1.0 | DJ - JL - CM - IS | AFR | AFR | 30/09/2026 | Versión original |
| 2.0 | DJ - JL - CM - IS | — | — | 02/10/2026 | Actualización a la arquitectura de AguardApp: Android nativo, MVVM + DDD, módulo Depósito y base de datos local |

---

# 1. Introducción

## 1.1. Propósito — Modelo 4+1

Este documento presenta una visión global de la arquitectura del sistema AguardApp utilizando el modelo de vistas 4+1 de Kruchten. Describe las decisiones arquitectónicas significativas mediante cinco vistas complementarias: casos de uso, lógica, implementación o desarrollo, procesos y despliegue físico.

```mermaid
flowchart TB
    UC[Vista de Casos de Uso] --> LOG[Vista Lógica]
    UC --> DEV[Vista de Implementación]
    UC --> PROC[Vista de Procesos]
    UC --> DEP[Vista de Despliegue]
    LOG <--> DEV
    LOG <--> PROC
    DEV <--> DEP
    PROC <--> DEP
```

## 1.2. Alcance

El documento describe la arquitectura de la versión actual de AguardApp: una aplicación Android nativa de un solo módulo (`app`) con dos funcionalidades (`bienvenida` y `deposito`), persistencia local con Room y sin servicios externos. Cubre la organización en capas y paquetes, la interacción entre objetos, el modelo de datos, los componentes, los procesos y el despliegue. Sirve de guía para el desarrollo y el mantenimiento del sistema.

## 1.3. Definiciones, siglas y abreviaturas

| Término | Definición |
|---|---|
| SAD | Software Architecture Document (Documento de Arquitectura de Software). |
| Modelo 4+1 | Modelo de Kruchten con cinco vistas arquitectónicas complementarias. |
| MVVM | Model-View-ViewModel, patrón de la capa de presentación. |
| DDD | Domain-Driven Design: el código se organiza alrededor del dominio (modelos, repositorios y casos de uso). |
| Jetpack Compose | Kit de interfaz declarativa de Android. |
| Room | Biblioteca de persistencia local sobre SQLite. |
| Koin | Biblioteca de inyección de dependencias para Kotlin. |
| UiState | Clase de datos con todo lo que una pantalla necesita mostrar. |
| QA | Quality Attribute (atributo de calidad del software). |

## 1.4. Organización del documento

El documento se organiza en cuatro secciones: introducción; objetivos y restricciones arquitectónicas; representación de la arquitectura mediante las vistas del modelo 4+1; y atributos de calidad del software.

---

# 2. Objetivos y restricciones arquitectónicas

## 2.1. Priorización de requerimientos

### 2.1.1. Requerimientos funcionales

| ID | Descripción | Prioridad |
|---|---|---|
| RF-01 | Mostrar la bienvenida y pedir el consentimiento la primera vez | Alta |
| RF-02 | Configurar el hogar (depósito, habitantes, hábitos y hora del próximo llenado) | Alta |
| RF-03 | Registrar un llenado completo o parcial | Alta |
| RF-04 | Consultar el depósito: nivel, hasta cuándo alcanza y consumo | Alta |
| RF-05 | Calcular el déficit hasta el próximo llenado | Alta |
| RF-06 | Recomendar recortes de consumo | Media |
| RF-07 | Declarar que se quedó sin agua y ajustar el consumo | Media |
| RF-08 | Operar sin conexión | Alta |

### 2.1.2. Requerimientos no funcionales — atributos de calidad

| ID | Atributo | Descripción | Prioridad |
|---|---|---|---|
| RNF-01 | Disponibilidad | Funcionamiento 100 % sin conexión, con Room como única fuente de verdad. | Alta |
| RNF-02 | Privacidad y seguridad | Ningún dato sale del teléfono; la aplicación no solicita permisos; identidad con UUID local. | Alta |
| RNF-03 | Usabilidad | Interfaz simple, una sola fuente, colores sólidos y errores claros debajo de cada campo. | Alta |
| RNF-04 | Compatibilidad | Android 7.0 (API 24) o superior; probado contra la API 36. | Media |
| RNF-05 | Mantenibilidad | MVVM + DDD; dominio en Kotlin puro, independiente de Android; un Screen y un ViewModel por pantalla. | Alta |

## 2.2. Restricciones

- La aplicación debe funcionar sin conexión, con la base de datos local como única fuente de verdad.
- El dominio (`domain/`) no puede depender de Android, Room ni Koin: es Kotlin puro.
- Solo se usan tecnologías gratuitas y de código abierto; no hay backend ni servicios en la nube.
- La persistencia se realiza con Room; el esquema se exporta en `app/schemas/`.
- Las pantallas no contienen lógica de negocio: solo pintan el `UiState` y llaman funciones del ViewModel.

---

# 3. Representación de la arquitectura del sistema

## 3.1. Vista de casos de uso

El único actor es el jefe de hogar, que usa la aplicación en su propio teléfono.

```mermaid
flowchart LR
    JH[Jefe de hogar]

    subgraph SYS[Sistema AguardApp]
      U1((Ver bienvenida y aceptar))
      U2((Configurar el hogar))
      U3((Registrar llenado))
      U4((Consultar mi depósito))
      U5((Consultar qué recortar))
      U6((Declarar que se quedó sin agua))
    end

    JH --> U1
    JH --> U2
    JH --> U3
    JH --> U4
    JH --> U5
    JH --> U6
```

## 3.2. Vista lógica

### 3.2.1. Diagrama de subsistemas / paquetes

```mermaid
flowchart TB
  subgraph APP[Aplicación AguardApp - módulo app]
    MAIN[AguardApplication / MainActivity / App]
    subgraph CORE[core]
      NAV[navigation: Rutas, AppNavHost, InicioViewModel]
      DI[di: AppModule]
      DB[db: AguardAppDatabase]
      DATA[data: Usuario]
      THEME[ui.theme]
      UTIL[util: Reloj, UUID]
    end
    BIEN[feature/bienvenida: presentation]
    subgraph DEP[feature/deposito]
      PRES[presentation]
      DOM[domain]
      DAT[data]
    end

    MAIN --> NAV
    MAIN --> DI
    NAV --> BIEN
    NAV --> PRES
    PRES --> DOM
    DAT -. implementa .-> DOM
    DI --> DB
    DI --> DAT
    BIEN --> DATA
  end
```

### 3.2.2. Diagramas de secuencia — vista de diseño

#### Módulo 1: Inicio y bienvenida

##### 1.1. Inicio de la aplicación y elección de la primera pantalla

```mermaid
sequenceDiagram
    actor U as Usuario
    participant APP as AguardApplication
    participant DI as AppModule
    participant DB as AguardAppDatabase
    participant IVM as InicioViewModel
    participant NAV as AppNavHost
    U->>APP: Abre la aplicación
    APP->>DI: iniciarAplicacion(context)
    DI->>DB: crear(context)
    DI->>DB: usuarioDao().obtener()
    alt Primera vez
        DI->>DB: guardar(UsuarioEntity(nuevoUuid()))
    end
    DI-->>APP: Koin iniciado con el usuarioId
    APP->>IVM: (App) observa uiState
    IVM->>DB: observarBienvenidaCompletada()
    IVM->>DB: observarPerfil()
    alt Bienvenida pendiente
        IVM-->>NAV: rutaInicial = bienvenida
    else Hogar sin configurar
        IVM-->>NAV: rutaInicial = configurar_hogar
    else Todo listo
        IVM-->>NAV: rutaInicial = mi_deposito
    end
```

##### 1.2. Aceptar y empezar

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as BienvenidaScreen
    participant VM as BienvenidaViewModel
    participant DAO as UsuarioDao
    participant NAV as AppNavHost
    U->>S: Marca el consentimiento y toca "Empezar"
    S->>VM: onEmpezar()
    VM->>DAO: completarBienvenida()
    DAO-->>VM: OK
    VM-->>S: uiState.listo = true
    S->>NAV: onListo()
    NAV-->>U: Configurar hogar (o Mi depósito si ya había perfil)
```

#### Módulo 2: Depósito

##### 2.1. Configuración del hogar

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as ConfiguracionScreen
    participant VM as ConfiguracionViewModel
    participant EC as EstimarConsumo
    participant R as DepositoRepositoryImpl
    participant DAO as DepositoDao
    U->>S: Ajusta capacidad, habitantes, hábitos y hora
    S->>VM: onCapacidadChange() / onHabitantesChange() / onHoraChange()
    VM->>EC: porHabitos(habitos, habitantes)
    EC-->>VM: Consumo estimado (L/h)
    VM-->>S: uiState con errores por campo y puedeGuardar
    U->>S: Toca "Guardar"
    S->>VM: onGuardar()
    VM->>R: guardarPerfil(configuracion)
    R->>EC: porHabitos(...)
    R->>DAO: guardarPerfil(PerfilHogarEntity)
    DAO-->>R: OK
    R-->>VM: Result.success
    VM-->>S: guardado = true
    S-->>U: Navega a Mi depósito (o vuelve atrás si estaba editando)
```

##### 2.2. Registro de un llenado (completo o parcial)

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as RegistrarLlenadoScreen
    participant VM as RegistrarLlenadoViewModel
    participant R as DepositoRepositoryImpl
    participant DAO as DepositoDao
    U->>S: Elige llenado completo o parcial
    S->>VM: (parcial) onLitrosChange(texto)
    VM-->>S: errorLitros si es 0 o supera la capacidad
    U->>S: Confirma
    S->>VM: onGuardar()
    VM->>R: registrarLlenado(ahora, litros)
    R->>R: valida: no futuro, 0 < litros <= capacidad
    R->>DAO: guardarLlenado(EventoLlenadoEntity)
    R->>DAO: guardarPerfil(consumoVigente = null)
    DAO-->>R: OK
    R-->>VM: Result.success
    VM-->>S: guardado = true
    S-->>U: popBackStack() a Mi depósito
```

##### 2.3. Monitoreo reactivo del depósito

```mermaid
sequenceDiagram
    participant S as DepositoScreen
    participant VM as DepositoViewModel
    participant R as DepositoRepositoryImpl
    participant DAO as DepositoDao
    participant AD as ArmarDeposito
    participant CD as CalcularDeficit
    S->>VM: collectAsStateWithLifecycle()
    loop Cada cambio en Room o cada minuto
        VM->>R: observarPerfil() + observarDeposito()
        R->>DAO: perfil, llenados y novedades (Flow)
        DAO-->>R: Datos locales
        R->>AD: invoke(perfil, historial, ahora)
        AD-->>R: Deposito (consumo estimado)
        R-->>VM: PerfilHogar y Deposito
        VM->>CD: proximoLlenado(hora, ahora)
        VM->>CD: litrosQueFaltan(deposito, proximo, ahora)
        CD-->>VM: Déficit en litros
        VM-->>S: DepositoUiState(vista)
    end
```

##### 2.4. Declaración de "me quedé sin agua"

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as SinAguaScreen
    participant VM as SinAguaViewModel
    participant R as DepositoRepositoryImpl
    participant DSA as DeclararSinAgua
    participant DAO as DepositoDao
    S->>VM: init → previsualizarSinAgua(ahora)
    VM->>R: previsualizarSinAgua(ahora)
    R->>DSA: invoke(deposito, intervalos, ahora)
    DSA-->>R: Consumo nuevo (tope ±30 %)
    R-->>VM: PrevisualizacionSinAgua
    VM-->>S: Comparación proyectado vs. ocurrido
    U->>S: Elige "Se acabó ahora" o escribe HH:mm
    S->>VM: onRegistrar()
    VM->>VM: valida formato, no futura, posterior al último llenado
    VM->>R: declararSinAgua(momento)
    R->>DAO: guardarNovedad(intervalo observado)
    R->>DAO: guardarPerfil(consumoVigente = nuevo)
    DAO-->>R: OK
    R-->>VM: Result.success
    VM-->>S: listo = true
    S-->>U: Vuelve a Mi depósito con el depósito en cero
```

##### 2.5. Qué recortar

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as QueRecortarScreen
    participant VM as QueRecortarViewModel
    participant R as DepositoRepository
    participant CR as CalcularRecortes
    U->>S: Toca "¿Qué puedo recortar?"
    S->>VM: QueRecortarViewModel()
    VM->>R: observarPerfil(), observarDeposito(), observarPlanRecortes()
    R-->>VM: Hogar, depósito y plan guardado
    VM->>CR: evaluar(perfil, deposito, plan, ahora)
    CR-->>VM: Déficit, recortes con litros y los ya marcados
    VM-->>S: Lista con casillas
    U->>S: Marca una recomendación
    S->>VM: onAlternar(recomendacion)
    VM->>R: guardarPlanRecortes(plan hasta el próximo llenado)
    VM->>CR: SituacionRecortes(litrosGanados, litrosQueFaltan)
    CR-->>VM: Ahorras X L / Aún te faltan Y L
    VM-->>S: uiState actualizado en vivo
```

### 3.2.3. Diagrama de colaboración

```mermaid
flowchart TB
    S[DepositoScreen]
    VM[DepositoViewModel]
    R[DepositoRepositoryImpl]
    AD[ArmarDeposito / EstimarConsumo]
    CD[CalcularDeficit]
    ROOM[(Room / SQLite)]

    S -- observa uiState --> VM
    VM -- observarPerfil / observarDeposito --> R
    R -- consulta Flow --> ROOM
    ROOM -- perfil, llenados, novedades --> R
    R -- arma el depósito --> AD
    VM -- próximo llenado y déficit --> CD
    VM -- DepositoUiState --> S
```

### 3.2.4. Diagrama de objetos

Ejemplo de un hogar con un tanque de 1 100 L, 4 personas, 2 duchas por día y lavadora, que llenó a las 5:10 a.m. Sin historial, el consumo sale de los hábitos: (4 × (60 + 2 × 30) + 100) / 12 = 48,3 L/h. A las 2:00 p.m. quedan 1 100 − 48,3 × 8,83 ≈ 673 L, y el tanque se agota a las 3:56 a.m. del día siguiente; como el agua vuelve a las 5:00 a.m., el déficit es de 48,3 × 15 − 673 ≈ 52 L.

```mermaid
classDiagram
    class perfilCasa {
      usuarioId = "3f2a…"
      tipoReservorio = TANQUE_ELEVADO
      capacidad = 1100 L
      habitantes = 4
      horaProximoLlenado = 05:00
    }
    class habitosCasa {
      duchasPorDia = 2
      usaLavadora = true
      riegaJardin = false
    }
    class llenadoHoy {
      momento = 2026-10-02T05:10
      litros = 1100 L
    }
    class depositoHoy {
      consumo = 48.3 L/h (por hábitos)
      nivelEn(14:00) = 673 L
      agotamientoProyectado = 2026-10-03T03:56
    }
    perfilCasa --> habitosCasa
    depositoHoy --> llenadoHoy
    perfilCasa ..> depositoHoy
```

### 3.2.5. Diagrama de clases

```mermaid
classDiagram
    class DepositoRepository {
      <<interface>>
      +observarPerfil() Flow~PerfilHogar?~
      +guardarPerfil(configuracion) Result
      +observarDeposito() Flow~Deposito?~
      +litrosPorHabitanteDia() Double?
      +registrarLlenado(momento, litros) Result
      +declararSinAgua(momento) Result
      +previsualizarSinAgua(momento) Result
    }
    class DepositoRepositoryImpl
    class DepositoDao {
      <<Room>>
    }
    class ArmarDeposito
    class DeclararSinAgua
    class EstimarConsumo {
      +consumo()
      +porHabitos()
      +litrosPorHabitanteDia()
      +intervalosEntreLlenados()
    }
    class CalcularDeficit {
      +proximoLlenado()
      +litrosQueFaltan()
    }
    class CalcularRecortes {
      +evaluar()
      +sugerir()
      +nuevoPlan()
    }
    class DepositoViewModel
    class ConfiguracionViewModel
    class RegistrarLlenadoViewModel
    class QueRecortarViewModel
    class SinAguaViewModel

    DepositoRepositoryImpl ..|> DepositoRepository
    DepositoRepositoryImpl --> DepositoDao
    DepositoRepositoryImpl --> ArmarDeposito
    DepositoRepositoryImpl --> DeclararSinAgua
    ArmarDeposito --> EstimarConsumo
    DeclararSinAgua --> EstimarConsumo
    DepositoViewModel --> DepositoRepository
    DepositoViewModel --> CalcularDeficit
    ConfiguracionViewModel --> DepositoRepository
    ConfiguracionViewModel --> EstimarConsumo
    RegistrarLlenadoViewModel --> DepositoRepository
    QueRecortarViewModel --> CalcularRecortes
    QueRecortarViewModel --> DepositoRepository
    DepositoViewModel --> CalcularRecortes
    CalcularRecortes --> CalcularDeficit
    SinAguaViewModel --> DepositoRepository
```

### 3.2.6. Diagrama de base de datos

Base de datos local `aguardapp.db` (Room, versión 3). De la versión 2 a la 3 se agregó la tabla `plan_recortes` con una migración automática (`AutoMigration`), que conserva los datos existentes.

```mermaid
erDiagram
    USUARIO ||--o| PERFIL_HOGAR : configura
    USUARIO ||--o{ EVENTO_LLENADO : registra
    USUARIO ||--o{ NOVEDAD_DEPOSITO : declara
    USUARIO ||--o| PLAN_RECORTES : planifica

    USUARIO {
      string id PK
      boolean bienvenidaCompletada
    }
    PERFIL_HOGAR {
      string usuarioId PK
      string tipoReservorio
      double capacidadLitros
      int habitantes
      int duchasPorDia
      boolean usaLavadora
      boolean riegaJardin
      string horaProximoLlenado
      double consumoPorHabitosLitrosHora
      double consumoVigenteLitrosHora
    }
    EVENTO_LLENADO {
      string id PK
      string usuarioId
      string momento
      double litros
    }
    NOVEDAD_DEPOSITO {
      string id PK
      string usuarioId
      string momento
      string inicioObservado
      double litrosObservados
    }
    PLAN_RECORTES {
      string usuarioId PK
      string llenado
      string hasta
      string recortes
    }
```

## 3.3. Vista de implementación — desarrollo

### 3.3.1. Diagrama de arquitectura de software / paquetes

```mermaid
flowchart LR
    subgraph P[Presentación - MVVM]
      UI[Screens Jetpack Compose]
      VM[ViewModels + UiState]
    end
    subgraph DOM[Dominio - Kotlin puro]
      MOD[model]
      REPI[repository: interfaz]
      UC[usecase]
    end
    subgraph D[Datos]
      IMPL[DepositoRepositoryImpl]
      MAP[mapper]
      ROOM[local: Room DAO y entidades]
    end

    UI --> VM
    VM --> UC
    VM --> REPI
    IMPL -. implementa .-> REPI
    IMPL --> UC
    IMPL --> MAP
    IMPL --> ROOM
    UC --> MOD
```

Estructura de carpetas del código:

```
app/src/main/java/com/example/aguardapp/
├── AguardApplication.kt · MainActivity.kt · App.kt
├── core/
│   ├── data/Usuario.kt            (entidad y DAO del usuario local)
│   ├── db/AguardAppDatabase.kt    (Room)
│   ├── di/AppModule.kt            (Koin)
│   ├── navigation/                (Rutas, AppNavHost, InicioViewModel)
│   ├── ui/theme/                  (colores, tema, tipografía)
│   └── util/Reloj.kt              (hora actual y UUID)
└── feature/
    ├── bienvenida/presentation/   (BienvenidaScreen, BienvenidaViewModel)
    └── deposito/
        ├── domain/   model/ · repository/ · usecase/
        ├── data/     DepositoRepositoryImpl · local/ · mapper/
        └── presentation/  *Screen + *ViewModel · FormatoDeposito · componentes/
```

### 3.3.2. Diagrama de arquitectura del sistema / componentes

```mermaid
flowchart LR
    subgraph DEVICE[Teléfono Android]
      subgraph APK[AguardApp]
        UI[Jetpack Compose + Navigation]
        VM[ViewModels]
        KOIN[Koin]
        DOMAIN[Dominio]
        REPO[Repositorio]
      end
      LOCAL[(SQLite: aguardapp.db)]
    end

    UI --> VM
    VM --> DOMAIN
    VM --> REPO
    KOIN -. inyecta .-> REPO
    REPO <--> LOCAL
```

> La aplicación no se conecta a ningún servicio externo.

## 3.4. Vista de procesos

### 3.4.1. Diagrama de actividad del sistema

```mermaid
flowchart TD
    A([Inicio]) --> B[Iniciar Room y Koin; crear el usuario local si no existe]
    B --> C{¿Bienvenida completada?}
    C -- No --> D[Mostrar bienvenida]
    D --> E
    C -- Sí --> E{¿Hogar configurado?}
    E -- No --> F[Configurar hogar]
    F --> G
    E -- Sí --> G[Mostrar Mi depósito]
    G --> H[Leer perfil, llenados y novedades desde Room]
    H --> I[Estimar consumo y calcular nivel, agotamiento y déficit]
    I --> J[Actualizar la interfaz]
    J --> K{¿Acción del usuario?}
    K -- Registrar llenado --> L[Guardar llenado]
    K -- Sin agua --> M[Guardar novedad y nuevo consumo]
    K -- Qué recortar --> N[Mostrar recomendaciones]
    K -- Ninguna, pasa un minuto --> H
    L --> H
    M --> H
    N --> G
```

Las operaciones con la base de datos se ejecutan en corrutinas fuera del hilo principal; las pantallas observan `StateFlow` y se recomponen solo cuando cambia su estado.

## 3.5. Vista de despliegue — vista física

### 3.5.1. Diagrama de despliegue

```mermaid
flowchart TB
    subgraph PHONE[Dispositivo Android 7.0+]
      subgraph PROC[Proceso de la app com.example.aguardapp]
        APP[AguardApp APK]
      end
      DB[(Almacenamiento interno: aguardapp.db)]
      APP <--> DB
    end
```

La aplicación se distribuye como un único APK. No requiere servidor, cuenta de usuario ni conexión a internet.

---

# 4. Atributos de calidad del software

## 4.1. Escenario de funcionalidad

Los atributos de calidad (QA) son propiedades medibles que indican en qué grado el sistema satisface las necesidades de sus interesados. AguardApp entrega de forma completa las funciones del módulo Depósito: configurar el hogar, registrar llenados, consultar el nivel y el déficit, ver qué recortar y declarar que se quedó sin agua.

## 4.2. Escenario de usabilidad

Un usuario sin experiencia configura su hogar y registra un llenado en menos de un minuto: los controles son botones grandes, el formulario muestra el error exacto debajo de cada campo y el botón Guardar solo se habilita cuando todo es válido. El diseño usa colores sólidos y una sola fuente para facilitar la lectura.

## 4.3. Escenario de confiabilidad

Sin conexión, la aplicación funciona igual porque no depende de la red. Las validaciones del dominio y del repositorio impiden datos incoherentes (llenados en el futuro, litros mayores que la capacidad, agotamientos anteriores al último llenado), y el tope de ±30 % evita que un error del usuario desordene la estimación del consumo.

## 4.4. Escenario de rendimiento

Todas las consultas se resuelven en la base local; los cálculos (nivel, agotamiento, déficit) son operaciones simples en memoria. La pantalla Mi depósito se refresca cada minuto sin bloquear la interfaz.

## 4.5. Escenario de mantenibilidad

El dominio es Kotlin puro y no depende de Android, por lo que puede probarse con pruebas unitarias simples. Cada pantalla tiene solo dos archivos (Screen y ViewModel), y la inyección de dependencias está en un único módulo de Koin, lo que facilita agregar nuevas funcionalidades.

## 4.6. Otros escenarios

**Privacidad:** la aplicación no declara permisos en el `AndroidManifest.xml` y ningún dato sale del dispositivo, en línea con la privacidad por diseño de la Ley N.° 29733.
