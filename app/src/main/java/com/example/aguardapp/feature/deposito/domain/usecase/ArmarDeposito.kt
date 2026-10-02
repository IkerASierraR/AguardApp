package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.DatosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo

/** Todo lo que el hogar ha registrado, sin interpretar. */
data class HistorialDeposito(
    val llenados: List<EventoLlenado> = emptyList(),
    val observados: List<IntervaloConsumo> = emptyList(),
    val agotadoEn: LocalDateTime? = null,
    val consumoVigente: ConsumoHorario? = null
) {
    fun intervalos(hogar: DatosDelHogar): List<IntervaloConsumo> =
        ConstruirIntervalos()(llenados, hogar.capacidad) + observados
}

class ArmarDeposito(private val estimar: EstimarConsumo = EstimarConsumo()) {

    /** `null` mientras el hogar no haya registrado ningún llenado. */
    operator fun invoke(hogar: DatosDelHogar, historial: HistorialDeposito, ahora: LocalDateTime): Deposito? {
        val llenado = historial.llenados.filter { it.momento <= ahora }.maxByOrNull { it.momento } ?: return null
        val consumo = historial.consumoVigente
            ?: estimar(historial.intervalos(hogar), hogar.capacidad, hogar.consumoPorHabitos).consumo
        val vaciadoDespues = historial.agotadoEn?.takeIf { it > llenado.momento }
        return Deposito(hogar.capacidad, consumo, llenado, vaciadoDespues)
    }
}
