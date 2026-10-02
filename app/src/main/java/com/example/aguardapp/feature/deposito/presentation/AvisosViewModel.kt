package com.example.aguardapp.feature.deposito.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.PlanRecortes
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.usecase.CalcularDeficit
import com.example.aguardapp.feature.deposito.domain.usecase.CalcularRecortes
import kotlin.math.roundToInt

private const val PORCENTAJE_NIVEL_BAJO = 20

enum class TipoDeAviso { DEFICIT, NIVEL_BAJO, SIN_LLENADO }

sealed interface DestinoDelAviso {
    data object RegistrarLlenado : DestinoDelAviso
    data object QueRecortar : DestinoDelAviso
    data object MiDeposito : DestinoDelAviso
}

data class AvisoVista(
    val tipo: TipoDeAviso,
    val titulo: String,
    val texto: String,
    val destino: DestinoDelAviso
)

data class AvisosUiState(
    val cargando: Boolean = true,
    val avisos: List<AvisoVista> = emptyList()
)

class AvisosViewModel(
    private val repositorio: DepositoRepository,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvisosUiState())
    val uiState: StateFlow<AvisosUiState> = _uiState.asStateFlow()

    private val calcularDeficit = CalcularDeficit()
    private val calcularRecortes = CalcularRecortes()

    init {
        viewModelScope.launch {
            combine(repositorio.observarPerfil(), repositorio.observarDeposito(), repositorio.observarPlanRecortes(), ::armarAvisos)
                .collect { avisos -> _uiState.value = AvisosUiState(cargando = false, avisos = avisos) }
        }
    }

    private fun armarAvisos(perfil: PerfilHogar?, deposito: Deposito?, plan: PlanRecortes?): List<AvisoVista> {
        if (perfil == null) return emptyList()
        if (deposito == null) return listOf(avisoSinLlenado())
        val momento = ahora()
        val proximoLlenado = calcularDeficit.proximoLlenado(perfil.configuracion.horaProximoLlenado, momento)
        val recortes = calcularRecortes.evaluar(perfil, deposito, plan, momento)
        val deficit = recortes.litrosQueFaltan
        val nivel = deposito.nivelEn(momento)
        return buildList {
            if (deficit > 0) {
                add(
                    AvisoVista(
                        tipo = TipoDeAviso.DEFICIT,
                        titulo = "Tu reserva se agota antes de que vuelva el agua",
                        texto = "Se acaba ${describirMomento(deposito.agotamientoProyectado(), momento)} y el agua llega " +
                            "${describirMomento(proximoLlenado, momento)}, te faltan ${formatearMiles(deficit)} L" +
                            if (recortes.tieneRecortes) " aun con tus recortes." else ".",
                        destino = DestinoDelAviso.QueRecortar
                    )
                )
            }
            if (nivel.porcentaje < PORCENTAJE_NIVEL_BAJO) {
                add(
                    AvisoVista(
                        tipo = TipoDeAviso.NIVEL_BAJO,
                        titulo = "Te queda poca agua",
                        texto = "Quedan ${formatearMiles(nivel.litros.valor.roundToInt())} L, " +
                            "el ${nivel.porcentaje.roundToInt()} % de tu tanque.",
                        destino = DestinoDelAviso.MiDeposito
                    )
                )
            }
        }
    }

    private fun avisoSinLlenado() = AvisoVista(
        tipo = TipoDeAviso.SIN_LLENADO,
        titulo = "¿Llegó el agua a tu casa?",
        texto = "Registra el llenado de tu tanque para saber cuánto te queda.",
        destino = DestinoDelAviso.RegistrarLlenado
    )

    companion object {
        fun desdeInyeccion(): AvisosViewModel {
            val koin = KoinPlatform.getKoin()
            return AvisosViewModel(koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
