package com.example.aguardapp.feature.deposito.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.LitrosPorHabitanteDia
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.PrevisualizacionSinAgua
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.TipoLlenado

interface DepositoRepository {

    /** El hogar configurado; emite `null` hasta que el usuario complete la configuración inicial. */
    fun observarPerfil(): Flow<PerfilHogar?>

    /** Guarda la configuración; al cambiarla se descarta el consumo aprendido, que ya no corresponde. */
    suspend fun guardarPerfil(configuracion: ConfiguracionHogar, consumoPorHabitos: ConsumoHorario?): Result<Unit>

    /** El depósito vigente; emite `null` mientras el hogar no tenga ningún llenado. */
    fun observarDeposito(): Flow<Deposito?>

    /** Promedio diario por persona; `null` si aún no hay datos suficientes. */
    suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia?

    suspend fun registrarLlenado(momento: LocalDateTime, tipo: TipoLlenado): Result<Unit>

    suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit>

    /** Lo que cambiaría al declarar que se quedó sin agua en `momento`, sin guardar nada. */
    suspend fun previsualizarSinAgua(momento: LocalDateTime): Result<PrevisualizacionSinAgua>
}
