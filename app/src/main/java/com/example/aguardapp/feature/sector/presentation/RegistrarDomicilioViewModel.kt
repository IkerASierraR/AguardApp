package com.example.aguardapp.feature.sector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.aguardapp.feature.sector.domain.model.Coordenada
import com.example.aguardapp.feature.sector.domain.repository.SectorRepository
import com.example.aguardapp.feature.sector.domain.usecase.ResolverSector
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.data.local.UsuarioDao

class RegistrarDomicilioViewModel(
    private val repositorio: SectorRepository,
    private val guardarSectorEnUsuario: suspend (String) -> Unit = {}
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrarDomicilioUiState())
    val uiState: StateFlow<RegistrarDomicilioUiState> = _uiState.asStateFlow()

    private val resolverSector = ResolverSector()

    // No se detecta nada al abrir: el usuario elige primero su ubicación (GPS o pin en el mapa),
    // y solo entonces se resuelve el sector y se habilita "Confirmar".

    /** Llega una ubicación real: del GPS o de un punto marcado en el mapa. */
    fun marcarEnMapa(coordenada: Coordenada) = elegir(coordenada)

    fun buscandoUbicacion() {
        _uiState.update { it.copy(mensaje = "Buscando tu ubicación… puede tardar unos segundos.", mensajeEsError = false) }
    }

    /** El GPS no dio una ubicación: se explica la causa en vez de inventar una. */
    fun avisarError(texto: String) {
        _uiState.update { it.copy(mensaje = texto, mensajeEsError = true) }
    }

    private fun elegir(coord: Coordenada) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, ubicacion = coord, mensaje = null) }
            try {
                val sector = resolverSector.resolver(coord, repositorio.obtenerSectores())
                if (sector == null) {
                    _uiState.update { it.copy(cargando = false, sector = null, continuidad = "", etiquetaMapa = "SECTOR") }
                    return@launch
                }
                val minutos = repositorio.obtenerCronogramas(sector.id).firstOrNull()?.duracionMinutos
                _uiState.update {
                    it.copy(
                        cargando = false,
                        sector = sector,
                        continuidad = minutos?.let { m -> "${duracionATexto(m)}/día" } ?: "Sin horario",
                        etiquetaMapa = "SECTOR ${sector.id.substringAfterLast('-')}"
                    )
                }
            } catch (e: Exception) {
                // Sin conexión o error del servidor: no dejamos la pantalla colgada.
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    // Guarda el sector detectado en el usuario local (Room). No necesita internet ni sesión.
    fun confirmarSector() {
        val sector = _uiState.value.sector ?: return
        val casa = _uiState.value.ubicacion ?: return
        viewModelScope.launch {
            try {
                // Primero la casa: al guardar el sector la app sale de esta pantalla.
                repositorio.guardarUbicacionCasa(casa)
                guardarSectorEnUsuario(sector.id)
                _uiState.update { it.copy(guardado = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardado = false) }
            }
        }
    }

    companion object {
        fun desdeInyeccion(): RegistrarDomicilioViewModel {
            val koin = KoinPlatform.getKoin()
            return RegistrarDomicilioViewModel(koin.get(), koin.get<UsuarioDao>()::guardarSector)
        }
    }
}
