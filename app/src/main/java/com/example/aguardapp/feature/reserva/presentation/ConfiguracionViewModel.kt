package com.example.aguardapp.feature.reserva.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import com.example.aguardapp.feature.reserva.domain.repository.EstimadorPorHabitos
import com.example.aguardapp.feature.reserva.domain.repository.ReservaRepository
import com.example.aguardapp.feature.reserva.domain.usecase.ConfigurarHogar

class ConfiguracionViewModel(
    private val repositorio: ReservaRepository,
    estimador: EstimadorPorHabitos
) : ViewModel() {

    private val actualizar = ActualizarConfiguracion(estimador)
    private val configurar = ConfigurarHogar(estimador, repositorio)

    private val _uiState = MutableStateFlow(actualizar.conEstimacion(ConfiguracionUiState()))
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    /** Se llama al abrir la pantalla: precarga el perfil guardado y limpia el resultado anterior. */
    fun cargar() {
        viewModelScope.launch {
            val perfil = repositorio.observarPerfil().first()
            val base = perfil?.let { ConfiguracionUiState.desde(it.configuracion) } ?: ConfiguracionUiState()
            _uiState.value = actualizar.conEstimacion(base)
        }
    }

    fun alEvento(evento: ConfiguracionEvent) {
        if (evento == ConfiguracionEvent.Continuar) guardar() else _uiState.update { actualizar(it, evento) }
    }

    private fun guardar() {
        if (_uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val resultado = configurar(_uiState.value.aConfiguracion())
            _uiState.update {
                it.copy(guardando = false, guardado = resultado.isSuccess, error = resultado.exceptionOrNull()?.message)
            }
        }
    }

    companion object {
        fun desdeInyeccion(): ConfiguracionViewModel {
            val koin = KoinPlatform.getKoin()
            return ConfiguracionViewModel(koin.get(), koin.get())
        }
    }
}
