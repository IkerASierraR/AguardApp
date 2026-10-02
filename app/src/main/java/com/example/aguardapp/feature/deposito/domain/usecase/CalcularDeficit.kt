package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.horasEntre
import kotlin.math.roundToInt

class CalcularDeficit {

    /**
     * Litros que faltarán antes del próximo llenado:
     * lo que se consume hasta entonces menos lo que queda ahora. Nunca es negativo.
     */
    operator fun invoke(deposito: Deposito, proximoLlenado: LocalDateTime, ahora: LocalDateTime): Int {
        val horasHastaElLlenado = horasEntre(ahora, proximoLlenado).coerceAtLeast(0.0)
        val consumoHastaElLlenado = deposito.consumo.litrosPorHora * horasHastaElLlenado
        val nivelActual = deposito.nivelEn(ahora).litros.valor
        return (consumoHastaElLlenado - nivelActual).coerceAtLeast(0.0).roundToInt()
    }
}
