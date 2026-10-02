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

/** Cómo se llenó el tanque; `ruta` es el texto que viaja en la ruta "registrar_llenado/{tipo}". */
enum class TipoLlenado(val ruta: String) {
    COMPLETO("completo"),
    PARCIAL("parcial");

    companion object {
        /** Convierte el texto de la ruta en el tipo; si no se reconoce, se toma como completo. */
        fun desdeRuta(texto: String?): TipoLlenado = entries.firstOrNull { it.ruta == texto } ?: COMPLETO
    }
}

data class RegistrarLlenadoUiState(
    val tipo: TipoLlenado = TipoLlenado.COMPLETO,
    val capacidadLitros: Int? = null,
    /** Lo que el usuario escribe en el campo de litros (solo en el llenado parcial). */
    val litrosTexto: String = "",
    /** Error debajo del campo de litros; `null` si está bien. */
    val errorLitros: String? = null,
    /** Error al guardar (por ejemplo, si aún no configuró su hogar). */
    val error: String? = null,
    val guardando: Boolean = false,
    /** `true` cuando ya se guardó y la pantalla puede cerrarse. */
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

    // Lee la capacidad del tanque: es el total del llenado completo y el límite del parcial.
    private fun cargarCapacidad() {
        viewModelScope.launch {
            val perfil = repositorio.observarPerfil().first()
            _uiState.update { it.copy(capacidadLitros = perfil?.capacidad?.litros?.valor?.roundToInt()) }
        }
    }

    /** El usuario escribe los litros: solo se aceptan dígitos y se valida al momento. */
    fun onLitrosChange(texto: String) {
        val soloDigitos = texto.filter { it.isDigit() }.take(MAXIMO_DIGITOS)
        _uiState.update { it.copy(litrosTexto = soloDigitos, errorLitros = validarLitros(soloDigitos, it.capacidadLitros)) }
    }

    /** Guarda el llenado: el completo usa la capacidad; el parcial, los litros escritos. */
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

    // Reglas del campo de litros: obligatorio, mayor que 0 y sin pasar la capacidad del tanque.
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
