package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.horasEntre
import kotlin.math.roundToInt

class CalcularDeficit {

    fun proximoLlenado(hora: LocalTime, ahora: LocalDateTime): LocalDateTime {
        val hoy = LocalDateTime(ahora.date, hora)
        return if (hoy > ahora) hoy else LocalDateTime(ahora.date.plus(1, DateTimeUnit.DAY), hora)
    }

    fun litrosQueFaltan(deposito: Deposito, proximoLlenado: LocalDateTime, ahora: LocalDateTime): Int {
        val horasHastaElLlenado = horasEntre(ahora, proximoLlenado).coerceAtLeast(0.0)
        val consumoHastaElLlenado = deposito.consumo.litrosPorHora * horasHastaElLlenado
        val nivelActual = deposito.nivelEn(ahora).litros.valor
        return (consumoHastaElLlenado - nivelActual).coerceAtLeast(0.0).roundToInt()
    }
}
