package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar

/** Todo lo que el hogar ha registrado, sin interpretar. */
data class HistorialDeposito(
    val llenados: List<EventoLlenado> = emptyList(),
    /** Los intervalos que terminaron con "me quedé sin agua". */
    val observados: List<IntervaloConsumo> = emptyList(),
    val agotadoEn: LocalDateTime? = null
) {
    /** Todos los intervalos de consumo: los de entre llenados más los observados. */
    fun intervalos(perfil: PerfilHogar): List<IntervaloConsumo> =
        EstimarConsumo().intervalosEntreLlenados(llenados, perfil.capacidad) + observados
}

/** Arma el depósito vigente a partir del perfil y del historial. */
class ArmarDeposito(private val estimar: EstimarConsumo = EstimarConsumo()) {

    /** `null` mientras el hogar no haya registrado ningún llenado. */
    operator fun invoke(perfil: PerfilHogar, historial: HistorialDeposito, ahora: LocalDateTime): Deposito? {
        val llenado = historial.llenados.filter { it.momento <= ahora }.maxByOrNull { it.momento } ?: return null
        val consumo = perfil.consumoVigente
            ?: estimar.consumo(historial.intervalos(perfil), perfil.capacidad, perfil.consumoPorHabitos)
        val vaciadoDespues = historial.agotadoEn?.takeIf { it > llenado.momento }
        return Deposito(perfil.capacidad, consumo, llenado, vaciadoDespues)
    }
}

/** Lo que cambia cuando el usuario declara que se quedó sin agua. */
data class ResultadoSinAgua(val deposito: Deposito, val intervaloObservado: IntervaloConsumo)

class DeclararSinAgua(private val estimar: EstimarConsumo = EstimarConsumo()) {

    /** El depósito queda vacío en `momento`: el tanque se vació en un tiempo conocido y la app aprende de eso. */
    operator fun invoke(deposito: Deposito, intervalos: List<IntervaloConsumo>, momento: LocalDateTime): ResultadoSinAgua {
        require(momento > deposito.llenado.momento) { "Solo se puede quedar sin agua después del último llenado" }
        val observado = IntervaloConsumo(deposito.llenado.momento, momento, deposito.nivelTrasLlenado.litros, ClaseIntervalo.OBSERVADO)
        val consumo = recalcular(deposito, intervalos + observado)
        return ResultadoSinAgua(deposito.copy(consumo = consumo, agotadoEn = momento), observado)
    }

    // Una sola declaración no mueve la estimación más allá del tope: un error de dedo no la desordena.
    private fun recalcular(deposito: Deposito, intervalos: List<IntervaloConsumo>): ConsumoHorario {
        val nuevo = estimar.consumo(intervalos, deposito.capacidad).litrosPorHora
        val anterior = deposito.consumo.litrosPorHora
        val minimo = anterior * (1 - ParametrosConsumo.TOPE_CAMBIO_POR_DECLARACION)
        val maximo = anterior * (1 + ParametrosConsumo.TOPE_CAMBIO_POR_DECLARACION)
        return ConsumoHorario(nuevo.coerceIn(minimo, maximo))
    }
}
