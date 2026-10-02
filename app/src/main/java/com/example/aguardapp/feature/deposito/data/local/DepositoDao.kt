package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositoDao {

    @Query("SELECT * FROM perfil_hogar WHERE usuarioId = :usuarioId")
    fun observarPerfil(usuarioId: String): Flow<PerfilHogarEntity?>

    @Upsert
    suspend fun guardarPerfil(perfil: PerfilHogarEntity)

    @Query("SELECT * FROM evento_llenado WHERE usuarioId = :usuarioId ORDER BY momento")
    fun observarLlenados(usuarioId: String): Flow<List<EventoLlenadoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarLlenado(llenado: EventoLlenadoEntity)

    @Query("SELECT * FROM novedad_deposito WHERE usuarioId = :usuarioId ORDER BY momento")
    fun observarNovedades(usuarioId: String): Flow<List<NovedadDepositoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarNovedad(novedad: NovedadDepositoEntity)

    @Query("SELECT * FROM plan_recortes WHERE usuarioId = :usuarioId")
    fun observarPlanRecortes(usuarioId: String): Flow<PlanRecortesEntity?>

    @Upsert
    suspend fun guardarPlanRecortes(plan: PlanRecortesEntity)
}
