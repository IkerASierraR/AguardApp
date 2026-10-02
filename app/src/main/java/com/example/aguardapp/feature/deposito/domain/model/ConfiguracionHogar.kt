package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalTime

/** Lo que el usuario elige en la configuración inicial, sin lo que se deriva de ello. */
data class ConfiguracionHogar(
    val tipoReservorio: TipoReservorio,
    val capacidad: CapacidadLitros,
    val habitantes: Habitantes,
    val habitos: HabitosDelHogar,
    /** La hora a la que suele llegar el agua cada día. */
    val horaProximoLlenado: LocalTime
)
