package com.example.aguardapp.feature.retos.domain.usecase

import com.example.aguardapp.feature.retos.domain.model.Reporte
import com.example.aguardapp.feature.retos.domain.repository.ReporteRepository

class CrearReporte(private val repositorio: ReporteRepository) {
    suspend fun ejecutar(reporte: Reporte) = repositorio.guardar(reporte)
}
