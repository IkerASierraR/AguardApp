package com.example.aguardapp.feature.deposito.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.Litros
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.PrevisualizacionSinAgua

/** Lo que la app puede leer y guardar del depósito. */
interface DepositoRepository {

    /** El hogar configurado; emite `null` hasta que el usuario complete la configuración. */
    fun observarPerfil(): Flow<PerfilHogar?>

    /** Guarda la configuración; al cambiarla se descarta el consumo aprendido, que ya no corresponde. */
    suspend fun guardarPerfil(configuracion: ConfiguracionHogar): Result<Unit>

    /** El depósito vigente; emite `null` mientras el hogar no tenga ningún llenado. */
    fun observarDeposito(): Flow<Deposito?>

    /** Promedio de litros por persona al día; `null` si aún no hay datos suficientes. */
    suspend fun litrosPorHabitanteDia(): Double?

    /** Guarda un llenado: el tanque quedó con `litros` en `momento`. */
    suspend fun registrarLlenado(momento: LocalDateTime, litros: Litros): Result<Unit>

    suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit>

    /** Lo que cambiaría al declarar que se quedó sin agua en `momento`, sin guardar nada. */
    suspend fun previsualizarSinAgua(momento: LocalDateTime): Result<PrevisualizacionSinAgua>
}
