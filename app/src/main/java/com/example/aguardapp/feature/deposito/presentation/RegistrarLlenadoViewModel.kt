package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.deposito.domain.model.Litros
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import kotlin.math.roundToInt
import org.koin.mp.KoinPlatform

enum class TipoLlenado(val ruta: String) {
    COMPLETO("completo"),
    PARCIAL("parcial");

    companion object {
        fun desdeRuta(texto: String?): TipoLlenado = entries.firstOrNull { it.ruta == texto } ?: COMPLETO
    }
}

data class RegistrarLlenadoUiState(
    val tipo: TipoLlenado = TipoLlenado.COMPLETO,
    val capacidadLitros: Int? = null,
    val litrosTexto: String = "",
    val errorLitros: String? = null,
    val error: String? = null,
    val guardando: Boolean = false,
    val guardado: Boolean = false
)

class RegistrarLlenadoViewModel(
    private val repositorio: DepositoRepository,
    private val ahora: () -> LocalDateTime,
    tipo: TipoLlenado
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrarLlenadoUiState(tipo = tipo))
    val uiState: StateFlow<RegistrarLlenadoUiState> = _uiState.asStateFlow()

    init {
        cargarCapacidad()
    }

    private fun cargarCapacidad() {
        viewModelScope.launch {
            val perfil = repositorio.observarPerfil().first()
            _uiState.update { it.copy(capacidadLitros = perfil?.capacidad?.litros?.valor?.roundToInt()) }
        }
    }

    fun onLitrosChange(texto: String) {
        val soloDigitos = texto.filter { it.isDigit() }.take(MAXIMO_DIGITOS)
        _uiState.update { it.copy(litrosTexto = soloDigitos, errorLitros = validarLitros(soloDigitos, it.capacidadLitros)) }
    }

    fun onGuardar() {
        val estado = _uiState.value
        val capacidad = estado.capacidadLitros ?: return
        if (estado.tipo == TipoLlenado.COMPLETO) {
            guardar(capacidad)
            return
        }
        val error = validarLitros(estado.litrosTexto, capacidad)
        if (error != null) {
            _uiState.update { it.copy(errorLitros = error) }
            return
        }
        guardar(estado.litrosTexto.toInt())
    }

    private fun guardar(litros: Int) {
        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val resultado = repositorio.registrarLlenado(ahora(), Litros(litros.toDouble()))
            _uiState.update {
                it.copy(guardando = false, guardado = resultado.isSuccess, error = resultado.exceptionOrNull()?.message)
            }
        }
    }

    private fun validarLitros(texto: String, capacidad: Int?): String? {
        val litros = texto.toIntOrNull() ?: return "Escribe cuántos litros entraron"
        if (litros <= 0) return "Los litros deben ser mayores que 0"
        if (capacidad != null && litros > capacidad) return "No puede superar la capacidad de tu tanque ($capacidad L)"
        return null
    }

    companion object {
        private const val MAXIMO_DIGITOS = 5

        fun desdeInyeccion(tipo: TipoLlenado): RegistrarLlenadoViewModel {
            val koin = KoinPlatform.getKoin()
            return RegistrarLlenadoViewModel(koin.get(), koin.get<Reloj>()::ahora, tipo)
        }
    }
}
