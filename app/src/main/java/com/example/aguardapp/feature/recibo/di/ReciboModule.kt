package com.example.aguardapp.feature.recibo.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.map
import org.koin.dsl.module
import com.example.aguardapp.feature.recibo.data.BorradorReciboStore
import com.example.aguardapp.feature.recibo.data.ReciboRepositoryRoom
import com.example.aguardapp.feature.recibo.data.sync.NubeReciboSupabase
import com.example.aguardapp.feature.recibo.data.sync.SincronizadorRecibo
import com.example.aguardapp.feature.recibo.domain.port.ParserRecibo
import com.example.aguardapp.feature.recibo.domain.port.ReconocedorTexto
import com.example.aguardapp.feature.recibo.domain.repository.ReciboRepository
import com.example.aguardapp.feature.recibo.domain.usecase.ConfirmarReciboUseCase
import com.example.aguardapp.feature.recibo.domain.usecase.CorregirCampoUseCase
import com.example.aguardapp.feature.recibo.domain.usecase.EscanearReciboUseCase
import com.example.aguardapp.feature.recibo.domain.usecase.ObservarHistorialUseCase
import com.example.aguardapp.feature.recibo.domain.usecase.ObservarResumenUseCase
import com.example.aguardapp.feature.recibo.infrastructure.ocr.ParserReciboEpsTacna
import com.example.aguardapp.feature.recibo.infrastructure.ocr.ReconocedorTextoAndroid

// Los DAO vienen de la base de datos general (AguardAppDatabase), aportada por core/di.
val moduloRecibo = module {
    single<ReciboRepository> { ReciboRepositoryRoom(get()) }
    single { BorradorReciboStore(getOrNull()) }
    single<ParserRecibo> { ParserReciboEpsTacna }
    single<ReconocedorTexto> { ReconocedorTextoAndroid() }
    single {
        val supabase = get<SupabaseClient>()
        SincronizadorRecibo(
            dao = get(),
            nube = NubeReciboSupabase(supabase),
            haySesion = supabase.auth.sessionStatus.map { it is SessionStatus.Authenticated }
        )
    }

    factory { ObservarResumenUseCase(get()) }
    factory { ObservarHistorialUseCase(get()) }
    factory { ConfirmarReciboUseCase(get()) }
    factory { CorregirCampoUseCase() }
    factory { EscanearReciboUseCase(get(), get()) }
}
