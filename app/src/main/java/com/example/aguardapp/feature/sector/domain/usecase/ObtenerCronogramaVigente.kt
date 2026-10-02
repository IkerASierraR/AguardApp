package com.example.aguardapp.feature.sector.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.sector.domain.model.Cronograma

class ObtenerCronogramaVigente {

    fun obtener(cronogramas: List<Cronograma>, ahora: LocalDateTime): Cronograma? =
        cronogramas.firstOrNull { it.contiene(ahora) }
}
