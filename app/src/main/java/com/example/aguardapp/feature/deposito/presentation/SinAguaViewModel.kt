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

/** Las dos respuestas posibles a "¿cuándo se acabó el agua?". */
enum class OpcionSinAgua { AHORA, ANTES }

data class SinAguaUiState(
    val cargando: Boolean = true,
    val vista: SinAguaVista? = null,
    val opcion: OpcionSinAgua = OpcionSinAgua.AHORA,
    /** La hora que escribe el usuario cuando elige "Se acabó antes", en formato HH:mm. */
    val horaTexto: String = "",
    /** Error debajo del campo de hora; `null` si está bien. */
    val errorHora: String? = null,
    val error: String? = null,
    /** `true` cuando ya se registró y la pantalla puede cerrarse. */
    val listo: Boolean = false
)

/** Lo que compara la pantalla "Te quedaste sin agua": lo proyectado frente a lo que pasó. */
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

    // El último llenado: la hora en que se acabó el agua tiene que ser posterior.
    private var ultimoLlenado: LocalDateTime? = null

    init {
        cargar()
    }

    // Al abrir la pantalla: calcula qué cambiaría si se acabó el agua ahora.
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

    /** El usuario escribe la hora: solo dígitos y ":" (máximo "HH:mm"). */
    fun onHoraChange(texto: String) {
        val limpio = texto.filter { it.isDigit() || it == ':' }.take(5)
        _uiState.update { it.copy(horaTexto = limpio, errorHora = null) }
    }

    fun onDescartarError() {
        _uiState.update { it.copy(error = null) }
    }

    /** Registra que se acabó el agua: ahora mismo, o a la hora escrita si es válida. */
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

    // Reglas del campo de hora: formato HH:mm, no futura y posterior al último llenado.
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

    // Compara lo que se proyectaba con lo que pasó, en textos listos para la pantalla.
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
