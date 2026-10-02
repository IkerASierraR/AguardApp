package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

/** El agua llega todos los días a la misma hora: hoy si aún no llegó, si no mañana. */
class CalcularProximoLlenado {

    operator fun invoke(hora: LocalTime, ahora: LocalDateTime): LocalDateTime {
        val hoy = LocalDateTime(ahora.date, hora)
        return if (hoy > ahora) hoy else LocalDateTime(ahora.date.plus(1, DateTimeUnit.DAY), hora)
    }
}
