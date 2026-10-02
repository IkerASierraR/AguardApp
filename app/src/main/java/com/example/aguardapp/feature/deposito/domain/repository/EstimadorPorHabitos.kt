package com.example.aguardapp.feature.deposito.domain.repository

import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar

/** Estima el consumo del hogar a partir de sus hábitos declarados. */
interface EstimadorPorHabitos {
    fun estimar(habitos: HabitosDelHogar, habitantes: Habitantes): ConsumoHorario?
}
