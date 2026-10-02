package com.example.aguardapp.feature.bienvenida.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.data.UsuarioDao

data class BienvenidaUiState(
    /** Si aceptó que los datos se guarden en el teléfono; sin aceptar no puede empezar. */
    val consentimiento: Boolean = true,
    /** `true` cuando ya se guardó que pasó la bienvenida y se puede navegar. */
    val listo: Boolean = false
)

class BienvenidaViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _uiState = MutableStateFlow(BienvenidaUiState())
    val uiState: StateFlow<BienvenidaUiState> = _uiState.asStateFlow()

    fun onAlternarConsentimiento() = _uiState.update { it.copy(consentimiento = !it.consentimiento) }

    /** Guarda que ya pasó la bienvenida, para no volver a mostrarla. */
    fun onEmpezar() {
        if (!_uiState.value.consentimiento) return
        viewModelScope.launch {
            usuarioDao.completarBienvenida()
            _uiState.update { it.copy(listo = true) }
        }
    }

    companion object {
        fun desdeInyeccion(): BienvenidaViewModel = BienvenidaViewModel(KoinPlatform.getKoin().get())
    }
}
