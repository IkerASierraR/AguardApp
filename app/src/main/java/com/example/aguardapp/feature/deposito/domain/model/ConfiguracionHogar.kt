package com.example.aguardapp.feature.deposito.domain.model

/** Lo que el usuario elige en la configuración inicial, sin lo que se deriva de ello. */
data class ConfiguracionHogar(
    val tipoReservorio: TipoReservorio,
    val capacidad: CapacidadLitros,
    val habitantes: Habitantes,
    val habitos: HabitosDelHogar
)
