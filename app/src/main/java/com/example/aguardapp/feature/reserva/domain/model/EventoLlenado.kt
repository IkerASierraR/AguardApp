package com.example.aguardapp.feature.reserva.domain.model

import kotlinx.datetime.LocalDateTime

data class EventoLlenado(
    val momento: LocalDateTime,
    val tipo: TipoLlenado,
    val origen: OrigenLlenado = OrigenLlenado.REAL
)
