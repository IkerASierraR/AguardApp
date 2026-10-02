package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.CapacidadLitros
import com.example.aguardapp.feature.deposito.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo

internal object ParametrosConsumo {
    const val MAX_INTERVALOS = 5
    const val FACTOR_INTERVALO_LARGO = 2.0
    const val HORAS_ESTIMACION_INICIAL = 48.0
    const val TOPE_CAMBIO_POR_DECLARACION = 0.30

    const val HORAS_MINIMAS_ENTRE_LLENADOS = 6.0

    const val MINIMO_INFERIDOS = 2

    const val LITROS_BASE_POR_PERSONA = 60.0
    const val LITROS_POR_DUCHA = 30.0
    const val LITROS_LAVADORA = 100.0
    const val LITROS_RIEGO = 150.0
    const val HORAS_DE_USO_AL_DIA = 12.0
}

class EstimarConsumo {

    fun consumo(intervalos: List<IntervaloConsumo>, capacidad: CapacidadLitros, porHabitos: ConsumoHorario? = null): ConsumoHorario {
        val validos = seleccionar(intervalos)
        return when {
            alcanzanParaEstimar(validos) -> ConsumoHorario(validos.map { it.consumoHorario.litrosPorHora }.mediana())
            porHabitos != null -> porHabitos
            else -> ConsumoHorario(capacidad.litros.valor / ParametrosConsumo.HORAS_ESTIMACION_INICIAL)
        }
    }

    fun porHabitos(habitos: HabitosDelHogar, habitantes: Habitantes): ConsumoHorario {
        val porPersona = ParametrosConsumo.LITROS_BASE_POR_PERSONA + habitos.duchasPorDia * ParametrosConsumo.LITROS_POR_DUCHA
        val lavadora = if (habitos.usaLavadora) ParametrosConsumo.LITROS_LAVADORA else 0.0
        val riego = if (habitos.riegaJardin) ParametrosConsumo.LITROS_RIEGO else 0.0
        val diario = habitantes.cantidad * porPersona + lavadora + riego
        return ConsumoHorario(diario / ParametrosConsumo.HORAS_DE_USO_AL_DIA)
    }

    fun litrosPorHabitanteDia(intervalos: List<IntervaloConsumo>, habitantes: Habitantes): Double? {
        val validos = seleccionar(intervalos)
        if (!alcanzanParaEstimar(validos)) return null
        return validos.map { it.litrosPorDia }.mediana() / habitantes.cantidad
    }

    fun intervalosEntreLlenados(llenados: List<EventoLlenado>, capacidad: CapacidadLitros): List<IntervaloConsumo> =
        llenados
            .sortedBy { it.momento }
            .zipWithNext()
            .filter { (antes, despues) -> despues.momento > antes.momento }
            .map { (antes, despues) ->
                IntervaloConsumo(antes.momento, despues.momento, minOf(antes.litros, capacidad.litros), ClaseIntervalo.POR_LLENADO)
            }

    private fun seleccionar(intervalos: List<IntervaloConsumo>): List<IntervaloConsumo> {
        val recientes = intervalos.filterNot(::esRelleno).sortedBy { it.fin }.takeLast(ParametrosConsumo.MAX_INTERVALOS)
        val sinOlvidos = descartarOlvidos(recientes)
        val observados = sinOlvidos.filter { it.clase == ClaseIntervalo.OBSERVADO }
        return observados.ifEmpty { sinOlvidos }
    }

    private fun alcanzanParaEstimar(validos: List<IntervaloConsumo>): Boolean =
        validos.any { it.clase == ClaseIntervalo.OBSERVADO } || validos.size >= ParametrosConsumo.MINIMO_INFERIDOS

    private fun esRelleno(intervalo: IntervaloConsumo): Boolean =
        intervalo.clase == ClaseIntervalo.POR_LLENADO && intervalo.horas < ParametrosConsumo.HORAS_MINIMAS_ENTRE_LLENADOS

    private fun descartarOlvidos(intervalos: List<IntervaloConsumo>): List<IntervaloConsumo> {
        if (intervalos.isEmpty()) return intervalos
        val limite = intervalos.map { it.horas }.mediana() * ParametrosConsumo.FACTOR_INTERVALO_LARGO
        return intervalos.filter { it.horas <= limite }
    }
}

internal fun List<Double>.mediana(): Double {
    require(isNotEmpty()) { "No hay valores para calcular la mediana" }
    val orden = sorted()
    val medio = orden.size / 2
    return if (orden.size % 2 == 1) orden[medio] else (orden[medio - 1] + orden[medio]) / 2
}
