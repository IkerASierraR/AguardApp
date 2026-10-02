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
import kotlinx.datetime.LocalTime
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.deposito.domain.model.PrevisualizacionSinAgua
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import kotlin.math.abs
import kotlin.math.roundToInt
import org.koin.mp.KoinPlatform

enum class OpcionSinAgua { AHORA, ANTES }

data class SinAguaUiState(
    val cargando: Boolean = true,
    val vista: SinAguaVista? = null,
    val opcion: OpcionSinAgua = OpcionSinAgua.AHORA,
    val horaTexto: String = "",
    val errorHora: String? = null,
    val error: String? = null,
    val listo: Boolean = false
)

data class SinAguaVista(
    val textoProyectado: String,
    val textoSeAcabo: String,
    val textoDiferencia: String,
    val seAcaboAntes: Boolean,
    val textoConsumo: String
)

private const val MINUTO_EN_HORAS = 1.0 / 60

private val FORMATO_HORA = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

class SinAguaViewModel(
    private val repositorio: DepositoRepository,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val _uiState = MutableStateFlow(SinAguaUiState())
    val uiState: StateFlow<SinAguaUiState> = _uiState.asStateFlow()

    private var ultimoLlenado: LocalDateTime? = null

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            ultimoLlenado = repositorio.observarDeposito().first()?.llenado?.momento
            val previsualizacion = repositorio.previsualizarSinAgua(ahora())
            _uiState.update {
                it.copy(
                    cargando = false,
                    vista = previsualizacion.getOrNull()?.let(::armarVista),
                    error = previsualizacion.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onOpcionChange(opcion: OpcionSinAgua) {
        _uiState.update { it.copy(opcion = opcion, errorHora = null) }
    }

    fun onHoraChange(texto: String) {
        val limpio = texto.filter { it.isDigit() || it == ':' }.take(5)
        _uiState.update { it.copy(horaTexto = limpio, errorHora = null) }
    }

    fun onDescartarError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onRegistrar() {
        val estado = _uiState.value
        if (estado.opcion == OpcionSinAgua.AHORA) {
            declarar(ahora())
            return
        }
        val error = validarHora(estado.horaTexto)
        if (error != null) {
            _uiState.update { it.copy(errorHora = error) }
            return
        }
        declarar(LocalDateTime(ahora().date, LocalTime.parse(estado.horaTexto)))
    }

    private fun validarHora(texto: String): String? {
        if (!FORMATO_HORA.matches(texto)) return "Escribe la hora en formato HH:mm (por ejemplo 14:30)"
        val momento = LocalDateTime(ahora().date, LocalTime.parse(texto))
        if (momento > ahora()) return "La hora no puede ser futura"
        val llenado = ultimoLlenado
        if (llenado != null && momento <= llenado) {
            return "Debe ser posterior a tu último llenado (${describirMomento(llenado, ahora())})"
        }
        return null
    }

    private fun declarar(momento: LocalDateTime) {
        viewModelScope.launch {
            repositorio.declararSinAgua(momento)
                .onSuccess { _uiState.update { it.copy(listo = true) } }
                .onFailure { falla -> _uiState.update { it.copy(error = falla.message) } }
        }
    }

    private fun armarVista(previsualizacion: PrevisualizacionSinAgua): SinAguaVista {
        val adelanto = previsualizacion.horasAntesDeLoPrevisto
        val antes = previsualizacion.consumoActual.litrosPorHora.roundToInt()
        val despues = previsualizacion.consumoNuevo.litrosPorHora.roundToInt()
        return SinAguaVista(
            textoProyectado = describirHora(previsualizacion.agotamientoProyectado, previsualizacion.momento),
            textoSeAcabo = describirHora(previsualizacion.momento, previsualizacion.momento),
            textoDiferencia = when {
                abs(adelanto) < MINUTO_EN_HORAS -> "justo a tiempo"
                adelanto > 0 -> "${formatearDuracion(adelanto)} antes"
                else -> "${formatearDuracion(-adelanto)} después"
            },
            seAcaboAntes = adelanto >= MINUTO_EN_HORAS,
            textoConsumo = if (antes == despues) "$antes L/h" else "$antes → $despues L/h"
        )
    }

    companion object {
        fun desdeInyeccion(): SinAguaViewModel {
            val koin = KoinPlatform.getKoin()
            return SinAguaViewModel(koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
