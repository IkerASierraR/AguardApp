package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

/** Un aviso ya emitido, tal como aparece en la lista de avisos. */
data class AvisoGuardado(
    val id: String,
    val aviso: Aviso,
    val momento: LocalDateTime,
    val leido: Boolean
)
