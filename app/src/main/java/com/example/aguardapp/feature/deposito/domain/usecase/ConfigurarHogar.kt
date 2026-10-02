package com.example.aguardapp.feature.deposito.domain.usecase

import com.example.aguardapp.feature.deposito.domain.model.ConfiguracionHogar
import com.example.aguardapp.feature.deposito.domain.repository.EstimadorPorHabitos
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository

/** Guarda la configuración del hogar junto con el consumo que se estima de sus hábitos. */
class ConfigurarHogar(
    private val estimador: EstimadorPorHabitos,
    private val repositorio: DepositoRepository
) {
    suspend operator fun invoke(configuracion: ConfiguracionHogar): Result<Unit> {
        val consumo = estimador.estimar(configuracion.habitos, configuracion.habitantes)
        return repositorio.guardarPerfil(configuracion, consumo)
    }
}
