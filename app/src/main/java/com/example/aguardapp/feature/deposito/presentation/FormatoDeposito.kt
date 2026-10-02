package com.example.aguardapp.feature.deposito.presentation

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import com.example.aguardapp.feature.deposito.domain.model.TipoReservorio
import com.example.aguardapp.feature.deposito.domain.model.masHoras
import kotlin.math.roundToInt

private const val SEGUNDOS_DE_REDONDEO = 30.0

fun formatearHora(hora: LocalTime): String {
    val hora12 = if (hora.hour % 12 == 0) 12 else hora.hour % 12
    val sufijo = if (hora.hour < 12) "a.m." else "p.m."
    return "$hora12:${hora.minute.toString().padStart(2, '0')} $sufijo"
}

fun formatearDuracion(horas: Double): String {
    val minutos = (horas * 60).roundToInt()
    val enteras = minutos / 60
    val resto = minutos % 60
    return when {
        enteras == 0 -> "$resto min"
        resto == 0 -> "$enteras h"
        else -> "$enteras h $resto min"
    }
}

fun describirMomento(momento: LocalDateTime, ahora: LocalDateTime): String {
    val redondeado = momento.masHoras(SEGUNDOS_DE_REDONDEO / 3600)
    val fecha = redondeado.date
    val dia = when (fecha) {
        ahora.date -> "hoy"
        ahora.date.plus(1, DateTimeUnit.DAY) -> "mañana"
        else -> "el ${fecha.day}/${fecha.month.number}"
    }
    return "$dia ${formatearHora(redondeado.time)}"
}

fun formatearMiles(valor: Int): String =
    valor.toString().reversed().chunked(3).joinToString(" ").reversed()

fun describirHora(momento: LocalDateTime, referencia: LocalDateTime): String {
    val redondeado = momento.masHoras(SEGUNDOS_DE_REDONDEO / 3600)
    return if (redondeado.date == referencia.date) formatearHora(redondeado.time) else describirMomento(momento, referencia)
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
