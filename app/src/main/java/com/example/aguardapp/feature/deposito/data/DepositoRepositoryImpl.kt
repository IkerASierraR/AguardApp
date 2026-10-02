package com.example.aguardapp.feature.deposito.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.data.local.DepositoDao
import com.example.aguardapp.feature.deposito.data.mapper.aDominio
import com.example.aguardapp.feature.deposito.data.mapper.aEntidad
import com.example.aguardapp.feature.deposito.data.mapper.aHistorial
import com.example.aguardapp.feature.deposito.data.mapper.comoNovedad
import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.Litros
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.PrevisualizacionSinAgua
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.usecase.ArmarDeposito
import com.example.aguardapp.feature.deposito.domain.usecase.DeclararSinAgua
import com.example.aguardapp.feature.deposito.domain.usecase.EstimarConsumo
import com.example.aguardapp.feature.deposito.domain.usecase.HistorialDeposito
import com.example.aguardapp.feature.deposito.domain.usecase.ResultadoSinAgua

class DepositoRepositoryImpl(
    private val dao: DepositoDao,
    private val usuarioId: String,
    private val ahora: () -> LocalDateTime,
    private val nuevoId: () -> String
) : DepositoRepository {

    private val estimar = EstimarConsumo()

    override fun observarPerfil(): Flow<PerfilHogar?> = dao.observarPerfil(usuarioId).map { it?.aDominio() }

    override suspend fun guardarPerfil(configuracion: ConfiguracionHogar): Result<Unit> = runCatching {
        val consumoPorHabitos = estimar.porHabitos(configuracion.habitos, configuracion.habitantes)
        dao.guardarPerfil(PerfilHogar(usuarioId, configuracion, consumoPorHabitos).aEntidad())
    }

    override fun observarDeposito(): Flow<Deposito?> =
        combine(dao.observarPerfil(usuarioId), dao.observarLlenados(usuarioId), dao.observarNovedades(usuarioId)) { perfil, llenados, novedades ->
            perfil?.aDominio()?.let { ArmarDeposito()(it, aHistorial(llenados, novedades), ahora()) }
        }

    override suspend fun litrosPorHabitanteDia(): Double? {
        val (perfil, historial) = cargar() ?: return null
        return estimar.litrosPorHabitanteDia(historial.intervalos(perfil), perfil.habitantes)
    }

    override suspend fun registrarLlenado(momento: LocalDateTime, litros: Litros): Result<Unit> = runCatching {
        require(momento <= ahora()) { "No se puede registrar un llenado en el futuro" }
        val (perfil, _) = requireNotNull(cargar()) { SIN_PERFIL }
        require(litros > Litros.CERO && litros <= perfil.capacidad.litros) { "Los litros deben ser mayores que 0 y no superar la capacidad" }
        dao.guardarLlenado(EventoLlenado(momento, litros).aEntidad(nuevoId(), usuarioId))
        dao.guardarPerfil(perfil.copy(consumoVigente = null).aEntidad())
    }

    override suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit> = runCatching {
        val (perfil, _, resultado) = simularSinAgua(momento)
        dao.guardarNovedad(resultado.intervaloObservado.comoNovedad(nuevoId(), usuarioId, momento))
        dao.guardarPerfil(perfil.copy(consumoVigente = resultado.deposito.consumo).aEntidad())
    }

    override suspend fun previsualizarSinAgua(momento: LocalDateTime): Result<PrevisualizacionSinAgua> = runCatching {
        val (_, deposito, resultado) = simularSinAgua(momento)
        PrevisualizacionSinAgua(deposito.agotamientoProyectado(), momento, deposito.consumo, resultado.deposito.consumo)
    }

    private suspend fun simularSinAgua(momento: LocalDateTime): Triple<PerfilHogar, Deposito, ResultadoSinAgua> {
        require(momento <= ahora()) { "No se puede declarar en el futuro" }
        val (perfil, historial) = requireNotNull(cargar()) { SIN_PERFIL }
        val deposito = checkNotNull(ArmarDeposito()(perfil, historial, ahora())) { "Aún no hay un depósito que declarar sin agua" }
        val resultado = DeclararSinAgua()(deposito, historial.intervalos(perfil), momento)
        return Triple(perfil, deposito, resultado)
    }

    private suspend fun cargar(): Pair<PerfilHogar, HistorialDeposito>? {
        val perfil = dao.observarPerfil(usuarioId).first()?.aDominio() ?: return null
        val historial = aHistorial(dao.observarLlenados(usuarioId).first(), dao.observarNovedades(usuarioId).first())
        return perfil to historial
    }

    private companion object {
        const val SIN_PERFIL = "Configura tu hogar antes de registrar movimientos"
    }
}
