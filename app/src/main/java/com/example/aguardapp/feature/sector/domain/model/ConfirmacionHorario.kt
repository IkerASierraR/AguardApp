package com.example.aguardapp.feature.sector.domain.model

import kotlinx.datetime.LocalDateTime

enum class TipoConfirmacion { LLEGADA, CORTE }

data class ConfirmacionHorario(
    val id: String,
    val sectorId: String,
    val usuarioId: String,
    val momento: LocalDateTime,
    val tipo: TipoConfirmacion
)
