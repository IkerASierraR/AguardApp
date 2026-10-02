package com.example.aguardapp.feature.deposito.data.mapper

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import com.example.aguardapp.feature.deposito.data.local.EventoLlenadoEntity
import com.example.aguardapp.feature.deposito.data.local.NovedadDepositoEntity
import com.example.aguardapp.feature.deposito.data.local.PerfilHogarEntity
import com.example.aguardapp.feature.deposito.domain.model.CapacidadLitros
import com.example.aguardapp.feature.deposito.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo
import com.example.aguardapp.feature.deposito.domain.model.Litros
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.TipoReservorio
import com.example.aguardapp.feature.deposito.domain.usecase.HistorialDeposito

// Conversiones entre las tablas de Room y los modelos del dominio.

fun PerfilHogar.aEntidad() = PerfilHogarEntity(
    usuarioId = usuarioId,
    tipoReservorio = configuracion.tipoReservorio.name,
    capacidadLitros = configuracion.capacidad.litros.valor,
    habitantes = configuracion.habitantes.cantidad,
    duchasPorDia = configuracion.habitos.duchasPorDia,
    usaLavadora = configuracion.habitos.usaLavadora,
    riegaJardin = configuracion.habitos.riegaJardin,
    horaProximoLlenado = configuracion.horaProximoLlenado.toString(),
    consumoPorHabitosLitrosHora = consumoPorHabitos?.litrosPorHora,
    consumoVigenteLitrosHora = consumoVigente?.litrosPorHora
)

fun PerfilHogarEntity.aDominio() = PerfilHogar(
    usuarioId = usuarioId,
    configuracion = ConfiguracionHogar(
        tipoReservorio = TipoReservorio.valueOf(tipoReservorio),
        capacidad = CapacidadLitros.deLitros(capacidadLitros),
        habitantes = Habitantes(habitantes),
        habitos = HabitosDelHogar(duchasPorDia, usaLavadora, riegaJardin),
        horaProximoLlenado = LocalTime.parse(horaProximoLlenado)
    ),
    consumoPorHabitos = consumoPorHabitosLitrosHora?.let(::ConsumoHorario),
    consumoVigente = consumoVigenteLitrosHora?.let(::ConsumoHorario)
)

fun EventoLlenado.aEntidad(id: String, usuarioId: String) =
    EventoLlenadoEntity(id, usuarioId, momento.toString(), litros.valor)

fun EventoLlenadoEntity.aDominio() = EventoLlenado(LocalDateTime.parse(momento), Litros(litros))

/** Junta los llenados y las veces que se quedó sin agua en el historial del depósito. */
fun aHistorial(llenados: List<EventoLlenadoEntity>, novedades: List<NovedadDepositoEntity>) = HistorialDeposito(
    llenados = llenados.map { it.aDominio() },
    observados = novedades.map {
        IntervaloConsumo(LocalDateTime.parse(it.inicioObservado), LocalDateTime.parse(it.momento), Litros(it.litrosObservados), ClaseIntervalo.OBSERVADO)
    },
    agotadoEn = novedades.maxOfOrNull { LocalDateTime.parse(it.momento) }
)

fun IntervaloConsumo.comoNovedad(id: String, usuarioId: String, momento: LocalDateTime) = NovedadDepositoEntity(
    id = id,
    usuarioId = usuarioId,
    momento = momento.toString(),
    inicioObservado = inicio.toString(),
    litrosObservados = litrosConsumidos.valor
)
