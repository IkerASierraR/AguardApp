package com.example.aguardapp.feature.deposito.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.data.local.DepositoDao
import com.example.aguardapp.feature.deposito.data.mapper.aDatosDelHogar
import com.example.aguardapp.feature.deposito.data.mapper.aDominio
import com.example.aguardapp.feature.deposito.data.mapper.aEntidad
import com.example.aguardapp.feature.deposito.data.mapper.aHistorial
import com.example.aguardapp.feature.deposito.data.mapper.comoNovedad
import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.model.ConsumoHorario
import com.example.aguardapp.feature.deposito.domain.model.DatosDelHogar
import com.example.aguardapp.feature.deposito.domain.model.EventoLlenado
import com.example.aguardapp.feature.deposito.domain.model.LitrosPorHabitanteDia
import com.example.aguardapp.feature.deposito.domain.model.PerfilHogar
import com.example.aguardapp.feature.deposito.domain.model.PrevisualizacionSinAgua
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.TipoLlenado
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.usecase.ArmarDeposito
import com.example.aguardapp.feature.deposito.domain.usecase.CalcularLitrosPorHabitanteDia
import com.example.aguardapp.feature.deposito.domain.usecase.DeclararSinAgua
import com.example.aguardapp.feature.deposito.domain.usecase.HistorialDeposito
import com.example.aguardapp.feature.deposito.domain.usecase.ResultadoSinAgua

/** Lee y escribe solo en Room: la base local es la fuente de verdad (constitución, artículo II). */
class DepositoRepositoryImpl(
    private val dao: DepositoDao,
    private val usuarioId: String,
    private val ahora: () -> LocalDateTime,
    private val nuevoId: () -> String
) : DepositoRepository {

    private class Estado(val perfil: PerfilHogar, val historial: HistorialDeposito) {
        val hogar: DatosDelHogar get() = perfil.aDatosDelHogar()
    }

    override fun observarPerfil(): Flow<PerfilHogar?> = dao.observarPerfil(usuarioId).map { it?.aDominio() }

    override suspend fun guardarPerfil(configuracion: ConfiguracionHogar, consumoPorHabitos: ConsumoHorario?): Result<Unit> =
        runCatching {
            val perfil = PerfilHogar(
                usuarioId, configuracion.tipoReservorio, configuracion.capacidad,
                configuracion.habitantes, configuracion.habitos, consumoPorHabitos
            )
            dao.guardarPerfil(perfil.aEntidad())
        }

    override fun observarDeposito(): Flow<Deposito?> =
        combine(
            dao.observarPerfil(usuarioId),
            dao.observarLlenados(usuarioId),
            dao.observarNovedades(usuarioId)
        ) { perfil, llenados, novedades ->
            perfil?.aDominio()?.let { armar(Estado(it, aHistorial(it, llenados, novedades))) }
        }

    override suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia? {
        val estado = cargarEstado() ?: return null
        return CalcularLitrosPorHabitanteDia()(estado.historial.intervalos(estado.hogar), estado.hogar.habitantes)
    }

    override suspend fun registrarLlenado(momento: LocalDateTime, tipo: TipoLlenado): Result<Unit> =
        runCatching {
            require(momento <= ahora()) { "No se puede registrar un llenado en el futuro" }
            val estado = requireNotNull(cargarEstado()) { SIN_PERFIL }
            dao.guardarLlenado(EventoLlenado(momento, tipo).aEntidad(nuevoId(), usuarioId))
            dao.guardarPerfil(estado.perfil.copy(consumoVigente = null).aEntidad())
        }

    override suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit> =
        runCatching {
            val simulacion = simularSinAgua(momento)
            val resultado = simulacion.resultado
            dao.guardarNovedad(resultado.intervaloObservado.comoNovedad(nuevoId(), usuarioId, momento))
            dao.guardarPerfil(simulacion.estado.perfil.copy(consumoVigente = resultado.deposito.consumo).aEntidad())
        }

    override suspend fun previsualizarSinAgua(momento: LocalDateTime): Result<PrevisualizacionSinAgua> =
        runCatching {
            val simulacion = simularSinAgua(momento)
            PrevisualizacionSinAgua(
                agotamientoProyectado = simulacion.deposito.agotamientoProyectado(),
                momento = momento,
                consumoActual = simulacion.deposito.consumo,
                consumoNuevo = simulacion.resultado.deposito.consumo
            )
        }

    private class Simulacion(val estado: Estado, val deposito: Deposito, val resultado: ResultadoSinAgua)

    private suspend fun simularSinAgua(momento: LocalDateTime): Simulacion {
        require(momento <= ahora()) { "No se puede declarar en el futuro" }
        val estado = requireNotNull(cargarEstado()) { SIN_PERFIL }
        val deposito = checkNotNull(armar(estado)) { "Aún no hay un depósito que declarar sin agua" }
        val resultado = DeclararSinAgua()(deposito, estado.historial.intervalos(estado.hogar), momento)
        return Simulacion(estado, deposito, resultado)
    }

    private suspend fun cargarEstado(): Estado? {
        val perfil = dao.observarPerfil(usuarioId).first()?.aDominio() ?: return null
        val llenados = dao.observarLlenados(usuarioId).first()
        val novedades = dao.observarNovedades(usuarioId).first()
        return Estado(perfil, aHistorial(perfil, llenados, novedades))
    }

    private fun armar(estado: Estado): Deposito? = ArmarDeposito()(estado.hogar, estado.historial, ahora())

    private companion object {
        const val SIN_PERFIL = "Configura tu hogar antes de registrar movimientos"
    }
}
