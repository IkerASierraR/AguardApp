package com.example.aguardapp.feature.deposito.data.mapper

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.data.local.EventoLlenadoEntity
import com.example.aguardapp.feature.deposito.data.local.NovedadDepositoEntity
import com.example.aguardapp.feature.deposito.data.local.PerfilHogarEntity
import com.example.aguardapp.feature.deposito.domain.model.CapacidadLitros
import com.example.aguardapp.feature.deposito.domain.model.ClaseIntervalo
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.DatosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.IntervaloConsumo
import com.example.aguardapp.feature.deposito.domain.model.Litros
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.TipoLlenado
import com.example.aguardapp.feature.deposito.domain.model.TipoReservorio
import com.example.aguardapp.feature.deposito.domain.usecase.HistorialDeposito

fun PerfilHogar.aEntidad() = PerfilHogarEntity(
    usuarioId = usuarioId,
    tipoReservorio = tipoReservorio.name,
    capacidadLitros = capacidad.litros.valor,
    habitantes = habitantes.cantidad,
    duchasPorDia = habitos.duchasPorDia,
    usaLavadora = habitos.usaLavadora,
    riegaJardin = habitos.riegaJardin,
    consumoPorHabitosLitrosHora = consumoPorHabitos?.litrosPorHora,
    consumoVigenteLitrosHora = consumoVigente?.litrosPorHora
)

fun PerfilHogarEntity.aDominio() = PerfilHogar(
    usuarioId = usuarioId,
    tipoReservorio = TipoReservorio.valueOf(tipoReservorio),
    capacidad = CapacidadLitros.deLitros(capacidadLitros),
    habitantes = Habitantes(habitantes),
    habitos = HabitosDelHogar(duchasPorDia, usaLavadora, riegaJardin),
    consumoPorHabitos = consumoPorHabitosLitrosHora?.let(::ConsumoHorario),
    consumoVigente = consumoVigenteLitrosHora?.let(::ConsumoHorario)
)

fun EventoLlenado.aEntidad(id: String, usuarioId: String) =
    EventoLlenadoEntity(id, usuarioId, momento.toString(), tipo.name)

fun EventoLlenadoEntity.aDominio() = EventoLlenado(LocalDateTime.parse(momento), TipoLlenado.valueOf(tipo))

fun PerfilHogar.aDatosDelHogar() = DatosDelHogar(capacidad, habitantes, consumoPorHabitos)

fun aHistorial(
    perfil: PerfilHogar,
    llenados: List<EventoLlenadoEntity>,
    novedades: List<NovedadDepositoEntity>
): HistorialDeposito {
    val sinAgua = novedades.filter { it.tipo == NovedadDepositoEntity.SIN_AGUA }
    return HistorialDeposito(
        llenados = llenados.map { it.aDominio() },
        observados = sinAgua.mapNotNull { it.aIntervaloObservado() },
        agotadoEn = sinAgua.maxOfOrNull { LocalDateTime.parse(it.momento) },
        consumoVigente = perfil.consumoVigente
    )
}

private fun NovedadDepositoEntity.aIntervaloObservado(): IntervaloConsumo? {
    if (inicioObservado == null || litrosObservados == null) return null
    return IntervaloConsumo(
        LocalDateTime.parse(inicioObservado), LocalDateTime.parse(momento), Litros(litrosObservados), ClaseIntervalo.OBSERVADO
    )
}

fun IntervaloConsumo.comoNovedad(id: String, usuarioId: String, momento: LocalDateTime) = NovedadDepositoEntity(
    id = id,
    usuarioId = usuarioId,
    momento = momento.toString(),
    tipo = NovedadDepositoEntity.SIN_AGUA,
    inicioObservado = inicio.toString(),
    litrosObservados = litrosConsumidos.valor
)
