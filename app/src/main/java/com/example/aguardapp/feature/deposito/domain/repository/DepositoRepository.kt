package com.example.aguardapp.feature.deposito.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.Litros
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.PrevisualizacionSinAgua

interface DepositoRepository {

    fun observarPerfil(): Flow<PerfilHogar?>

    suspend fun guardarPerfil(configuracion: ConfiguracionHogar): Result<Unit>

    fun observarDeposito(): Flow<Deposito?>

    suspend fun litrosPorHabitanteDia(): Double?

    suspend fun registrarLlenado(momento: LocalDateTime, litros: Litros): Result<Unit>

    suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit>

    suspend fun previsualizarSinAgua(momento: LocalDateTime): Result<PrevisualizacionSinAgua>
}
