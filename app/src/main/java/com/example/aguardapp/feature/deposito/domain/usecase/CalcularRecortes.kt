package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.PlanRecortes
import com.example.aguardapp.feature.deposito.domain.model.Recomendacion
import com.example.aguardapp.feature.deposito.domain.model.horasEntre
import kotlin.math.roundToInt

data class RecorteSugerido(val recomendacion: Recomendacion, val litros: Int)

data class SituacionRecortes(
    val deficitLitros: Int,
    val sugeridos: List<RecorteSugerido>,
    val elegidas: Set<Recomendacion>
) {
    val litrosGanados: Int get() = sugeridos.filter { it.recomendacion in elegidas }.sumOf { it.litros }

    val litrosQueFaltan: Int get() = (deficitLitros - litrosGanados).coerceAtLeast(0)

    val tieneRecortes: Boolean get() = elegidas.isNotEmpty()
}

class CalcularRecortes(private val calcularDeficit: CalcularDeficit = CalcularDeficit()) {

    fun evaluar(perfil: PerfilHogar, deposito: Deposito, plan: PlanRecortes?, ahora: LocalDateTime): SituacionRecortes {
        val proximoLlenado = calcularDeficit.proximoLlenado(perfil.configuracion.horaProximoLlenado, ahora)
        val sugeridos = sugerir(perfil.configuracion.habitos, perfil.habitantes, horasEntre(ahora, proximoLlenado))
        val disponibles = sugeridos.map { it.recomendacion }.toSet()
        val elegidas = plan?.takeIf { it.vigentePara(deposito, ahora) }?.elegidas.orEmpty() intersect disponibles
        return SituacionRecortes(calcularDeficit.litrosQueFaltan(deposito, proximoLlenado, ahora), sugeridos, elegidas)
    }

    fun nuevoPlan(perfil: PerfilHogar, deposito: Deposito, elegidas: Set<Recomendacion>, ahora: LocalDateTime): PlanRecortes =
        PlanRecortes(
            llenado = deposito.llenado.momento,
            hasta = calcularDeficit.proximoLlenado(perfil.configuracion.horaProximoLlenado, ahora),
            elegidas = elegidas
        )

    fun sugerir(habitos: HabitosDelHogar, habitantes: Habitantes, horasHastaElLlenado: Double): List<RecorteSugerido> {
        val parteDelDia = (horasHastaElLlenado / ParametrosConsumo.HORAS_DE_USO_AL_DIA).coerceIn(0.0, 1.0)
        return Recomendacion.entries
            .mapNotNull { recomendacion ->
                val litros = (litrosAlDia(recomendacion, habitos, habitantes) * parteDelDia).roundToInt()
                if (litros > 0) RecorteSugerido(recomendacion, litros) else null
            }
            .sortedByDescending { it.litros }
    }

    private fun litrosAlDia(recomendacion: Recomendacion, habitos: HabitosDelHogar, habitantes: Habitantes): Double {
        val personas = habitantes.cantidad
        return when (recomendacion) {
            Recomendacion.NO_LAVAR_ROPA -> if (habitos.usaLavadora) ParametrosConsumo.LITROS_LAVADORA else 0.0
            Recomendacion.NO_REGAR -> if (habitos.riegaJardin) ParametrosConsumo.LITROS_RIEGO else 0.0
            Recomendacion.DUCHAS_CORTAS ->
                personas * habitos.duchasPorDia * ParametrosConsumo.LITROS_POR_DUCHA * ParametrosConsumo.AHORRO_DUCHA_CORTA
            Recomendacion.CERRAR_EL_CANO -> personas * ParametrosConsumo.AHORRO_PLATOS_POR_PERSONA
            Recomendacion.BALDE_EN_EL_BANO -> personas * ParametrosConsumo.AHORRO_INODORO_POR_PERSONA
        }
    }
}
