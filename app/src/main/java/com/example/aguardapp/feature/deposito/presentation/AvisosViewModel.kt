package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.deposito.domain.repository.RegistroDeAvisos

class AvisosViewModel(
    private val registro: RegistroDeAvisos,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvisosUiState())
    val uiState: StateFlow<AvisosUiState> = _uiState.asStateFlow()

    private val construirVista = ConstruirVistaAvisos()

    init {
        viewModelScope.launch {
            registro.observar().collect { guardados ->
                _uiState.value = AvisosUiState(cargando = false, avisos = construirVista(guardados, ahora()))
            }
        }
    }

    /** Se llama al salir de la pantalla: lo que se vio deja de contar como nuevo. */
    fun marcarComoLeidos() {
        viewModelScope.launch { registro.marcarTodosComoLeidos() }
    }

    companion object {
        fun desdeInyeccion(): AvisosViewModel {
            val koin = KoinPlatform.getKoin()
            return AvisosViewModel(koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
