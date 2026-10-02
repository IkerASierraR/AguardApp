package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.aguardapp.feature.deposito.domain.model.Recomendacion
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.usecase.CalcularRecortes
import org.koin.mp.KoinPlatform

data class QueRecortarUiState(
    val deficitLitros: Int = 0,
    val recomendaciones: List<Recomendacion> = emptyList(),
    val elegidas: Set<Recomendacion> = emptySet(),
    val litrosGanados: Int = 0,
    val litrosQueFaltan: Int = 0
) {
    val cubreElDeficit: Boolean get() = litrosQueFaltan == 0
}

class QueRecortarViewModel(
    private val repositorio: DepositoRepository,
    deficitLitros: Int
) : ViewModel() {

    private val recortes = CalcularRecortes()

    private val _uiState = MutableStateFlow(QueRecortarUiState(deficitLitros = deficitLitros, litrosQueFaltan = deficitLitros))
    val uiState: StateFlow<QueRecortarUiState> = _uiState.asStateFlow()

    init {
        cargarRecomendaciones()
    }

    private fun cargarRecomendaciones() {
        viewModelScope.launch {
            val perfil = repositorio.observarPerfil().first() ?: return@launch
            _uiState.update { it.copy(recomendaciones = recortes.sugerir(perfil.configuracion.habitos)) }
        }
    }

    fun onAlternar(recomendacion: Recomendacion) {
        _uiState.update { estado ->
            val elegidas = if (recomendacion in estado.elegidas) {
                estado.elegidas - recomendacion
            } else {
                estado.elegidas + recomendacion
            }
            estado.copy(
                elegidas = elegidas,
                litrosGanados = recortes.litrosGanados(elegidas),
                litrosQueFaltan = recortes.litrosQueFaltan(estado.deficitLitros, elegidas)
            )
        }
    }

    companion object {
        fun desdeInyeccion(deficitLitros: Int): QueRecortarViewModel =
            QueRecortarViewModel(KoinPlatform.getKoin().get(), deficitLitros)
    }
}
