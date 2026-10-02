package com.example.aguardapp.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aguardapp.core.data.UsuarioDao
import com.example.aguardapp.core.data.UsuarioEntity
import com.example.aguardapp.feature.deposito.data.local.DepositoDao
import com.example.aguardapp.feature.deposito.data.local.EventoLlenadoEntity
import com.example.aguardapp.feature.deposito.data.local.NovedadDepositoEntity
import com.example.aguardapp.feature.deposito.data.local.PerfilHogarEntity

private const val NOMBRE_BASE_DE_DATOS = "aguardapp.db"

/**
 * La base de datos local de la app (SQLite con Room).
 * Cada vez que cambien las tablas hay que subir `version`; sin migraciones, Room borra la base vieja y crea la nueva.
 */
@Database(
    entities = [
        UsuarioEntity::class,
        PerfilHogarEntity::class,
        EventoLlenadoEntity::class,
        NovedadDepositoEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AguardAppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun depositoDao(): DepositoDao

    companion object {
        fun crear(context: Context): AguardAppDatabase =
            Room.databaseBuilder(context.applicationContext, AguardAppDatabase::class.java, NOMBRE_BASE_DE_DATOS)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
