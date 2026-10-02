package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

data class EventoLlenado(
    val momento: LocalDateTime,
    val tipo: TipoLlenado
)
