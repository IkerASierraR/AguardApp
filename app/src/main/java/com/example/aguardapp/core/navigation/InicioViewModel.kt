package com.example.aguardapp.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.data.UsuarioDao
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository

/** Por dónde empieza la app; `rutaInicial` es `null` mientras se lee la base de datos. */
data class InicioUiState(
    val rutaInicial: String? = null,
    val hayConfiguracion: Boolean = false
)

/** Decide la primera pantalla: la bienvenida, configurar el hogar o directamente Mi depósito. */
class InicioViewModel(
    private val usuarioDao: UsuarioDao,
    private val repositorio: DepositoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val bienvenidaCompletada = usuarioDao.observarBienvenidaCompletada().first() == true
            val hayConfiguracion = repositorio.observarPerfil().first() != null
            _uiState.value = InicioUiState(rutaInicial(bienvenidaCompletada, hayConfiguracion), hayConfiguracion)
        }
    }

    private fun rutaInicial(bienvenidaCompletada: Boolean, hayConfiguracion: Boolean): String = when {
        !bienvenidaCompletada -> Rutas.BIENVENIDA
        !hayConfiguracion -> Rutas.CONFIGURAR_HOGAR
        else -> Rutas.MI_DEPOSITO
    }

    companion object {
        fun desdeInyeccion(): InicioViewModel {
            val koin = KoinPlatform.getKoin()
            return InicioViewModel(koin.get(), koin.get())
        }
    }
}
