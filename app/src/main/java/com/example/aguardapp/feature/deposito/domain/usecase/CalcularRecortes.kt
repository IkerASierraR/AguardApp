package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.Recomendacion

class CalcularRecortes {

    fun sugerir(habitos: HabitosDelHogar): List<Recomendacion> =
        Recomendacion.entries
            .filter { it.correspondeA(habitos) }
            .sortedByDescending { it.litrosQueAhorra }

    fun litrosGanados(elegidas: Set<Recomendacion>): Int = elegidas.sumOf { it.litrosQueAhorra }

    fun litrosQueFaltan(deficitLitros: Int, elegidas: Set<Recomendacion>): Int =
        (deficitLitros - litrosGanados(elegidas)).coerceAtLeast(0)
}
