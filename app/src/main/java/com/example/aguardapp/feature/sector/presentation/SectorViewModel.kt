package com.example.aguardapp.feature.sector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.sector.domain.model.ConfirmacionHorario
import com.example.aguardapp.feature.sector.domain.model.Coordenada
import com.example.aguardapp.feature.sector.domain.model.TipoConfirmacion
import com.example.aguardapp.feature.sector.domain.repository.SectorRepository
import com.example.aguardapp.feature.sector.domain.usecase.BuscarCisternasCercanas
import com.example.aguardapp.feature.sector.domain.usecase.ObtenerCronogramaVigente
import com.example.aguardapp.feature.sector.domain.usecase.ProximoAbastecimiento
import com.example.aguardapp.feature.sector.domain.usecase.ResolverSector
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.di.QUALIFICADOR_USUARIO
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.core.data.local.UsuarioDao

class SectorViewModel(
    private val repositorio: SectorRepository,
    private val ahora: () -> LocalDateTime,
    // UUID local e inmutable del usuario (constitución, art. III).
    private val usuarioId: String,
    private val obtenerSectorGuardado: suspend () -> String? = { null }
) : ViewModel() {

    private val _uiState = MutableStateFlow(SectorUiState())
    val uiState: StateFlow<SectorUiState> = _uiState.asStateFlow()

    private val resolverSector = ResolverSector()
    private val cronogramaVigente = ObtenerCronogramaVigente()
    private val proximoAbastecimiento = ProximoAbastecimiento()
    private val buscarCisternas = BuscarCisternasCercanas()

    init {
        cargarSector()
    }

    private fun cargarSector() {
        viewModelScope.launch {
            try {
                val momento = ahora()
                val sectores = repositorio.obtenerSectores()
                // La casa y el sector salen de lo que el usuario confirmó en Registrar domicilio.
                val casaGuardada = repositorio.obtenerUbicacionCasa()
                val guardado = obtenerSectorGuardado()
                val sector = sectores.firstOrNull { it.id == guardado }
                    ?: casaGuardada?.let { resolverSector.resolver(it, sectores) }
                if (sector == null) {
                    _uiState.update { it.copy(cargando = false) }
                    return@launch
                }
                // Quien registró su sector antes de que se guardara la casa ve el centro del sector.
                val ubicacionCasa = casaGuardada ?: sector.centro
                val cronogramas = repositorio.obtenerCronogramas(sector.id)
                val puntos = sectores.flatMap { repositorio.obtenerPuntosCisterna(it.id) }
                _uiState.value = SectorUiState(
                    cargando = false,
                    ahora = momento,
                    sector = sector,
                    ubicacionCasa = ubicacionCasa,
                    cronogramaDeHoy = cronogramas.firstOrNull { it.fecha == momento.date },
                    aguaLlegandoAhora = cronogramaVigente.obtener(cronogramas, momento) != null,
                    proximoAbastecimiento = proximoAbastecimiento.calcular(cronogramas, momento),
                    cisternas = buscarCisternas.buscar(puntos, ubicacionCasa, RADIO_CISTERNAS_KM),
                    confirmacionesDeHoy = repositorio.obtenerConfirmaciones(sector.id, momento.date).size
                )
            } catch (e: Exception) {
                // Sin conexión o error del servidor: no dejamos la pantalla colgada en el spinner.
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun confirmar(tipo: TipoConfirmacion) {
        val sector = _uiState.value.sector ?: return
        viewModelScope.launch {
            try {
                val momento = ahora()
                repositorio.registrarConfirmacion(
                    ConfirmacionHorario(
                        id = "$tipo-$momento",
                        sectorId = sector.id,
                        usuarioId = usuarioId,
                        momento = momento,
                        tipo = tipo
                    )
                )
                val total = repositorio.obtenerConfirmaciones(sector.id, momento.date).size
                _uiState.update {
                    it.copy(confirmacionesDeHoy = total, mensaje = "Gracias, registramos tu confirmación")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = "No pudimos registrar tu confirmación. Revisa tu conexión.") }
            }
        }
    }

    companion object {
        private const val RADIO_CISTERNAS_KM = 10.0

        // La app real: el repositorio (Room + Supabase) viene de Koin; sin Koin (preview) cae al fake.
        fun desdeInyeccion(): SectorViewModel {
            val koin = KoinPlatform.getKoin()
            val usuarioDao = koin.get<UsuarioDao>()
            return SectorViewModel(koin.get(), koin.get<Reloj>()::ahora, koin.get(QUALIFICADOR_USUARIO)) {
                usuarioDao.obtener()?.sectorId
            }
        }
    }
}
