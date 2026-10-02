package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.repository.RegistroDeAvisos

private const val MILIS_POR_MINUTO = 60_000L

// El nivel baja con el tiempo aunque nada cambie en la base, así que la vista se refresca cada minuto.
private fun cadaMinuto(): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        delay(MILIS_POR_MINUTO)
    }
}

/** Orquesta: escucha el depósito, pide al dominio lo que hace falta y publica un único estado. */
class DepositoViewModel(
    private val repositorio: DepositoRepository,
    private val avisos: RegistroDeAvisos,
    private val ahora: () -> LocalDateTime,
    reloj: Flow<Unit> = cadaMinuto()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DepositoUiState())
    val uiState: StateFlow<DepositoUiState> = _uiState.asStateFlow()

    private val construirVista = ConstruirVistaDeposito()

    init {
        viewModelScope.launch {
            combine(repositorio.observarPerfil(), repositorio.observarDeposito(), avisos.observar(), reloj) { perfil, deposito, guardados, _ ->
                Triple(perfil, deposito, guardados.count { !it.leido })
            }.collect { (perfil, deposito, sinLeer) -> publicar(perfil, deposito, sinLeer) }
        }
    }

    fun alEvento(evento: DepositoEvent) {
        viewModelScope.launch {
            when (evento) {
                is DepositoEvent.RegistrarLlenado -> repositorio.registrarLlenado(ahora(), evento.tipo)
                    .onFailure { falla -> _uiState.update { it.copy(error = falla.message) } }
                DepositoEvent.DescartarError -> _uiState.update { it.copy(error = null) }
            }
        }
    }

    private suspend fun publicar(perfil: PerfilHogar?, deposito: Deposito?, avisosSinLeer: Int) {
        val vista = if (perfil == null || deposito == null) {
            null
        } else {
            val hogar = ContextoDelHogar(perfil.tipoReservorio, perfil.habitantes.cantidad)
            construirVista(deposito, hogar, repositorio.litrosPorHabitanteDia(), ahora())
        }
        _uiState.update { it.copy(cargando = false, hogarConfigurado = perfil != null, avisosSinLeer = avisosSinLeer, vista = vista) }
    }

    companion object {
        fun desdeInyeccion(): DepositoViewModel {
            val koin = KoinPlatform.getKoin()
            return DepositoViewModel(koin.get(), koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
