package com.example.aguardapp.feature.bienvenida.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import com.example.aguardapp.feature.bienvenida.domain.RegistroDeBienvenida

/** Lo que decide la entrada: si ya pasó la bienvenida y si aceptó guardar sus datos en el teléfono. */
data class BienvenidaUiState(
    val cargando: Boolean = true,
    val completada: Boolean = false,
    val consentimiento: Boolean = true
)

class BienvenidaViewModel(private val registro: RegistroDeBienvenida) : ViewModel() {
    private val _uiState = MutableStateFlow(BienvenidaUiState())
    val uiState: StateFlow<BienvenidaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            registro.observarCompletada().collect { completada -> _uiState.update { it.copy(cargando = false, completada = completada) } }
        }
    }

    fun alternarConsentimiento() = _uiState.update { it.copy(consentimiento = !it.consentimiento) }

    fun empezar() {
        if (!_uiState.value.consentimiento) return
        viewModelScope.launch { registro.completar() }
    }

    companion object {
        fun desdeInyeccion(): BienvenidaViewModel = BienvenidaViewModel(KoinPlatform.getKoin().get())
    }
}
