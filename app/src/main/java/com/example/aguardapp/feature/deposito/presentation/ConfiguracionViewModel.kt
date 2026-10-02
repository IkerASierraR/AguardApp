package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.koin.mp.KoinPlatform
import com.example.aguardapp.feature.deposito.domain.model.CapacidadLitros
import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.model.Habitantes
import com.example.aguardapp.feature.deposito.domain.model.HabitosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.TipoReservorio
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.usecase.EstimarConsumo
import kotlin.math.roundToInt

const val CAPACIDAD_MINIMA_LITROS = 200
const val CAPACIDAD_MAXIMA_LITROS = 5000
const val PASO_CAPACIDAD_LITROS = 50
private const val HABITANTES_MAXIMOS = 12
private const val DUCHAS_MAXIMAS = 6

data class ConfiguracionUiState(
    val tipo: TipoReservorio = TipoReservorio.TANQUE_ELEVADO,
    val capacidadLitros: Int = 1000,
    val habitantes: Int = 4,
    val duchasPorDia: Int = 2,
    val usaLavadora: Boolean = true,
    val riegaJardin: Boolean = false,
    val horaProximoLlenado: LocalTime? = null,
    val consumoEstimadoLitrosPorHora: Int = 0,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null
) {

    val errorCapacidad: String?
        get() = if (capacidadLitros in CAPACIDAD_MINIMA_LITROS..CAPACIDAD_MAXIMA_LITROS) null
        else "La capacidad debe estar entre $CAPACIDAD_MINIMA_LITROS y $CAPACIDAD_MAXIMA_LITROS L"

    val errorHabitantes: String?
        get() = if (habitantes in 1..HABITANTES_MAXIMOS) null else "Los habitantes deben estar entre 1 y $HABITANTES_MAXIMOS"

    val errorHora: String?
        get() = if (horaProximoLlenado != null) null else "Elige la hora a la que suele llegar el agua"

    val puedeGuardar: Boolean
        get() = errorCapacidad == null && errorHabitantes == null && errorHora == null && !guardando
}

class ConfiguracionViewModel(private val repositorio: DepositoRepository) : ViewModel() {

    private val estimar = EstimarConsumo()

    private val _uiState = MutableStateFlow(conConsumo(ConfiguracionUiState()))
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    init {
        cargarPerfilGuardado()
    }

    private fun cargarPerfilGuardado() {
        viewModelScope.launch {
            val configuracion = repositorio.observarPerfil().first()?.configuracion ?: return@launch
            _uiState.update {
                conConsumo(
                    it.copy(
                        tipo = configuracion.tipoReservorio,
                        capacidadLitros = configuracion.capacidad.litros.valor.roundToInt(),
                        habitantes = configuracion.habitantes.cantidad,
                        duchasPorDia = configuracion.habitos.duchasPorDia,
                        usaLavadora = configuracion.habitos.usaLavadora,
                        riegaJardin = configuracion.habitos.riegaJardin,
                        horaProximoLlenado = configuracion.horaProximoLlenado
                    )
                )
            }
        }
    }

    fun onTipoChange(tipo: TipoReservorio) = _uiState.update { it.copy(tipo = tipo) }

    fun onCapacidadChange(litros: Int) =
        _uiState.update { it.copy(capacidadLitros = litros.coerceIn(CAPACIDAD_MINIMA_LITROS, CAPACIDAD_MAXIMA_LITROS)) }

    fun onHabitantesChange(diferencia: Int) =
        _uiState.update { conConsumo(it.copy(habitantes = (it.habitantes + diferencia).coerceIn(1, HABITANTES_MAXIMOS))) }

    fun onDuchasChange(diferencia: Int) =
        _uiState.update { conConsumo(it.copy(duchasPorDia = (it.duchasPorDia + diferencia).coerceIn(0, DUCHAS_MAXIMAS))) }

    fun onLavadoraChange() = _uiState.update { conConsumo(it.copy(usaLavadora = !it.usaLavadora)) }

    fun onRiegoChange() = _uiState.update { conConsumo(it.copy(riegaJardin = !it.riegaJardin)) }

    fun onHoraChange(hora: LocalTime) = _uiState.update { it.copy(horaProximoLlenado = hora) }

    fun onDescartarError() = _uiState.update { it.copy(error = null) }

    fun onGuardar() {
        val estado = _uiState.value
        val hora = estado.horaProximoLlenado
        if (!estado.puedeGuardar || hora == null) return
        val configuracion = ConfiguracionHogar(
            tipoReservorio = estado.tipo,
            capacidad = CapacidadLitros.deLitros(estado.capacidadLitros.toDouble()),
            habitantes = Habitantes(estado.habitantes),
            habitos = HabitosDelHogar(estado.duchasPorDia, estado.usaLavadora, estado.riegaJardin),
            horaProximoLlenado = hora
        )
        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val resultado = repositorio.guardarPerfil(configuracion)
            _uiState.update { it.copy(guardando = false, guardado = resultado.isSuccess, error = resultado.exceptionOrNull()?.message) }
        }
    }

    private fun conConsumo(estado: ConfiguracionUiState): ConfiguracionUiState {
        val habitos = HabitosDelHogar(estado.duchasPorDia, estado.usaLavadora, estado.riegaJardin)
        val consumo = estimar.porHabitos(habitos, Habitantes(estado.habitantes))
        return estado.copy(consumoEstimadoLitrosPorHora = consumo.litrosPorHora.roundToInt())
    }

    companion object {
        fun desdeInyeccion(): ConfiguracionViewModel = ConfiguracionViewModel(KoinPlatform.getKoin().get())
    }
}
