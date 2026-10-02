package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.Recomendacion
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.usecase.CalcularDeficit
import com.example.aguardapp.feature.deposito.domain.usecase.CalcularRecortes
import com.example.aguardapp.feature.deposito.domain.usecase.RecorteSugerido
import com.example.aguardapp.feature.deposito.domain.usecase.SituacionRecortes
import org.koin.mp.KoinPlatform

data class QueRecortarUiState(
    val cargando: Boolean = true,
    val deficitLitros: Int = 0,
    val textoProximoLlenado: String = "",
    val sugeridos: List<RecorteSugerido> = emptyList(),
    val elegidas: Set<Recomendacion> = emptySet(),
    val litrosGanados: Int = 0,
    val litrosQueFaltan: Int = 0,
    val error: String? = null
) {
    val cubreElDeficit: Boolean get() = litrosQueFaltan == 0
}

class QueRecortarViewModel(
    private val repositorio: DepositoRepository,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val calcularDeficit = CalcularDeficit()
    private val recortes = CalcularRecortes(calcularDeficit)

    private val _uiState = MutableStateFlow(QueRecortarUiState())
    val uiState: StateFlow<QueRecortarUiState> = _uiState.asStateFlow()

    private var perfil: PerfilHogar? = null
    private var deposito: Deposito? = null

    init {
        viewModelScope.launch {
            combine(repositorio.observarPerfil(), repositorio.observarDeposito(), repositorio.observarPlanRecortes()) { perfil, deposito, plan ->
                this@QueRecortarViewModel.perfil = perfil
                this@QueRecortarViewModel.deposito = deposito
                if (perfil == null || deposito == null) null else recortes.evaluar(perfil, deposito, plan, ahora())
            }.collect { situacion -> publicar(situacion) }
        }
    }

    fun onAlternar(recomendacion: Recomendacion) {
        val perfil = perfil ?: return
        val deposito = deposito ?: return
        val actuales = _uiState.value.elegidas
        val elegidas = if (recomendacion in actuales) actuales - recomendacion else actuales + recomendacion
        _uiState.update { estado ->
            val situacion = SituacionRecortes(estado.deficitLitros, estado.sugeridos, elegidas)
            estado.copy(elegidas = elegidas, litrosGanados = situacion.litrosGanados, litrosQueFaltan = situacion.litrosQueFaltan)
        }
        viewModelScope.launch {
            repositorio.guardarPlanRecortes(recortes.nuevoPlan(perfil, deposito, elegidas, ahora()))
                .onFailure { falla -> _uiState.update { it.copy(error = falla.message) } }
        }
    }

    fun onDescartarError() = _uiState.update { it.copy(error = null) }

    private fun publicar(situacion: SituacionRecortes?) {
        val perfil = perfil
        if (situacion == null || perfil == null) {
            _uiState.update { it.copy(cargando = false) }
            return
        }
        val momento = ahora()
        val proximoLlenado = calcularDeficit.proximoLlenado(perfil.configuracion.horaProximoLlenado, momento)
        _uiState.update {
            it.copy(
                cargando = false,
                deficitLitros = situacion.deficitLitros,
                textoProximoLlenado = describirMomento(proximoLlenado, momento),
                sugeridos = situacion.sugeridos,
                elegidas = situacion.elegidas,
                litrosGanados = situacion.litrosGanados,
                litrosQueFaltan = situacion.litrosQueFaltan
            )
        }
    }

    companion object {
        fun desdeInyeccion(): QueRecortarViewModel {
            val koin = KoinPlatform.getKoin()
            return QueRecortarViewModel(koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
