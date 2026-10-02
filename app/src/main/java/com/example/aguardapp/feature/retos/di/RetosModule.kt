package com.example.aguardapp.feature.retos.di

import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module
import com.example.aguardapp.core.di.QUALIFICADOR_USUARIO
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.retos.data.RetosRepositoryImpl
import com.example.aguardapp.feature.retos.data.ReporteRepositoryImpl
import com.example.aguardapp.feature.retos.data.sync.NubeReportesSupabase
import com.example.aguardapp.feature.retos.data.sync.NubeRetosSupabase
import com.example.aguardapp.feature.retos.domain.repository.ReporteRepository
import com.example.aguardapp.feature.retos.domain.repository.RetosRepository

val moduloRetos = module {
    single<RetosRepository> {
        val reloj = get<Reloj>()
        RetosRepositoryImpl(
            dao = get(), usuarioId = get(QUALIFICADOR_USUARIO),
            nube = NubeRetosSupabase(get<SupabaseClient>()), hoy = { reloj.ahora().date }
        )
    }
    single<ReporteRepository> {
        ReporteRepositoryImpl(get(), get(QUALIFICADOR_USUARIO), NubeReportesSupabase(get<SupabaseClient>()))
    }
}
