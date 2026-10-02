package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.usecase.CalcularDeficit
import kotlin.math.roundToInt

data class DepositoUiState(
    val cargando: Boolean = true,
    val vista: DepositoVista? = null
)

data class DepositoVista(
    val saludo: String,
    val subtituloHogar: String,
    val nivelLitros: Int,
    val capacidadLitros: Int,
    val porcentaje: Int,
    val textoLlenado: String,
    val textoAgotamiento: String,
    val textoProximoLlenado: String,
    val deficitLitros: Int,
    val consumoLitrosPorHora: Int,
    val litrosPorHabitanteDia: Int?
)

class DepositoViewModel(
    private val repositorio: DepositoRepository,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val _uiState = MutableStateFlow(DepositoUiState())
    val uiState: StateFlow<DepositoUiState> = _uiState.asStateFlow()

    private val calcularDeficit = CalcularDeficit()

    init {
        val cadaMinuto = flow {
            while (true) {
                emit(Unit)
                delay(60_000L)
            }
        }
        viewModelScope.launch {
            combine(repositorio.observarPerfil(), repositorio.observarDeposito(), cadaMinuto) { perfil, deposito, _ -> perfil to deposito }
                .collect { (perfil, deposito) -> publicar(perfil, deposito) }
        }
    }

    private suspend fun publicar(perfil: PerfilHogar?, deposito: Deposito?) {
        val vista = if (perfil != null && deposito != null) armarVista(perfil, deposito) else null
        _uiState.value = DepositoUiState(cargando = false, vista = vista)
    }

    private suspend fun armarVista(perfil: PerfilHogar, deposito: Deposito): DepositoVista {
        val momento = ahora()
        val nivel = deposito.nivelEn(momento)
        val habitantes = perfil.habitantes.cantidad
        val proximoLlenado = calcularDeficit.proximoLlenado(perfil.configuracion.horaProximoLlenado, momento)
        return DepositoVista(
            saludo = saludoPara(momento.hour),
            subtituloHogar = if (habitantes == 1) "1 persona" else "$habitantes personas",
            nivelLitros = nivel.litros.valor.roundToInt(),
            capacidadLitros = deposito.capacidad.litros.valor.roundToInt(),
            porcentaje = nivel.porcentaje.roundToInt(),
            textoLlenado = "${nombreDelTipo(perfil.configuracion.tipoReservorio)} · último llenado ${describirMomento(deposito.llenado.momento, momento)}",
            textoAgotamiento = describirMomento(deposito.agotamientoProyectado(), momento),
            textoProximoLlenado = describirMomento(proximoLlenado, momento),
            deficitLitros = calcularDeficit.litrosQueFaltan(deposito, proximoLlenado, momento),
            consumoLitrosPorHora = deposito.consumo.litrosPorHora.roundToInt(),
            litrosPorHabitanteDia = repositorio.litrosPorHabitanteDia()?.roundToInt()
        )
    }

    companion object {
        fun desdeInyeccion(): DepositoViewModel {
            val koin = KoinPlatform.getKoin()
            return DepositoViewModel(koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
