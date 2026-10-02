package com.example.aguardapp.feature.reserva.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.reserva.domain.repository.ReservaRepository

class SinAguaViewModel(
    private val repositorio: ReservaRepository,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val _uiState = MutableStateFlow(SinAguaUiState())
    val uiState: StateFlow<SinAguaUiState> = _uiState.asStateFlow()

    private val construirVista = ConstruirVistaSinAgua()

    /** Se llama al abrir la pantalla: calcula qué cambiaría si se acabó el agua ahora. */
    fun cargar() {
        viewModelScope.launch {
            val previsualizacion = repositorio.previsualizarSinAgua(ahora())
            _uiState.value = SinAguaUiState(
                cargando = false,
                vista = previsualizacion.getOrNull()?.let(construirVista::invoke),
                error = previsualizacion.exceptionOrNull()?.message
            )
        }
    }

    fun alEvento(evento: SinAguaEvent) {
        when (evento) {
            SinAguaEvent.SeAcaboAhora -> declarar(ahora())
            is SinAguaEvent.SeAcaboAntes -> declarar(LocalDateTime(ahora().date, evento.hora))
            SinAguaEvent.DescartarError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun declarar(momento: LocalDateTime) {
        viewModelScope.launch {
            repositorio.declararSinAgua(momento)
                .onSuccess { _uiState.update { it.copy(listo = true) } }
                .onFailure { falla -> _uiState.update { it.copy(error = falla.message) } }
        }
    }

    companion object {
        fun desdeInyeccion(): SinAguaViewModel {
            val koin = KoinPlatform.getKoin()
            return SinAguaViewModel(koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
