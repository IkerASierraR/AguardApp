package com.example.aguardapp.feature.sector.di

import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module
import com.example.aguardapp.feature.sector.data.SectorRepositoryImpl
import com.example.aguardapp.feature.sector.data.sync.NubeSectorSupabase
import com.example.aguardapp.feature.sector.domain.repository.SectorRepository

val moduloSector = module {
    single<SectorRepository> {
        SectorRepositoryImpl(dao = get(), nube = NubeSectorSupabase(get<SupabaseClient>()))
    }
}
