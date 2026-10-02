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

data class ConfiguracionHogar(
    val tipoReservorio: TipoReservorio,
    val capacidad: CapacidadLitros,
    val habitantes: Habitantes,
    val habitos: HabitosDelHogar,
    val horaProximoLlenado: LocalTime
)

data class PerfilHogar(
    val usuarioId: String,
    val configuracion: ConfiguracionHogar,
    val consumoPorHabitos: ConsumoHorario? = null,
    val consumoVigente: ConsumoHorario? = null
) {
    val capacidad: CapacidadLitros get() = configuracion.capacidad
    val habitantes: Habitantes get() = configuracion.habitantes
}
