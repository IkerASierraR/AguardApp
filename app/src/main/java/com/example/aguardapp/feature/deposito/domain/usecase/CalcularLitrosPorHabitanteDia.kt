package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo
import com.example.aguardapp.feature.deposito.domain.model.LitrosPorHabitanteDia

class CalcularLitrosPorHabitanteDia(
    private val seleccionar: SeleccionarIntervalos = SeleccionarIntervalos()
) {
    /** `null` mientras no haya los mismos datos que exige el consumo estimado: así los dos indicadores no se contradicen. */
    operator fun invoke(intervalos: List<IntervaloConsumo>, habitantes: Habitantes): LitrosPorHabitanteDia? {
        val validos = seleccionar(intervalos)
        if (!seleccionar.alcanzanParaEstimar(validos)) return null
        val consumoDiario = validos.map { it.litrosPorDia }.mediana()
        return LitrosPorHabitanteDia(consumoDiario / habitantes.cantidad)
    }
}
