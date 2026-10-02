package com.example.aguardapp.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aguardapp.core.data.local.UsuarioDao
import com.example.aguardapp.core.data.local.UsuarioEntity
import com.example.aguardapp.feature.deposito.data.local.AvisoDepositoDao
import com.example.aguardapp.feature.deposito.data.local.AvisoDepositoEntity
import com.example.aguardapp.feature.deposito.data.local.DepositoDao
import com.example.aguardapp.feature.deposito.data.local.EventoLlenadoEntity
import com.example.aguardapp.feature.deposito.data.local.NovedadDepositoEntity
import com.example.aguardapp.feature.deposito.data.local.PerfilHogarEntity

const val NOMBRE_BASE_DE_DATOS = "aguardapp.db"

@Database(
    entities = [
        UsuarioEntity::class,
        PerfilHogarEntity::class,
        EventoLlenadoEntity::class,
        NovedadDepositoEntity::class,
        AvisoDepositoEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AguardAppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun depositoDao(): DepositoDao
    abstract fun avisoDepositoDao(): AvisoDepositoDao
}
