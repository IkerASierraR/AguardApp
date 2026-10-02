package com.example.aguardapp.core.db

import android.content.Context
import androidx.room.Room

fun crearBaseDeDatos(context: Context): AguardAppDatabase =
    Room.databaseBuilder(context.applicationContext, AguardAppDatabase::class.java, NOMBRE_BASE_DE_DATOS)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
