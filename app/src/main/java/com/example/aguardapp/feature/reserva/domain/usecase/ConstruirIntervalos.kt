package com.example.aguardapp.feature.reserva.domain.usecase

import com.example.aguardapp.feature.reserva.domain.model.CapacidadLitros
import com.example.aguardapp.feature.reserva.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.reserva.domain.model.EventoLlenado
import com.example.aguardapp.feature.reserva.domain.model.IntervaloConsumo
import com.example.aguardapp.feature.reserva.domain.model.NivelReserva
import com.example.aguardapp.feature.reserva.domain.model.OrigenLlenado

/** Convierte cada par de llenados reales consecutivos en un intervalo de consumo. */
class ConstruirIntervalos {

    operator fun invoke(eventos: List<EventoLlenado>, capacidad: CapacidadLitros): List<IntervaloConsumo> =
        eventos
            .filter { it.origen == OrigenLlenado.REAL }
            .sortedBy { it.momento }
            .zipWithNext()
            .filter { (antes, despues) -> despues.momento > antes.momento }
            .map { (antes, despues) ->
                IntervaloConsumo(
                    inicio = antes.momento,
                    fin = despues.momento,
                    litrosConsumidos = NivelReserva.trasLlenado(capacidad, antes.tipo).litros,
                    clase = ClaseIntervalo.POR_LLENADO
                )
            }
}
