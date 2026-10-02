package com.example.aguardapp.feature.sector.domain.repository

import kotlinx.datetime.LocalDate
import com.example.aguardapp.feature.sector.domain.model.ConfirmacionHorario
import com.example.aguardapp.feature.sector.domain.model.Coordenada
import com.example.aguardapp.feature.sector.domain.model.Cronograma
import com.example.aguardapp.feature.sector.domain.model.PuntoCisterna
import com.example.aguardapp.feature.sector.domain.model.Sector

interface SectorRepository {
    suspend fun obtenerSectores(): List<Sector>
    suspend fun obtenerCronogramas(sectorId: String): List<Cronograma>
    suspend fun obtenerPuntosCisterna(sectorId: String): List<PuntoCisterna>
    suspend fun obtenerConfirmaciones(sectorId: String, fecha: LocalDate): List<ConfirmacionHorario>
    suspend fun registrarConfirmacion(confirmacion: ConfirmacionHorario)

    /** Guarda la casa del usuario solo en el dispositivo; al servidor nunca sube la coordenada. */
    suspend fun guardarUbicacionCasa(ubicacion: Coordenada)
    suspend fun obtenerUbicacionCasa(): Coordenada?
}
