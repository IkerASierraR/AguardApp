package com.example.aguardapp.feature.retos.domain.usecase

import com.example.aguardapp.feature.retos.domain.model.LitrosPorHabitanteDia
import com.example.aguardapp.feature.retos.domain.model.PosicionSector
import com.example.aguardapp.feature.retos.domain.model.RetoUsuario
import com.example.aguardapp.feature.retos.domain.repository.RetosRepository

class ObtenerRetosSemana(private val repositorio: RetosRepository) {
    suspend fun ejecutar() = repositorio.obtenerRetosActivos()
}

class ActualizarRetoDelDia(private val repositorio: RetosRepository) {
    suspend fun ejecutar(retoId: String, cumplido: Boolean) {
        repositorio.guardarCumplimiento(RetoUsuario(retoId, repositorio.fechaActual(), cumplido))
    }
}

class CompararConSector(private val repositorio: RetosRepository) {
    suspend fun ejecutar(sectorId: String, consumo: LitrosPorHabitanteDia): PosicionSector? {
        val promedio = repositorio.obtenerPromedioSector(sectorId) ?: return null
        return PosicionSector(consumo, LitrosPorHabitanteDia(promedio))
    }
}
