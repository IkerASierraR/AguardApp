package com.example.aguardapp.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aguardapp.core.data.local.UsuarioDao
import com.example.aguardapp.core.data.local.UsuarioEntity
import com.example.aguardapp.feature.recibo.data.local.ReciboBorradorDao
import com.example.aguardapp.feature.recibo.data.local.ReciboBorradorEntity
import com.example.aguardapp.feature.recibo.data.local.ReciboDao
import com.example.aguardapp.feature.recibo.data.local.ReciboEntity
import com.example.aguardapp.feature.reserva.data.local.EventoLlenadoEntity
import com.example.aguardapp.feature.reserva.data.local.AvisoReservaDao
import com.example.aguardapp.feature.reserva.data.local.AvisoReservaEntity
import com.example.aguardapp.feature.reserva.data.local.NovedadReservaEntity
import com.example.aguardapp.feature.reserva.data.local.PerfilHogarEntity
import com.example.aguardapp.feature.reserva.data.local.ReservaDao
import com.example.aguardapp.feature.retos.data.local.RetoEntity
import com.example.aguardapp.feature.retos.data.local.RetoUsuarioEntity
import com.example.aguardapp.feature.retos.data.local.ReporteEntity
import com.example.aguardapp.feature.retos.data.local.RetosDao
import com.example.aguardapp.feature.sector.data.local.ConfirmacionHorarioEntity
import com.example.aguardapp.feature.sector.data.local.CronogramaEntity
import com.example.aguardapp.feature.sector.data.local.DomicilioEntity
import com.example.aguardapp.feature.sector.data.local.PuntoCisternaEntity
import com.example.aguardapp.feature.sector.data.local.SectorDao
import com.example.aguardapp.feature.sector.data.local.SectorEntity

const val NOMBRE_BASE_DE_DATOS = "aguardapp.db"

@Database(
    entities = [
        UsuarioEntity::class,
        PerfilHogarEntity::class,
        EventoLlenadoEntity::class,
        NovedadReservaEntity::class,
        AvisoReservaEntity::class,
        SectorEntity::class,
        CronogramaEntity::class,
        PuntoCisternaEntity::class,
        ConfirmacionHorarioEntity::class,
        DomicilioEntity::class,
        ReciboEntity::class,
        ReciboBorradorEntity::class,
        RetoEntity::class,
        RetoUsuarioEntity::class,
        ReporteEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AguardAppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun reservaDao(): ReservaDao
    abstract fun reciboDao(): ReciboDao
    abstract fun reciboBorradorDao(): ReciboBorradorDao
    abstract fun avisoReservaDao(): AvisoReservaDao
    abstract fun sectorDao(): SectorDao
    abstract fun retosDao(): RetosDao
}

