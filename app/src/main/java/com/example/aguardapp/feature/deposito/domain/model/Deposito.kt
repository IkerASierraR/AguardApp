package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

/** Un llenado registrado por el usuario: cuándo fue y con cuántos litros quedó el tanque. */
data class EventoLlenado(
    val momento: LocalDateTime,
    val litros: Litros
)

/** Cuánta agua hay en el tanque en un momento dado. */
data class NivelDeposito(
    val litros: Litros,
    val capacidad: CapacidadLitros
) {
    init {
        require(litros <= capacidad.litros) { "El nivel no puede superar la capacidad del reservorio" }
    }

    val porcentaje: Double get() = litros / capacidad.litros * 100
}

/**
 * El agua de un hogar desde su último llenado registrado.
 * Si el usuario declaró que se quedó sin agua, `agotadoEn` manda sobre la proyección.
 */
data class Deposito(
    val capacidad: CapacidadLitros,
    val consumo: ConsumoHorario,
    val llenado: EventoLlenado,
    val agotadoEn: LocalDateTime? = null
) {
    /** Lo que quedó en el tanque al llenarlo; nunca más que su capacidad. */
    val nivelTrasLlenado: NivelDeposito
        get() = NivelDeposito(minOf(llenado.litros, capacidad.litros), capacidad)

    /** El nivel baja de forma lineal y nunca es menor que cero. */
    fun nivelEn(momento: LocalDateTime): NivelDeposito {
        if (agotadoEn != null && momento >= agotadoEn) return NivelDeposito(Litros.CERO, capacidad)
        val horas = horasEntre(llenado.momento, momento).coerceAtLeast(0.0)
        val consumido = Litros(consumo.litrosPorHora * horas)
        return NivelDeposito(nivelTrasLlenado.litros - consumido, capacidad)
    }

    /** Cuándo se acabará el agua con el consumo actual. */
    fun agotamientoProyectado(): LocalDateTime {
        agotadoEn?.let { return it }
        val horasQueDura = nivelTrasLlenado.litros.valor / consumo.litrosPorHora
        return llenado.momento.masHoras(horasQueDura)
    }
}

/** Qué cambiaría si el usuario declarara que se quedó sin agua en `momento`, sin guardar nada todavía. */
data class PrevisualizacionSinAgua(
    val agotamientoProyectado: LocalDateTime,
    val momento: LocalDateTime,
    val consumoActual: ConsumoHorario,
    val consumoNuevo: ConsumoHorario
) {
    /** Horas que el agua duró menos de lo previsto; negativo si duró más. */
    val horasAntesDeLoPrevisto: Double get() = horasEntre(momento, agotamientoProyectado)
}
