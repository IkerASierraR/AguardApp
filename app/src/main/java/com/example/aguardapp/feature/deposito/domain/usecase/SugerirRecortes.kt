package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.Recomendacion

class SugerirRecortes {

    /** Las recomendaciones que corresponden al hogar, de la que más ahorra a la que menos. */
    operator fun invoke(habitos: HabitosDelHogar): List<Recomendacion> =
        Recomendacion.entries
            .filter { it.correspondeA(habitos) }
            .sortedByDescending { it.litrosQueAhorra }
}
