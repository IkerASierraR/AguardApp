package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

/** Un llenado registrado por el usuario: cuándo fue y con cuántos litros quedó el tanque. */
data class EventoLlenado(
    val momento: LocalDateTime,
    val litros: Litros
)
