package com.example.aguardapp.feature.sector.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import com.example.aguardapp.feature.sector.data.local.ConfirmacionHorarioEntity
import com.example.aguardapp.feature.sector.data.local.CronogramaEntity
import com.example.aguardapp.feature.sector.data.local.PuntoCisternaEntity
import com.example.aguardapp.feature.sector.data.local.SectorEntity
import com.example.aguardapp.feature.sector.domain.model.ConfirmacionHorario
import com.example.aguardapp.feature.sector.domain.model.Coordenada
import com.example.aguardapp.feature.sector.domain.model.Cronograma
import com.example.aguardapp.feature.sector.domain.model.EstadoCisterna
import com.example.aguardapp.feature.sector.domain.model.FuenteCronograma
import com.example.aguardapp.feature.sector.domain.model.PuntoCisterna
import com.example.aguardapp.feature.sector.domain.model.Sector
import com.example.aguardapp.feature.sector.domain.model.TipoConfirmacion
import com.example.aguardapp.feature.sector.domain.model.TipoCronograma

internal fun SectorEntity.aDominio() = Sector(id, nombre, distrito, Coordenada(latitud, longitud))

internal fun CronogramaEntity.aDominio() = Cronograma(
    id = id,
    sectorId = sectorId,
    fecha = LocalDate.parse(fecha),
    horaInicio = LocalTime.parse(horaInicio),
    horaFin = LocalTime.parse(horaFin),
    tipo = TipoCronograma.valueOf(tipo),
    fuente = FuenteCronograma.valueOf(fuente)
)

internal fun PuntoCisternaEntity.aDominio() = PuntoCisterna(
    id = id,
    sectorId = sectorId,
    nombre = nombre,
    ubicacion = Coordenada(latitud, longitud),
    horarioInicio = LocalTime.parse(horarioInicio),
    horarioFin = LocalTime.parse(horarioFin),
    estado = EstadoCisterna.valueOf(estado)
)

internal fun ConfirmacionHorarioEntity.aDominio() = ConfirmacionHorario(
    id = id,
    sectorId = sectorId,
    usuarioId = usuarioId,
    momento = LocalDateTime.parse(momento),
    tipo = TipoConfirmacion.valueOf(tipo)
)

internal fun ConfirmacionHorario.aEntidad() = ConfirmacionHorarioEntity(
    id = id,
    sectorId = sectorId,
    usuarioId = usuarioId,
    momento = momento.toString(),
    tipo = tipo.name
)
