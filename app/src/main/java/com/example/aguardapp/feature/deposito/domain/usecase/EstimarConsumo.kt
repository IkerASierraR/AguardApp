package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.CapacidadLitros
import com.example.aguardapp.feature.deposito.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo

// Valores iniciales; se ajustan con los hogares del piloto.
internal object ParametrosConsumo {
    const val MAX_INTERVALOS = 5
    const val FACTOR_INTERVALO_LARGO = 2.0
    const val HORAS_ESTIMACION_INICIAL = 48.0
    const val TOPE_CAMBIO_POR_DECLARACION = 0.30

    // Dos llenados más cercanos que esto son una corrección o un relleno, no un tanque gastado.
    const val HORAS_MINIMAS_ENTRE_LLENADOS = 6.0

    // Un intervalo inferido es solo una cota: hacen falta dos para fiarse de ellos.
    const val MINIMO_INFERIDOS = 2

    // Litros de referencia para estimar el consumo por hábitos (órdenes de magnitud, no una fuente).
    const val LITROS_BASE_POR_PERSONA = 60.0
    const val LITROS_POR_DUCHA = 30.0
    const val LITROS_LAVADORA = 100.0
    const val LITROS_RIEGO = 150.0
    const val HORAS_DE_USO_AL_DIA = 12.0
}

/** Calcula cuánta agua gasta el hogar: con su historial si alcanza, si no con sus hábitos. */
class EstimarConsumo {

    /** El consumo del hogar: primero el historial, luego los hábitos y, si no hay nada, una estimación inicial. */
    fun consumo(intervalos: List<IntervaloConsumo>, capacidad: CapacidadLitros, porHabitos: ConsumoHorario? = null): ConsumoHorario {
        val validos = seleccionar(intervalos)
        return when {
            alcanzanParaEstimar(validos) -> ConsumoHorario(validos.map { it.consumoHorario.litrosPorHora }.mediana())
            porHabitos != null -> porHabitos
            else -> ConsumoHorario(capacidad.litros.valor / ParametrosConsumo.HORAS_ESTIMACION_INICIAL)
        }
    }

    /** Estima el consumo a partir de lo que el usuario declara de su hogar. */
    fun porHabitos(habitos: HabitosDelHogar, habitantes: Habitantes): ConsumoHorario {
        val porPersona = ParametrosConsumo.LITROS_BASE_POR_PERSONA + habitos.duchasPorDia * ParametrosConsumo.LITROS_POR_DUCHA
        val lavadora = if (habitos.usaLavadora) ParametrosConsumo.LITROS_LAVADORA else 0.0
        val riego = if (habitos.riegaJardin) ParametrosConsumo.LITROS_RIEGO else 0.0
        val diario = habitantes.cantidad * porPersona + lavadora + riego
        return ConsumoHorario(diario / ParametrosConsumo.HORAS_DE_USO_AL_DIA)
    }

    /** Litros por persona al día; `null` mientras no haya datos suficientes (los mismos que exige el consumo). */
    fun litrosPorHabitanteDia(intervalos: List<IntervaloConsumo>, habitantes: Habitantes): Double? {
        val validos = seleccionar(intervalos)
        if (!alcanzanParaEstimar(validos)) return null
        return validos.map { it.litrosPorDia }.mediana() / habitantes.cantidad
    }

    /** Convierte cada par de llenados consecutivos en un intervalo de consumo. */
    fun intervalosEntreLlenados(llenados: List<EventoLlenado>, capacidad: CapacidadLitros): List<IntervaloConsumo> =
        llenados
            .sortedBy { it.momento }
            .zipWithNext()
            .filter { (antes, despues) -> despues.momento > antes.momento }
            .map { (antes, despues) ->
                IntervaloConsumo(antes.momento, despues.momento, minOf(antes.litros, capacidad.litros), ClaseIntervalo.POR_LLENADO)
            }

    // Elige los intervalos que sirven: los recientes, sin rellenos ni llenados olvidados; los observados mandan.
    private fun seleccionar(intervalos: List<IntervaloConsumo>): List<IntervaloConsumo> {
        val recientes = intervalos.filterNot(::esRelleno).sortedBy { it.fin }.takeLast(ParametrosConsumo.MAX_INTERVALOS)
        val sinOlvidos = descartarOlvidos(recientes)
        val observados = sinOlvidos.filter { it.clase == ClaseIntervalo.OBSERVADO }
        return observados.ifEmpty { sinOlvidos }
    }

    // Un intervalo observado es dato exacto y alcanza solo; los inferidos necesitan dos.
    private fun alcanzanParaEstimar(validos: List<IntervaloConsumo>): Boolean =
        validos.any { it.clase == ClaseIntervalo.OBSERVADO } || validos.size >= ParametrosConsumo.MINIMO_INFERIDOS

    // Entre dos llenados muy seguidos no se gastó el tanque: contarlo dispararía el consumo.
    private fun esRelleno(intervalo: IntervaloConsumo): Boolean =
        intervalo.clase == ClaseIntervalo.POR_LLENADO && intervalo.horas < ParametrosConsumo.HORAS_MINIMAS_ENTRE_LLENADOS

    // Un intervalo mucho más largo que lo normal es un llenado que no se registró.
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
