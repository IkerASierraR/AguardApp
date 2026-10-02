package com.example.aguardapp.feature.deposito.domain.model

/** Lo que el depósito necesita saber del hogar; se llena desde el perfil del hogar. */
data class DatosDelHogar(
    val capacidad: CapacidadLitros,
    val habitantes: Habitantes,
    val consumoPorHabitos: ConsumoHorario? = null
)
