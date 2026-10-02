package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo
import com.example.aguardapp.feature.deposito.domain.model.Deposito

/** Lo que cambia cuando el usuario declara que se quedó sin agua. */
data class ResultadoSinAgua(val deposito: Deposito, val intervaloObservado: IntervaloConsumo)

class DeclararSinAgua(private val estimar: EstimarConsumo = EstimarConsumo()) {

    /** El depósito queda vacío en `momento`: el tanque se vació en un tiempo conocido y eso es un intervalo `OBSERVADO`. */
    operator fun invoke(
        deposito: Deposito,
        intervalos: List<IntervaloConsumo>,
        momento: LocalDateTime
    ): ResultadoSinAgua {
        require(momento > deposito.llenado.momento) { "Solo se puede quedar sin agua después del último llenado" }
        val observado = observadoHasta(deposito, momento)
        val consumo = recalcular(deposito, intervalos + observado)
        return ResultadoSinAgua(deposito.copy(consumo = consumo, agotadoEn = momento), observado)
    }

    private fun observadoHasta(deposito: Deposito, momento: LocalDateTime): IntervaloConsumo =
        IntervaloConsumo(
            inicio = deposito.llenado.momento,
            fin = momento,
            litrosConsumidos = deposito.nivelTrasLlenado.litros,
            clase = ClaseIntervalo.OBSERVADO
        )

    // Una sola declaración no mueve la estimación más allá del tope: un error de dedo no la desordena.
    private fun recalcular(deposito: Deposito, intervalos: List<IntervaloConsumo>): ConsumoHorario {
        val nuevo = estimar(intervalos, deposito.capacidad).consumo.litrosPorHora
        val anterior = deposito.consumo.litrosPorHora
        val minimo = anterior * (1 - ParametrosConsumo.TOPE_CAMBIO_POR_DECLARACION)
        val maximo = anterior * (1 + ParametrosConsumo.TOPE_CAMBIO_POR_DECLARACION)
        return ConsumoHorario(nuevo.coerceIn(minimo, maximo))
    }
}
