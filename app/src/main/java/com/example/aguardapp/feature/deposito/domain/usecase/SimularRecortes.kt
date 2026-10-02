package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.Recomendacion

/** Cuánto se ahorra con lo elegido y cuánto falta todavía para cubrir el déficit. */
data class ResultadoRecortes(val litrosGanados: Int, val litrosQueFaltan: Int) {
    val cubreElDeficit: Boolean get() = litrosQueFaltan == 0
}

class SimularRecortes {

    operator fun invoke(deficitLitros: Int, elegidas: Set<Recomendacion>): ResultadoRecortes {
        val ganados = elegidas.sumOf { it.litrosQueAhorra }
        val faltan = (deficitLitros - ganados).coerceAtLeast(0)
        return ResultadoRecortes(ganados, faltan)
    }
}
