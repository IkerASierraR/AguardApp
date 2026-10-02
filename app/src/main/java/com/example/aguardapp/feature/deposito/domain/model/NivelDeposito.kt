package com.example.aguardapp.feature.deposito.domain.model

data class NivelDeposito(
    val litros: Litros,
    val capacidad: CapacidadLitros
) {
    init {
        require(litros <= capacidad.litros) { "El nivel no puede superar la capacidad del reservorio" }
    }

    val porcentaje: Double get() = litros / capacidad.litros * 100
}
