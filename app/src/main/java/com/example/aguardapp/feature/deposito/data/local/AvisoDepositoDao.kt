package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AvisoDepositoDao {

    @Query("SELECT COUNT(*) FROM aviso_deposito WHERE usuarioId = :usuarioId AND clave = :clave")
    suspend fun contar(usuarioId: String, clave: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardar(aviso: AvisoDepositoEntity)

    @Query("SELECT * FROM aviso_deposito WHERE usuarioId = :usuarioId ORDER BY momento DESC LIMIT 50")
    fun observar(usuarioId: String): Flow<List<AvisoDepositoEntity>>

    @Query("UPDATE aviso_deposito SET leido = 1 WHERE usuarioId = :usuarioId")
    suspend fun marcarTodosLeidos(usuarioId: String)
}
