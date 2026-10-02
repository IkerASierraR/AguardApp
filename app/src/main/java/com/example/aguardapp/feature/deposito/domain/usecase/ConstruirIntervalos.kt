package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.CapacidadLitros
import com.example.aguardapp.feature.deposito.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo
import com.example.aguardapp.feature.deposito.domain.model.NivelDeposito

/** Convierte cada par de llenados reales consecutivos en un intervalo de consumo. */
class ConstruirIntervalos {

    operator fun invoke(eventos: List<EventoLlenado>, capacidad: CapacidadLitros): List<IntervaloConsumo> =
        eventos
            .sortedBy { it.momento }
            .zipWithNext()
            .filter { (antes, despues) -> despues.momento > antes.momento }
            .map { (antes, despues) ->
                IntervaloConsumo(
                    inicio = antes.momento,
                    fin = despues.momento,
                    litrosConsumidos = NivelDeposito.trasLlenado(capacidad, antes.tipo).litros,
                    clase = ClaseIntervalo.POR_LLENADO
                )
            }
}
