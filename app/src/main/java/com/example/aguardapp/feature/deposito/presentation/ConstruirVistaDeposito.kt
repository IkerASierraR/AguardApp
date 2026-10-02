package com.example.aguardapp.feature.deposito.presentation

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.LitrosPorHabitanteDia
import com.example.aguardapp.feature.deposito.domain.model.TipoReservorio
import com.example.aguardapp.feature.deposito.domain.model.masHoras
import kotlin.math.roundToInt

/** Lo que la cabecera dice del hogar: cuántas personas viven y con qué tipo de reservorio cuenta. */
data class ContextoDelHogar(val tipo: TipoReservorio, val habitantes: Int)

/** Convierte el depósito del dominio en lo que muestra la pantalla; el ViewModel solo lo invoca. */
class ConstruirVistaDeposito {

    operator fun invoke(
        deposito: Deposito,
        hogar: ContextoDelHogar,
        litrosPorHabitanteDia: LitrosPorHabitanteDia?,
        ahora: LocalDateTime
    ): DepositoVista {
        val nivel = deposito.nivelEn(ahora)
        return DepositoVista(
            saludo = saludoPara(ahora.hour),
            subtituloHogar = if (hogar.habitantes == 1) "1 persona" else "${hogar.habitantes} personas",
            nivelLitros = nivel.litros.valor.roundToInt(),
            capacidadLitros = deposito.capacidad.litros.valor.roundToInt(),
            porcentaje = nivel.porcentaje.roundToInt(),
            textoLlenado = "${nombreDelTipo(hogar.tipo)} · último llenado ${describirMomento(deposito.llenado.momento, ahora)}",
            textoAgotamiento = describirMomento(deposito.agotamientoProyectado(), ahora),
            textoAlcanzaHastaSiLlena = alcanzaHastaSiLlena(deposito, ahora),
            consumoLitrosPorHora = deposito.consumo.litrosPorHora.roundToInt(),
            litrosPorHabitanteDia = litrosPorHabitanteDia?.valor?.roundToInt()
        )
    }

    private fun alcanzaHastaSiLlena(deposito: Deposito, ahora: LocalDateTime): String {
        val horasQueDuraria = deposito.capacidad.litros.valor / deposito.consumo.litrosPorHora
        return describirMomento(ahora.masHoras(horasQueDuraria), ahora)
    }
}

fun saludoPara(hora: Int): String = when {
    hora < 12 -> "Buenos días"
    hora < 19 -> "Buenas tardes"
    else -> "Buenas noches"
}

fun nombreDelTipo(tipo: TipoReservorio): String = when (tipo) {
    TipoReservorio.TANQUE_ELEVADO -> "Tanque elevado"
    TipoReservorio.CISTERNA -> "Cisterna"
    TipoReservorio.BIDONES -> "Bidones"
}
