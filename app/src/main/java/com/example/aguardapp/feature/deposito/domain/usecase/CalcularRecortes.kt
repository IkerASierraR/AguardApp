package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.Recomendacion

/** Qué puede recortar el hogar y cuánto ahorra con lo que elige. */
class CalcularRecortes {

    /** Las recomendaciones que corresponden al hogar, de la que más ahorra a la que menos. */
    fun sugerir(habitos: HabitosDelHogar): List<Recomendacion> =
        Recomendacion.entries
            .filter { it.correspondeA(habitos) }
            .sortedByDescending { it.litrosQueAhorra }

    /** Litros que se ganan con lo elegido. */
    fun litrosGanados(elegidas: Set<Recomendacion>): Int = elegidas.sumOf { it.litrosQueAhorra }

    /** Litros que siguen faltando después de los recortes; nunca negativo. */
    fun litrosQueFaltan(deficitLitros: Int, elegidas: Set<Recomendacion>): Int =
        (deficitLitros - litrosGanados(elegidas)).coerceAtLeast(0)
}
