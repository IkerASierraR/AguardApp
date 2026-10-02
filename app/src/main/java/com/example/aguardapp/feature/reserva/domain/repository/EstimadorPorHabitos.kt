package com.example.aguardapp.feature.reserva.domain.repository

import com.example.aguardapp.feature.reserva.domain.model.ConsumoHorario
import com.example.aguardapp.feature.reserva.domain.model.Habitantes
import com.example.aguardapp.feature.reserva.domain.model.HabitosDelHogar

/** Contrato con feature/recibo: estima el consumo del hogar a partir de sus hábitos declarados. */
interface EstimadorPorHabitos {
    fun estimar(habitos: HabitosDelHogar, habitantes: Habitantes): ConsumoHorario?
}
