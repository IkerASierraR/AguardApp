package com.example.aguardapp.feature.bienvenida.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.sesion.InicioConGoogle
import com.example.aguardapp.core.sesion.ModoDeAcceso
import com.example.aguardapp.core.sesion.RegistroDeAcceso
import com.example.aguardapp.core.sesion.ResultadoInicio
import com.example.aguardapp.feature.deposito.data.sync.SincronizadorDeposito

/** Lo que decide la entrada: el modo de acceso, el consentimiento y el aviso de error. */
data class AccesoUiState(
    val cargando: Boolean = true,
    val modo: ModoDeAcceso? = null,
    val consentimiento: Boolean = true,
    val enCurso: Boolean = false,
    val mensaje: String? = null
) {
    val puedeEntrar: Boolean get() = consentimiento && !enCurso
}

class AccesoViewModel(
    private val registro: RegistroDeAcceso,
    private val google: InicioConGoogle,
    // Trae de la nube lo que la cuenta ya tenía (el hogar y sus registros) antes de dejar pasar a la app.
    private val alEntrarConCuenta: suspend () -> Unit = {}
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccesoUiState())
    val uiState: StateFlow<AccesoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            registro.observar().collect { modo -> _uiState.update { it.copy(cargando = false, modo = modo) } }
        }
    }

    fun alternarConsentimiento() = _uiState.update { it.copy(consentimiento = !it.consentimiento) }

    fun descartarMensaje() = _uiState.update { it.copy(mensaje = null) }

    fun empezarSinCuenta() {
        if (!_uiState.value.puedeEntrar) return
        viewModelScope.launch { registro.guardar(ModoDeAcceso.SIN_CUENTA) }
    }

    fun entrarConGoogle() {
        if (!_uiState.value.puedeEntrar) return
        _uiState.update { it.copy(enCurso = true, mensaje = null) }
        viewModelScope.launch {
            val resultado = google.iniciar()
            if (resultado is ResultadoInicio.Exitoso) {
                alEntrarConCuenta()
                registro.guardar(ModoDeAcceso.GOOGLE)
            }
            _uiState.update { it.copy(enCurso = false, mensaje = mensajeDe(resultado)) }
        }
    }

    companion object {
        fun mensajeDe(resultado: ResultadoInicio): String? = when (resultado) {
            ResultadoInicio.Exitoso, ResultadoInicio.Cancelado -> null
            ResultadoInicio.SinCuentas -> "No hay ninguna cuenta de Google en este teléfono. Agrega una o empieza sin cuenta."
            is ResultadoInicio.Fallido -> "No pudimos entrar con Google. Inténtalo de nuevo o empieza sin cuenta."
        }

        fun desdeInyeccion(): AccesoViewModel {
            val koin = KoinPlatform.getKoin()
            val sincronizador = koin.get<SincronizadorDeposito>()
            return AccesoViewModel(
                registro = koin.get(),
                google = koin.get(),
                alEntrarConCuenta = { sincronizador.sincronizar() }
            )
        }
    }
}
