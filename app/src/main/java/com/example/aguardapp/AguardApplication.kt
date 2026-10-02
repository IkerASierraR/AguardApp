package com.example.aguardapp

import android.app.Application
import com.example.aguardapp.core.di.iniciarAplicacion

class AguardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        iniciarAplicacion(this)
    }
}
