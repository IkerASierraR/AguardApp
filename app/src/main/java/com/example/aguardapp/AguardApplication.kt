package com.example.aguardapp

import android.app.Application
import com.example.aguardapp.core.di.iniciarAplicacion
import com.example.aguardapp.feature.reserva.infrastructure.RecalculoHorarioWorker

class AguardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        iniciarAplicacion(this)
        RecalculoHorarioWorker.programar(this)
    }
}
