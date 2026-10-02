package com.example.aguardapp.feature.retos.domain.repository

import com.example.aguardapp.feature.retos.domain.model.Reporte

interface ReporteRepository {
    suspend fun guardar(reporte: Reporte)
    suspend fun obtenerPendientes(): List<Reporte>
}
