package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalTime

enum class TipoReservorio { TANQUE_ELEVADO, CISTERNA, BIDONES }

data class HabitosDelHogar(
    val duchasPorDia: Int,
    val usaLavadora: Boolean,
    val riegaJardin: Boolean
) {
    init {
        require(duchasPorDia >= 0) { "Las duchas por día no pueden ser negativas: $duchasPorDia" }
    }
}

/** Lo que el usuario elige en "Configurar hogar". */
data class ConfiguracionHogar(
    val tipoReservorio: TipoReservorio,
    val capacidad: CapacidadLitros,
    val habitantes: Habitantes,
    val habitos: HabitosDelHogar,
    /** La hora a la que suele llegar el agua cada día. */
    val horaProximoLlenado: LocalTime
)

/** El hogar guardado: su configuración más el consumo que la app calculó. */
data class PerfilHogar(
    val usuarioId: String,
    val configuracion: ConfiguracionHogar,
    /** El consumo estimado a partir de los hábitos. */
    val consumoPorHabitos: ConsumoHorario? = null,
    /** El consumo aprendido cuando el usuario declaró que se quedó sin agua. */
    val consumoVigente: ConsumoHorario? = null
) {
    val capacidad: CapacidadLitros get() = configuracion.capacidad
    val habitantes: Habitantes get() = configuracion.habitantes
}
