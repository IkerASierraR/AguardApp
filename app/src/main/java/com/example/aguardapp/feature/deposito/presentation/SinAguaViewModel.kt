package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
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
    val guardando: Boolean = false,
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

    private var previsualizando: Job? = null

    private fun cargar() {
        viewModelScope.launch {
            ultimoLlenado = repositorio.observarDeposito().first()?.llenado?.momento
            previsualizar(ahora())
        }
    }

    private fun previsualizar(momento: LocalDateTime) {
        previsualizando?.cancel()
        previsualizando = viewModelScope.launch {
            val previsualizacion = repositorio.previsualizarSinAgua(momento)
            _uiState.update {
                it.copy(
                    cargando = false,
                    vista = previsualizacion.getOrNull()?.let(::armarVista) ?: it.vista,
                    error = previsualizacion.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onOpcionChange(opcion: OpcionSinAgua) {
        _uiState.update { it.copy(opcion = opcion, errorHora = null) }
        val momento = if (opcion == OpcionSinAgua.AHORA) ahora() else momentoValido(_uiState.value.horaTexto)
        momento?.let(::previsualizar)
    }

    fun onHoraChange(digitos: String) {
        val error = if (digitos.length == 4) validarHora(digitos) else null
        _uiState.update { it.copy(horaTexto = digitos, errorHora = error) }
        momentoValido(digitos)?.let(::previsualizar)
    }

    fun onDescartarError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onRegistrar() {
        val estado = _uiState.value
        if (estado.guardando) return
        if (estado.opcion == OpcionSinAgua.AHORA) {
            declarar(ahora())
            return
        }
        val error = validarHora(estado.horaTexto)
        val momento = momentoEscrito(estado.horaTexto)
        if (error != null || momento == null) {
            _uiState.update { it.copy(errorHora = error) }
            return
        }
        declarar(momento)
    }

    private fun momentoEscrito(digitos: String): LocalDateTime? {
        val hora = horaDesdeDigitos(digitos) ?: return null
        val actual = ahora()
        val hoy = LocalDateTime(actual.date, hora)
        return if (hoy <= actual) hoy else LocalDateTime(actual.date.minus(1, DateTimeUnit.DAY), hora)
    }

    private fun momentoValido(digitos: String): LocalDateTime? =
        momentoEscrito(digitos)?.takeIf { validarHora(digitos) == null }

    private fun validarHora(digitos: String): String? {
        if (digitos.length < 4) return "Escribe la hora completa"
        val hora = horaDesdeDigitos(digitos) ?: return "La hora no existe: usa de 00:00 a 23:59"
        val actual = ahora()
        val momento = momentoEscrito(digitos) ?: return null
        val llenado = ultimoLlenado
        if (llenado != null && momento <= llenado) {
            return if (LocalDateTime(actual.date, hora) > actual) {
                "Esa hora todavía no llega: ahora son las ${formatearHora(actual.time)}"
            } else {
                "Debe ser posterior a tu último llenado (${describirMomento(llenado, actual)})"
            }
        }
        return null
    }

    private fun declarar(momento: LocalDateTime) {
        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            repositorio.declararSinAgua(momento)
                .onSuccess { _uiState.update { it.copy(guardando = false, listo = true) } }
                .onFailure { falla -> _uiState.update { it.copy(guardando = false, error = falla.message) } }
        }
    }

    private fun armarVista(previsualizacion: PrevisualizacionSinAgua): SinAguaVista {
        val adelanto = previsualizacion.horasAntesDeLoPrevisto
        val antes = previsualizacion.consumoActual.litrosPorHora.roundToInt()
        val despues = previsualizacion.consumoNuevo.litrosPorHora.roundToInt()
        return SinAguaVista(
            textoProyectado = describirHora(previsualizacion.agotamientoProyectado, ahora()),
            textoSeAcabo = describirHora(previsualizacion.momento, ahora()),
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
